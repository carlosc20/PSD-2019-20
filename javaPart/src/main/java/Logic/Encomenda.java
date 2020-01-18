package Logic;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Encomenda {
    private String nome;
    private String nomeProduto;
    private int quantidade;
    private double precoPorUnidade;

    public Encomenda(){}

    public Encomenda(String fabricante, String produto, int quantidade, double precoUni){
        this.nome = fabricante;
        this.nomeProduto = produto;
        this.quantidade = quantidade;
        this.precoPorUnidade = precoUni;
    }

    @JsonProperty
    public String getNome(){
        return nome;
    }

    @JsonProperty
    public String getNomeProduto(){
        return nomeProduto;
    }

    @JsonProperty
    public int getQuantidade(){
        return quantidade;
    }

    @JsonProperty
    public double getPrecoPorUnidade(){
        return precoPorUnidade;
    }

}
