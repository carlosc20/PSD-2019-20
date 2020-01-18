package catalogo.Negociador;

import Logic.Encomenda;
import Logic.Periodo;
import Logic.Producao;
import ProtoBuffers.Protos.OperationResponse;
import ProtoBuffers.Protos.OperationRequest;
import ProtoBuffers.Protos.OfertaProducaoRequest;
import ProtoBuffers.Protos.OfertaEncomendaRequest;
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

    public static void main(String[] args) {
        try (ZContext context = new ZContext()) {
            ZMQ.Socket socketREP = context.createSocket(ZMQ.REP);
            socketREP.bind("tcp://*:5555");
            ZMQ.Socket socketPUB = context.createSocket(ZMQ.PUB);
            socketPUB.bind("tcp://*:5561");
            JsonHttpClient jhc = new JsonHttpClient("localhost:12345");

            OperationResponse reply;
            while (!Thread.currentThread().isInterrupted()) {
                byte[] data = socketREP.recv(0);
                try {
                    OperationRequest request = OperationRequest.parseFrom(data);
                    String nome = request.getNome();
                    System.out.println("Received " + ": [" + nome + "]");

                    switch (request.getRequestCase().getNumber()){
                        case OperationRequest.PRODUCAO_FIELD_NUMBER:
                            OfertaProducaoRequest producao = request.getProducao();
                            // TODO avisa os subscritos
                            socketPUB.sendMore(nome);
                            socketPUB.send(data);
                            // guarda oferta de produção no catalogo
                            jhc.post("producoes", requestToObj(producao, nome));
                            // cria thread que dorme até acabar o período de negociação
                            // UUID uuid = UUID.randomUUID();
                            long waitSeconds = producao.getDuracaoS();
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
                            reply = REPLY_OK;
                            break;
                        case OperationRequest.ENCOMENDA_FIELD_NUMBER:
                            Encomenda encomenda = requestToObj(request.getEncomenda(), nome);
                            // verifica se exista oferta de producao correspondente no catalogo
                            Producao prod = jhc.getObject("producoes", Producao.class);
                            if(prod != null) {
                                // se existir guarda encomenda no catalogo
                                // TODO verifica se quant é maior que max e se preco e maior que min
                                jhc.post("encomendas", encomenda);
                                reply = REPLY_OK;
                            }
                            else {
                                reply = REPLY_INVALID; // TODO erro especifico
                            }
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

    private static Encomenda requestToObj(OfertaEncomendaRequest request, String nome) {
        return new Encomenda(nome, request.getFabricante(), request.getProduto(), request.getQuant(), request.getPreco());
    }

    private static Producao requestToObj(OfertaProducaoRequest r, String nome) {
        //r.getDuracaoS(); // TODO periodo direito
        return new Producao(nome, r.getProduto(), r.getQuantMin(), r.getQuantMax(), r.getPrecoUniMin(), new Periodo());
    }
}
