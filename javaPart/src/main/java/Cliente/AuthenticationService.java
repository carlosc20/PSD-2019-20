package Cliente;

import ProtoBuffers.Protos.*;
import com.google.protobuf.InvalidProtocolBufferException;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class AuthenticationService {

    private ZMQ.Socket socket;

    public AuthenticationService() {
        ZContext context = new ZContext();
        socket = context.createSocket(ZMQ.REQ);
        String server = "tcp://localhost:";
        socket.connect(server);
    }

    Session login(String username, String password) throws Exception {

        // enviar
        LoginRequest request = LoginRequest.newBuilder()
                .setNome(username)
                .setPassword(password)
                .build();
        socket.send(request.toByteArray(), 0);

        // receber, bloqueante
        byte[] reply = socket.recv(0);
        try {
            LoginResponse response = LoginResponse.parseFrom(reply);
            LoginResponse.TipoUtilizador tipo = response.getTipo();
            if(tipo == LoginResponse.TipoUtilizador.FABRICANTE) {
                return  new Session(username, password, 0);
            } else if(tipo == LoginResponse.TipoUtilizador.IMPORTADOR) {
                return  new Session(username, password, 1);
            }
            return null;

        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            throw new Exception();
        }
    }


}
