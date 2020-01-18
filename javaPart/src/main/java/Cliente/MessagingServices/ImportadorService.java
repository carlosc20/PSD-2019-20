package Cliente.MessagingServices;

import ProtoBuffers.Protos.OperationRequest;
import ProtoBuffers.Protos.OfertaEncomendaRequest;

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
        this.setSubscription("resultados", on);
    }
}
