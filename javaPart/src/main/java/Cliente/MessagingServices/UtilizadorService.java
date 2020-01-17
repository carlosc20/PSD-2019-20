package Cliente.MessagingServices;

import ProtoBuffers.Protos.OperationResponse;
import ProtoBuffers.Protos.AuthOperationRequest;
import ProtoBuffers.Protos.OperationRequest;
import com.google.protobuf.InvalidProtocolBufferException;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

abstract class UtilizadorService {

    private String nome;
    private String password;

    private ZMQ.Socket socket;

    UtilizadorService(Session session) {
        nome = session.getNome();
        password = session.getPassword();

        ZContext context = new ZContext();
        socket = context.createSocket(ZMQ.REQ);
        String server = "tcp://localhost:5555";
        socket.connect(server);

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
        socket.send(message.toByteArray(), 0);

        // receber
        byte[] reply = socket.recv(0);
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
}