package Cliente.MessagingServices;

import Logic.Encomenda;
import Logic.Periodo;
import Logic.Producao;
import ProtoBuffers.Protos.NotificacaoResultadosImportador;
import ProtoBuffers.Protos.OperationRequest;
import ProtoBuffers.Protos.OfertaEncomendaRequest;
import ProtoBuffers.Protos.NotificacaoOfertaProducao;
import com.google.protobuf.InvalidProtocolBufferException;

public class ImportadorService extends UtilizadorService {

    public ImportadorService(Session session, String server) {
        super(session, server);
    }

    public void fazerOfertaEncomenda(String fabricante, String produto, int quant, int preco) throws Exception {
        OfertaEncomendaRequest encomenda = OfertaEncomendaRequest.newBuilder()
                .setFabricante(fabricante)
                .setProduto(produto)
                .setQuant(quant)
                .setPreco(preco)
                .build();
        OperationRequest request = OperationRequest.newBuilder()
                .setNome(this.getNome())
                .setEncomenda(encomenda)
                .build();
        this.sendOperation(request);
    }

    public void setNotificacoesFabricante(boolean on, String fabricante) {
        this.setSubscription(fabricante, on);
    }

    public void setNotificacoesResultados(boolean on) {
        this.setSubscription(this.getNome(), on);
    }


    public Notification getNotification() throws Exception {
        String topic = getTopic();
        byte[] data = getPublication();
        try {
            if(topic.equals(this.getNome())) {
                // resultado negociação
                NotificacaoResultadosImportador n = NotificacaoResultadosImportador.parseFrom(data);
                Encomenda encomenda = new Encomenda(this.getNome(), n.getFabricante(), n.getProduto(), n.getQuant(), n.getPreco());
                return new Notification(encomenda);
            } else {
                // oferta de fabricante subscrito
                NotificacaoOfertaProducao n = NotificacaoOfertaProducao.parseFrom(data);
                // TODO periodo
                Producao producao = new Producao(topic, n.getProduto(), n.getQuantMin(), n.getQuantMax(), n.getPrecoUniMin(), new Periodo());
                return new Notification(producao);
            }
        } catch (InvalidProtocolBufferException e) {
            throw new Exception();
        }
    }


}
