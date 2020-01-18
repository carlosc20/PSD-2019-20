package Logic;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Encomenda {
    private String nomeImportador;
    private String nomeFabricante;
    private String nomeProduto;
    private int quantidade;
    private double precoPorUnidade;

    public Encomenda(){}

    public Encomenda(String nomeImportador, String nomeFabricante, String nomeProduto, int quantidade, double precoPorUnidade){
        this.nomeImportador = nomeImportador;
        this.nomeFabricante = nomeFabricante;
        this.nomeProduto = nomeProduto;
        this.quantidade = quantidade;
        this.precoPorUnidade = precoPorUnidade;
    }

    @JsonProperty
    public String getNomeImportador(){
        return nomeImportador;
    }

    @JsonProperty
    public String getNomeFabricante(){
        return nomeFabricante;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Encomenda encomenda = (Encomenda) o;
        return Objects.equals(nomeFabricante, encomenda.nomeFabricante) &&
                Objects.equals(nomeProduto, encomenda.nomeProduto);
    }
}
