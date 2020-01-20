package Cliente.MessagingServices;

import Logic.Encomenda;

import java.util.List;

public class FabricanteNotification {
    private String produto;
    private List<Encomenda> encomendas;

    public FabricanteNotification(String produto, List<Encomenda> encomendas) {
        this.produto = produto;
        this.encomendas = encomendas;
    }

    public String getProduto() {
        return produto;
    }

    public List<Encomenda> getEncomendas() {
        return encomendas;
    }
}
