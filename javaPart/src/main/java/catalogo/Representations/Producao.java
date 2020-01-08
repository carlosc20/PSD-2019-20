package catalogo.Representations;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Producao {
    private String nome;
    private int quantidadeMin;
    private int quantidadeMax;
    private double precoPorUnidade;
    private Periodo periodoOferta;

    public Producao(){}

    public Producao(String nome, int quantidadeMin, int quantidadeMax, double precoPorUnidade, Periodo periodoOferta){
        this.nome = nome;
        this.quantidadeMin = quantidadeMin;
        this.quantidadeMax = quantidadeMax;
        this.precoPorUnidade = precoPorUnidade;
        this.periodoOferta = periodoOferta;
    }

    @JsonProperty
    public String getNome() {
        return nome;
    }

    @JsonProperty
    public int getQuantidadeMin() {
        return quantidadeMin;
    }

    @JsonProperty
    public int getQuantidadeMax() {
        return quantidadeMax;
    }

    @JsonProperty
    public double getPrecoPorUnidade() {
        return precoPorUnidade;
    }

    @JsonProperty
    public Periodo getPeriodoOferta() {
        return periodoOferta;
    }
}
