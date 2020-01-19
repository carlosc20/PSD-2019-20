package Cliente.MessagingServices;

import ProtoBuffers.Protos.OperationResponse;
import ProtoBuffers.Protos.AuthOperationRequest;
import ProtoBuffers.Protos.OperationRequest;
import org.zeromq.ZMQ;

import java.io.IOException;

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

    void sendOperation(OperationRequest request) throws IOException {
        // enviar
        AuthOperationRequest message = AuthOperationRequest.newBuilder()
                .setPassword(password)
                .setRequest(request)
                .build();
        socketREQ.send(message.toByteArray(), 0);

        // receber
        byte[] reply = socketREQ.recv(0);
        OperationResponse response = OperationResponse.parseFrom(reply);
        switch (response.getCode()){
            case OK:
                return;
            case INVALID:
                throw new IOException();
            default:
                throw new IOException(); // TODO erros mais detalhados
        }
    }

    void setSubscription(String topic, boolean on) {
        if(on) {
            socketSUB.subscribe(topic);
        } else {
            socketSUB.unsubscribe(topic);
        }
    }


    String getTopic() {
        return socketSUB.recvStr();
    }

    byte[] getPublication() {
        return socketSUB.recv();
    }


}
