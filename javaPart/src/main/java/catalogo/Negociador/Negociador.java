package catalogo.Negociador;

import ProtoBuffers.Protos.OperationResponse;
import ProtoBuffers.Protos.OperationRequest;
import ProtoBuffers.Protos.OfertaProducaoRequest;
import ProtoBuffers.Protos.SubscreverResultados;
import ProtoBuffers.Protos.SubscreverFabricante;
import ProtoBuffers.Protos.OfertaEncomendaRequest;
import com.google.protobuf.InvalidProtocolBufferException;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Negociador {

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
                    System.out.println("Received " + ": [" + nome + "]");
                    switch (request.getRequestCase().getNumber()){
                        case OperationRequest.PRODUCAO_FIELD_NUMBER:
                            OfertaProducaoRequest producao = request.getProducao();
                            // TODO avisa os subscritos
                            //  envia para catalogo
                            new Thread(() -> {
                                try {
                                    Thread.sleep(1000 * 60);

                                     // TODO vai buscar ofertas ao catalogo, calcula resultado
                                     //  envia pubs aos subscritos
                                     //  envia para catalogo
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                            }).start();
                            break;
                        case OperationRequest.ENCOMENDA_FIELD_NUMBER:
                            OfertaEncomendaRequest encomenda = request.getEncomenda();
                            // TODO vai buscar ofertas de prod ao catalogo, se existirem envia para catalogo oferta, se nao devolve erro
                            break;
                    }
                    reply = OperationResponse.newBuilder()
                            .setCode(OperationResponse.ResponseStatusCode.OK)
                            .build();
                } catch (InvalidProtocolBufferException e) {
                    System.err.println(e.toString());
                    reply = OperationResponse.newBuilder()
                            .setCode(OperationResponse.ResponseStatusCode.INVALID)
                            .build();
                }
                socketREP.send(reply.toByteArray(), 0);
            }
        }
    }
}
