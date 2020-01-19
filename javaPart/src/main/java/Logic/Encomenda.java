package Logic;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Encomenda {
    private String importador;
    private String fabricante;
    private String produto;
    private int quantidade;
    private int precoPorUnidade;

    public Encomenda(){}

    public Encomenda(String importador, String fabricante, String produto, int quantidade, int precoUni){
        this.importador = importador;
        this.fabricante = fabricante;
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoPorUnidade = precoUni;
    }

    @JsonProperty
    public String getNomeImportador(){
        return importador;
    }

    @JsonProperty
    public String getNomeFabricante(){
        return fabricante;
    }


    @JsonProperty
    public String getNomeProduto(){
        return produto;
    }

    @JsonProperty
    public int getQuantidade(){
        return quantidade;
    }

    @JsonProperty
    public int getPrecoPorUnidade(){
        return precoPorUnidade;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Encomenda encomenda = (Encomenda) o;
        return Objects.equals(fabricante, encomenda.fabricante) &&
                Objects.equals(produto, encomenda.produto);
    }
}
