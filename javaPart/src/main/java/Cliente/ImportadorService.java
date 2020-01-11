package Cliente;

import ProtoBuffers.Protos;
import com.google.protobuf.InvalidProtocolBufferException;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class ImportadorService extends UtilizadorService {

    private ZMQ.Socket socket;

    public ImportadorService(Session session) {
        super(session);
        ZContext context = new ZContext();
        socket = context.createSocket(ZMQ.REQ);
        String server = "tcp://localhost:";
        socket.connect(server);
    }

    public void fazerOfertaEncomenda(String fabricante, String produto, int quant, int preco) throws Exception {
        Protos.OfertaEncomenda request = Protos.OfertaEncomenda.newBuilder()
                .setNome(this.getNome())
                .setPassword(this.getPassword())
                .setQuant(quant)
                .setPreco(preco)
                .build();
        socket.send(request.toByteArray(), 0);
        waitForReply();
    }

    public void setNotificacoesFabricante(boolean isActive, String fabricante) throws Exception {
        Protos.SubscreverFabricante request = Protos.SubscreverFabricante.newBuilder()
                .setNome(this.getNome())
                .setPassword(this.getPassword())
                .setFabricante(fabricante)
                .setIsActive(isActive)
                .build();
        socket.send(request.toByteArray(), 0);
        waitForReply();
    }

    void setNotificacoesResultados(boolean on) throws Exception {
        Protos.SubscreverResultados request = Protos.SubscreverResultados.newBuilder()
                .setNome(this.getNome())
                .setPassword(this.getPassword())
                .build();
        socket.send(request.toByteArray(), 0);
        waitForReply();
    }

    private void waitForReply() throws Exception {
        byte[] reply = socket.recv(0);
        try {
            Protos.OperationResponse response = Protos.OperationResponse.parseFrom(reply);
            int code = response.getCode();
            if(code != 0)
                throw new Exception();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            throw new Exception();
        }
    }

}
