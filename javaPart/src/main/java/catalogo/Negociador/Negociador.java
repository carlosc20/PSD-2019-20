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
            ZMQ.Socket socket = context.createSocket(ZMQ.REP);
            socket.bind("tcp://*:5555");

            OperationResponse reply;

            while (!Thread.currentThread().isInterrupted()) {
                byte[] data = socket.recv(0);
                try {
                    OperationRequest request = OperationRequest.parseFrom(data);
                    String nome = request.getNome();
                    System.out.println("Received " + ": [" + nome + "]");
                    switch (request.getRequestCase().getNumber()){
                        case OperationRequest.PRODUCAO_FIELD_NUMBER:
                            OfertaProducaoRequest producao = request.getProducao();
                            // TODO vai buscar prefs de sub FABRICANTE e avisa os subscritos
                            //  envia para catalogo
                            new Thread(() -> {
                                try {
                                    Thread.sleep(1000 * 60);

                                     // TODO vai buscar ofertas ao catalogo, calcula resultado
                                     //  vai buscar prefs de sub ao catalogo, envia pubs aos subscritos
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
                        case OperationRequest.SUBFABRICANTE_FIELD_NUMBER:
                            SubscreverFabricante subFabricante = request.getSubFabricante();
                            // TODO envia para o catalogo
                            break;
                        case OperationRequest.SUBRESULTADOS_FIELD_NUMBER:
                            SubscreverResultados subResultados = request.getSubResultados();
                            // TODO envia para o catalogo
                            break;
                    }
                    reply = OperationResponse.newBuilder()
                            .setCode(OperationResponse.ResponseStatusCode.OK)
                            .build();
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                    reply = OperationResponse.newBuilder()
                            .setCode(OperationResponse.ResponseStatusCode.INVALID)
                            .build();
                }
                socket.send(reply.toByteArray(), 0);
            }
        }
    }
}
