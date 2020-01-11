package Cliente;

import ProtoBuffers.Protos;
import com.google.protobuf.InvalidProtocolBufferException;
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

    void fazerOfertaProducao(String produto, int quantMin, int quantMax, int precoUniMin, Duration tempoFinal) throws Exception {
        // TODO enviar tempo atual e +duracao
        Protos.OfertaProducao request = Protos.OfertaProducao.newBuilder()
                .setNome(this.getNome())
                .setPassword(this.getPassword())
                .setProduto(produto)
                .setQuantMax(quantMax)
                .setQuantMin(quantMin)
                .setPrecoUniMin(precoUniMin)
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
