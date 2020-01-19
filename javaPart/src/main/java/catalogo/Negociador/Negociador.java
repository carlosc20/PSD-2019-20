package catalogo.Negociador;

import Logic.Encomenda;
import Logic.Periodo;
import Logic.Producao;
import ProtoBuffers.Protos;
import ProtoBuffers.Protos.OperationResponse;
import ProtoBuffers.Protos.OperationRequest;
import ProtoBuffers.Protos.OfertaProducaoRequest;
import ProtoBuffers.Protos.OfertaEncomendaRequest;
import ProtoBuffers.Protos.NotificacaoResultadosFabricante;
import ProtoBuffers.Protos.NotificacaoResultadosImportador;
import ProtoBuffers.Protos.NotificacaOfertaProducao;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.IOException;
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

                            NotificacaOfertaProducao notif = NotificacaOfertaProducao.newBuilder()
                                    .setProduto(prodRequest.getProduto())
                                    .setQuantMax(prodRequest.getQuantMax())
                                    .setQuantMin(prodRequest.getQuantMin())
                                    .setPrecoUniMin(prodRequest.getPrecoUniMin())
                                    .setDuracaoS(prodRequest.getDuracaoS())
                                    .build();
                            // avisa os importadores subscritos
                            socketPUB.sendMore(nome);
                            socketPUB.send(notif.toByteArray());

                            // cria thread que dorme até acabar o período de negociação
                            long waitSeconds = prodRequest.getDuracaoS();
                            new Thread(() -> {
                                try {
                                    Thread.sleep(1000 * waitSeconds);

                                    // TODO marca que terminou no catalogo -> vai buscar ofertas ao catalogo
                                    Collection<Encomenda> encomendas = jhc.getCollection("encomendas", Encomenda.class);
                                    //  calcula resultado
                                    //  envia pubs aos subscritos
                                    //  envia resutlado para catalogo
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

    private static Encomenda requestToObj(OfertaEncomendaRequest request, String nome) {
        return new Encomenda(nome, request.getFabricante(), request.getProduto(), request.getQuant(), request.getPreco());
    }

    private static Producao requestToObj(OfertaProducaoRequest r, String nome) {
        //r.getDuracaoS(); // TODO periodo direito
        return new Producao(nome, r.getProduto(), r.getQuantMin(), r.getQuantMax(), r.getPrecoUniMin(), new Periodo());
    }
}
