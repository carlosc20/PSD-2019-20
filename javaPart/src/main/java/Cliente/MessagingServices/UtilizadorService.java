package Cliente.MessagingServices;

import ProtoBuffers.Protos.OperationResponse;
import ProtoBuffers.Protos.AuthOperationRequest;
import ProtoBuffers.Protos.OperationRequest;
import com.google.protobuf.InvalidProtocolBufferException;
import org.zeromq.ZMQ;

abstract class UtilizadorService {

    private String nome;
    private String password;

    private ZMQ.Socket socketREQ;
    private ZMQ.Socket socketSUB;


    UtilizadorService(Session session, String server) {
        nome = session.getNome();
        password = session.getPassword();
        socketREQ = session.getSocket();
        socketSUB = session.getContext().createSocket(ZMQ.SUB);
        socketSUB.connect(server);
    }

    String getNome() {
        return nome;
    }

    String getPassword() {
        return password;
    }

    // TODO exceptions
    void sendOperation(OperationRequest request) throws Exception {
        // enviar
        AuthOperationRequest message = AuthOperationRequest.newBuilder()
                .setPassword(password)
                .setRequest(request)
                .build();
        socketREQ.send(message.toByteArray(), 0);

        // receber
        byte[] reply = socketREQ.recv(0);
        try {
            OperationResponse response = OperationResponse.parseFrom(reply);
            switch (response.getCode()){
                case OK:
                    return;
                case INVALID:
                    throw new Exception();
                default:
                    throw new Exception();
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            throw new Exception();
        }
    }

    void setSubscription(String topic, boolean on) {
        if(on) {
            socketSUB.subscribe(topic);
        } else {
            socketSUB.unsubscribe(topic);
        }
    }

    // bloqueante
    // socketSUB.subscribe(ZMQ.SUBSCRIPTION_ALL);
    // TODO o que devolver?
    void getNotification() {
        byte[] b = socketSUB.recv();
        System.out.println(new String(b));
    }
}
