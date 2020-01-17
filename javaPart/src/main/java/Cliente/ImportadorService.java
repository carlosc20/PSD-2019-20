package Cliente;

import ProtoBuffers.Protos.OperationRequest;
import ProtoBuffers.Protos.SubscreverResultados;
import ProtoBuffers.Protos.SubscreverFabricante;
import ProtoBuffers.Protos.OfertaEncomendaRequest;

public class ImportadorService extends UtilizadorService {


    public ImportadorService(Session session) {
        super(session);
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

    public void setNotificacoesFabricante(boolean on, String fabricante) throws Exception {
        SubscreverFabricante subFabricante = SubscreverFabricante.newBuilder()
                .setFabricante(fabricante)
                .setIsActive(on)
                .build();
        OperationRequest request = OperationRequest.newBuilder()
                .setNome(this.getNome())
                .setSubFabricante(subFabricante)
                .build();
        this.sendOperation(request);
    }

    void setNotificacoesResultados(boolean on) throws Exception {
        SubscreverResultados subResultados = SubscreverResultados.newBuilder()
                .setIsActive(on)
                .build();
        OperationRequest request = OperationRequest.newBuilder()
                .setNome(this.getNome())
                .setSubResultados(subResultados)
                .build();
        this.sendOperation(request);
    }


}
