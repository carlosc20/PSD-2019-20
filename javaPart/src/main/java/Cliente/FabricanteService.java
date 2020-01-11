package Cliente;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import java.time.Duration;

public class FabricanteService extends UtilizadorService{

    private ZMQ.Socket socket;

    public FabricanteService(Session session) {
        super(session);
        ZContext context = new ZContext();
        socket = context.createSocket(ZMQ.REQ);
        String server = "tcp://localhost:";
        socket.connect(server);
    }


    void fazerOfertaProducao(String produto, int quantMin, int quantMax, int precoUniMin, Duration duracao) {
        socket.send("ola", 0);
        // TODO
        byte[] reply = socket.recv(0);
    }
}
