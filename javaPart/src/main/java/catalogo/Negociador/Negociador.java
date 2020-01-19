package catalogo.Negociador;

import Logic.Encomenda;
import Logic.Periodo;
import Logic.Producao;
import ProtoBuffers.Protos.OperationResponse;
import ProtoBuffers.Protos.OperationRequest;
import ProtoBuffers.Protos.OfertaProducaoRequest;
import ProtoBuffers.Protos.OfertaEncomendaRequest;
import ProtoBuffers.Protos.NotificacaoResultadosFabricante;
import ProtoBuffers.Protos.NotificacaoResultadosImportador;
import ProtoBuffers.Protos.NotificacaoOfertaProducao;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;

public class Negociador {


    private static final JsonHttpClient jhc = new JsonHttpClient("localhost:12345");
    private static ZMQ.Socket socketPUB;

    public static void main(String[] args) {
        try (ZContext context = new ZContext()) {
            final ZMQ.Socket socketREP = context.createSocket(ZMQ.REP);
            socketREP.bind("tcp://*:5555");
            socketPUB = context.createSocket(ZMQ.PUB);
            socketPUB.bind("tcp://*:5561");

            OperationResponse reply;
            while (!Thread.currentThread().isInterrupted()) {
                byte[] data = socketREP.recv(0);
                try {
                    OperationRequest request = OperationRequest.parseFrom(data);
                    String nome = request.getNome();
                    System.out.println("Received from " + nome);

                    switch (request.getRequestCase().getNumber()){
                        case OperationRequest.PRODUCAO_FIELD_NUMBER: // OFERTA DE PRODUÇÃO
                            OfertaProducaoRequest prodRequest = request.getProducao();
                            Producao producao = requestToObj(prodRequest, nome);

                            // avisa os importadores subscritos
                            NotificacaoOfertaProducao notif = prodRequestToNotif(prodRequest);
                            socketPUB.sendMore(nome);
                            socketPUB.send(notif.toByteArray());

                            // cria thread que dorme até acabar o período de negociação
                            // quando acorda determina encomendas aceites e notifica participantes
                            long waitSeconds = prodRequest.getDuracaoS();
                            periodoNegociacao(waitSeconds, producao, nome).start();

                            reply = processProducao(producao);
                            break;
                        case OperationRequest.ENCOMENDA_FIELD_NUMBER: // OFERTA DE ENCOMENDA
                            Encomenda encomenda = requestToObj(request.getEncomenda(), nome);
                            reply = processEncomenda(encomenda);
                            break;
                        default:
                            reply = REPLY_INVALID;
                    }
                } catch (IOException | InterruptedException e) {
                    System.err.println(e.toString());
                    reply = REPLY_INVALID;
                }
                socketREP.send(reply.toByteArray(), 0);
            }
        }
    }


    private static OperationResponse processProducao(Producao producao) throws IOException, InterruptedException {
        // guarda oferta de produção no catalogo
        int statusCode = jhc.post("producoes/" + producao.getNomeFabricante() + "/" + producao.getNomeProduto(), producao);
        if (statusCode >= 200 && statusCode < 300) {
            return REPLY_OK;
        }
        return REPLY_INVALID;
    }

    private static OperationResponse processEncomenda(Encomenda encomenda) throws IOException, InterruptedException {
        // vai buscar oferta ao catalogo
        Producao prod = jhc.getObject("producoes", Producao.class);
        if(prod != null) {
            if(encomenda.getPrecoPorUnidade() < prod.getPrecoPorUnidade()) {
                // nao atinge preco minimo
                return REPLY_INVALID;
            }
            if(encomenda.getQuantidade() > prod.getQuantidadeMax()) {
                // quantidade demasiado grande
                return REPLY_INVALID;
            }
            // tudo ok, envia para catalogo
            int statusCode = jhc.post("encomendas", encomenda);
            if (statusCode >= 200 && statusCode < 300) {
                return REPLY_OK;
            }
            return REPLY_INVALID;
        }
        else {
            // não existem ofertas de produção
            return REPLY_INVALID;
        }
    }

    private static final OperationResponse REPLY_OK = OperationResponse.newBuilder()
            .setCode(OperationResponse.ResponseStatusCode.OK)
            .build();

    private static final OperationResponse REPLY_INVALID = OperationResponse.newBuilder()
            .setCode(OperationResponse.ResponseStatusCode.INVALID)
            .build();


    private static Thread periodoNegociacao(long waitSeconds, Producao producao, String nomeFabricante) {
        return new Thread(() -> {
            try {
                Thread.sleep(1000 * waitSeconds);
                // vai buscar ofertas ao catalogo
                Collection<Encomenda> encomendas = jhc.getCollection(
                        "producoes/" + producao.getNomeFabricante() + "/" + producao.getNomeProduto() + "/encomendas", Encomenda.class);
                // Heurística da razão para determinar encomendas aceites
                ArrayList<Encomenda> list = new ArrayList<>(encomendas);
                list.sort((Encomenda a,Encomenda b) -> {
                    float ratioA = (float) a.getPrecoPorUnidade() / a.getQuantidade();
                    float ratioB = (float) b.getPrecoPorUnidade() / b.getQuantidade();
                    return Float.compare(ratioA, ratioB);
                });
                int quantMin = producao.getQuantidadeMin();
                int quantMax = producao.getQuantidadeMax();
                int quantTotal = 0;
                int naceites = 0;
                for (Encomenda e : list) {
                    int quant = e.getQuantidade();
                    if (quant > quantMax) continue;
                    if (quantTotal + quant > quantMax) break;
                    quantTotal += quant;
                    naceites++;
                }
                if(quantTotal < quantMin) {
                    // mínimo não atingido -> cancelada

                    // notifica importadores participantes
                    for (Encomenda e : encomendas) {
                        NotificacaoResultadosImportador n = encomendaToNotif(e, false);
                        socketPUB.sendMore(e.getNomeImportador());
                        socketPUB.send(n.toByteArray());
                    }

                    // notifica fabricante
                    NotificacaoResultadosFabricante n = NotificacaoResultadosFabricante.newBuilder().build();
                    socketPUB.sendMore(nomeFabricante);
                    socketPUB.send(n.toByteArray());

                    // guarda no catalogo
                    // TODO guardar cancelada no catalogo

                } else {
                    // mínimo atingido, encomendas aceites

                    // notifica importadores participantes
                    int j = 0;
                    for (Encomenda e : list) {
                        if(j < naceites) { // encomenda aceite
                            NotificacaoResultadosImportador n = encomendaToNotif(e, true);
                            socketPUB.sendMore(e.getNomeImportador());
                            socketPUB.send(n.toByteArray());
                            j++;
                        } else { // encomenda não aceite
                            NotificacaoResultadosImportador n = encomendaToNotif(e, false);
                            socketPUB.sendMore(e.getNomeImportador());
                            socketPUB.send(n.toByteArray());
                        }
                    }

                    // notifica fabricante, envia lista de encomendas aceites
                    ArrayList<OfertaEncomendaRequest> aceites = new ArrayList<>(naceites);
                    for (Encomenda e : list.subList(0, naceites)) {
                        aceites.add(OfertaEncomendaRequest.newBuilder()
                                .setFabricante(e.getNomeFabricante())
                                .setPreco(e.getPrecoPorUnidade())
                                .setProduto(e.getNomeProduto())
                                .setQuant(e.getQuantidade())
                                .build()
                        );
                    }
                    NotificacaoResultadosFabricante n = NotificacaoResultadosFabricante.newBuilder()
                            .addAllEncomendas(aceites)
                            .build();
                    socketPUB.sendMore(nomeFabricante);
                    socketPUB.send(n.toByteArray());

                    // guarda resultado no catalogo
                    // TODO guardar resultado para catalogo
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    private static NotificacaoResultadosImportador encomendaToNotif(Encomenda e, boolean aceite) {
        return NotificacaoResultadosImportador.newBuilder()
                .setAceite(aceite)
                .setFabricante(e.getNomeFabricante())
                .setPreco(e.getPrecoPorUnidade())
                .setProduto(e.getNomeProduto())
                .setQuant(e.getQuantidade())
                .build();
    }

    private static NotificacaoOfertaProducao prodRequestToNotif(OfertaProducaoRequest prodRequest) {
        LocalDateTime inicio = LocalDateTime.now();
        LocalDateTime fim = inicio.plusSeconds(prodRequest.getDuracaoS());
        return NotificacaoOfertaProducao.newBuilder()
                .setProduto(prodRequest.getProduto())
                .setQuantMax(prodRequest.getQuantMax())
                .setQuantMin(prodRequest.getQuantMin())
                .setPrecoUniMin(prodRequest.getPrecoUniMin())
                .setDataInicial(inicio.format(DateTimeFormatter.ISO_DATE_TIME))
                .setDataFinal(fim.format(DateTimeFormatter.ISO_DATE_TIME))
                .build();
    }

    private static Encomenda requestToObj(OfertaEncomendaRequest r, String nome) {
        return new Encomenda(nome, r.getFabricante(), r.getProduto(), r.getQuant(), r.getPreco());
    }

    private static Producao requestToObj(OfertaProducaoRequest r, String nome) {
        LocalDateTime inicio = LocalDateTime.now();
        LocalDateTime fim = inicio.plusSeconds(r.getDuracaoS());
        return new Producao(nome, r.getProduto(), r.getQuantMin(), r.getQuantMax(), r.getPrecoUniMin(), new Periodo(inicio, fim));
    }
}
