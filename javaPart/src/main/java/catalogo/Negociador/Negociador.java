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
import java.util.Collection;

public class Negociador {

    private static final OperationResponse REPLY_OK = OperationResponse.newBuilder()
            .setCode(OperationResponse.ResponseStatusCode.OK)
            .build();

    private static final OperationResponse REPLY_INVALID = OperationResponse.newBuilder()
            .setCode(OperationResponse.ResponseStatusCode.INVALID)
            .build();

    private static JsonHttpClient jhc = new JsonHttpClient("localhost:12345");

    public static void main(String[] args) {
        try (ZContext context = new ZContext()) {
            ZMQ.Socket socketREP = context.createSocket(ZMQ.REP);
            socketREP.bind("tcp://*:5555");
            ZMQ.Socket socketPUB = context.createSocket(ZMQ.PUB);
            socketPUB.bind("tcp://*:5561");


            OperationResponse reply;
            while (!Thread.currentThread().isInterrupted()) {
                byte[] data = socketREP.recv(0);
                try {
                    OperationRequest request = OperationRequest.parseFrom(data);
                    String nome = request.getNome();
                    System.out.println("Received from " + nome);

                    switch (request.getRequestCase().getNumber()){
                        case OperationRequest.PRODUCAO_FIELD_NUMBER:
                            OfertaProducaoRequest prodRequest = request.getProducao();
                            Producao producao = requestToObj(prodRequest, nome);

                            // avisa os importadores subscritos
                            LocalDateTime inicio = LocalDateTime.now();
                            LocalDateTime fim = inicio.plusSeconds(prodRequest.getDuracaoS());
                            NotificacaoOfertaProducao notif = NotificacaoOfertaProducao.newBuilder()
                                    .setProduto(prodRequest.getProduto())
                                    .setQuantMax(prodRequest.getQuantMax())
                                    .setQuantMin(prodRequest.getQuantMin())
                                    .setPrecoUniMin(prodRequest.getPrecoUniMin())
                                    .setDataInicial(inicio.format(DateTimeFormatter.ISO_DATE_TIME))
                                    .setDataFinal(fim.format(DateTimeFormatter.ISO_DATE_TIME))
                                    .build();
                            socketPUB.sendMore(nome);
                            socketPUB.send(notif.toByteArray());

                            // cria thread que dorme até acabar o período de negociação
                            long waitSeconds = prodRequest.getDuracaoS();
                            new Thread(() -> {
                                try {
                                    Thread.sleep(1000 * waitSeconds);
                                    // vai buscar ofertas ao catalogo
                                    Collection<Encomenda> encomendas = jhc.getCollection(
                                            "producoes/" + producao.getNomeFabricante() + "/" + producao.getNomeProduto() + "/encomendas", Encomenda.class);
                                    // TODO logica toda
                                    // mínimo não atingido -> cancelada
                                    // notificar fabricante e ofertas (quantidade min não atingida)
                                    // guardar cancelada no catalogo

                                    // ofertas aceites e não aceites notificadas, enviar notificaçoes
                                    // guardar resultado para catalogo

                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                            }).start();
                            reply = processProducao(producao);
                            break;
                        case OperationRequest.ENCOMENDA_FIELD_NUMBER:
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

    private static Encomenda requestToObj(OfertaEncomendaRequest r, String nome) {
        return new Encomenda(nome, r.getFabricante(), r.getProduto(), r.getQuant(), r.getPreco());
    }

    private static Producao requestToObj(OfertaProducaoRequest r, String nome) {
        LocalDateTime inicio = LocalDateTime.now();
        LocalDateTime fim = inicio.plusSeconds(r.getDuracaoS());
        return new Producao(nome, r.getProduto(), r.getQuantMin(), r.getQuantMax(), r.getPrecoUniMin(), new Periodo(inicio, fim));
    }
}
