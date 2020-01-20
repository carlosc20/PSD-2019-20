package Cliente.MessagingServices;

import Logic.Encomenda;

import ProtoBuffers.Protos.OfertaEncomendaRequest;
import ProtoBuffers.Protos.NotificacaoResultadosFabricante;
import ProtoBuffers.Protos.OfertaProducaoRequest;
import ProtoBuffers.Protos.OperationRequest;
import com.google.protobuf.InvalidProtocolBufferException;


import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class FabricanteService extends UtilizadorService {

    public FabricanteService(Session session, String server) {
        super(session, server);
        String topic = "#"+this.getNome();
        setSubscription(topic, true);
    }

    public void fazerOfertaProducao(String produto, int quantMin, int quantMax, int precoUniMin, Duration duracao) throws Exception {
        OfertaProducaoRequest producao = OfertaProducaoRequest.newBuilder()
                .setProduto(produto)
                .setQuantMax(quantMax)
                .setQuantMin(quantMin)
                .setPrecoUniMin(precoUniMin)
                .setDuracaoS(duracao.getSeconds())
                .build();
        OperationRequest request = OperationRequest.newBuilder()
                .setNome(this.getNome())
                .setPassword(this.getPassword())
                .setProducao(producao)
                .build();
        this.sendOperation(request.toByteArray());
    }

    public FabricanteNotification getNotification() throws IOException {
        getTopic();
        byte[] data = getPublication();
        try{
            NotificacaoResultadosFabricante n = NotificacaoResultadosFabricante.parseFrom(data);
            List<Encomenda> encomendas = new ArrayList<>();
            for (OfertaEncomendaRequest r : n.getEncomendasList()) {
                encomendas.add(new Encomenda(this.getNome(), r.getFabricante(), r.getProduto(), r.getQuant(), r.getPreco()));
            }
            return new FabricanteNotification(n.getProduto(), encomendas);
        } catch (InvalidProtocolBufferException e) {
            throw new IOException();
        }
    }

}
