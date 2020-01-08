package catalogo.Representations;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Encomenda {
    private String nome;
    private String nomeProduto;
    private int quantidade;
    private double precoPorUnidade;

    public Encomenda(){}

    public Encomenda(String nome, String nomeProduto, int quantidade, double precoPorUnidade){
        this.nome = nome;
        this.nomeProduto = nomeProduto;
        this.quantidade = quantidade;
        this.precoPorUnidade = precoPorUnidade;
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
