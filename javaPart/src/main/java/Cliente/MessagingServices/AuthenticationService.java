package Cliente.MessagingServices;

import ProtoBuffers.Protos.*;
import com.google.protobuf.InvalidProtocolBufferException;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class AuthenticationService {

    private ZMQ.Socket socket;
    private ZContext context;

    public AuthenticationService(String server) {
        context = new ZContext();
        socket = context.createSocket(ZMQ.REQ);
        socket.connect(server);
    }

    public static final int IMPORTADOR = 0;
    public static final int FABRICANTE = 1;

    public Session login(String username, String password) throws Exception {

        // enviar
        LoginRequest request = LoginRequest.newBuilder()
                .setNome(username)
                .setPassword(password)
                .build();
        socket.send(request.toByteArray(), 0);

        // receber
        byte[] reply = socket.recv(0);
        try {
            LoginResponse response = LoginResponse.parseFrom(reply);
            LoginResponse.TipoUtilizador tipo = response.getTipo();
            if(tipo == LoginResponse.TipoUtilizador.FABRICANTE) {
                return  new Session(username, password, FABRICANTE, context, socket);
            } else if(tipo == LoginResponse.TipoUtilizador.IMPORTADOR) {
                return  new Session(username, password, IMPORTADOR, context, socket);
            }
            return null;

        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            throw new Exception();
        }
    }


}
