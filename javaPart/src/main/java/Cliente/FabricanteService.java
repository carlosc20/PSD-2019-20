package Cliente;

import ProtoBuffers.Protos;
import ProtoBuffers.Protos.OfertaProducaoRequest;
import ProtoBuffers.Protos.OperationRequest;

import java.time.Duration;

public class FabricanteService extends UtilizadorService{

    public FabricanteService(Session session) {
        super(session);
    }

    void fazerOfertaProducao(String produto, int quantMin, int quantMax, int precoUniMin, Duration tempoFinal) throws Exception {
        // TODO enviar tempo atual e +duracao?
        OfertaProducaoRequest producao = OfertaProducaoRequest.newBuilder()
                .setProduto(produto)
                .setQuantMax(quantMax)
                .setQuantMin(quantMin)
                .setPrecoUniMin(precoUniMin)
                .build();
        OperationRequest request = Protos.OperationRequest.newBuilder()
                .setNome(this.getNome())
                .setProducao(producao)
                .build();
        this.sendOperation(request);
    }


}
