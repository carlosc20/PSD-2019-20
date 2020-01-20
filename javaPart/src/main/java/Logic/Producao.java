package Logic;

import ProtoBuffers.Protos;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class Producao {

    private String nomeFabricante;
    private String nomeProduto;
    private int quantidadeMin;
    private int quantidadeMax;
    private int precoPorUnidade;
    private Periodo periodoOferta;

    public Producao(){

    }

    public Producao(String nomeFabricante, String nomeProduto, int quantidadeMin, int quantidadeMax, int precoPorUnidade, Periodo periodoOferta){
        this.nomeFabricante = nomeFabricante;
        this.nomeProduto = nomeProduto;
        this.quantidadeMin = quantidadeMin;
        this.quantidadeMax = quantidadeMax;
        this.precoPorUnidade = precoPorUnidade;
        this.periodoOferta = periodoOferta;
    }

    public static Producao fromProtoRequest(Protos.OperationRequest request) {
        Protos.OfertaProducaoRequest r = request.getProducao();
        LocalDateTime inicio = LocalDateTime.now();
        LocalDateTime fim = inicio.plusSeconds(r.getDuracaoS());
        return new Producao(request.getNome(), r.getProduto(), r.getQuantMin(), r.getQuantMax(), r.getPrecoUniMin(), new Periodo(inicio, fim));
    }

    @JsonProperty
    public String getNomeFabricante() {
        return nomeFabricante;
    }

    @JsonProperty
    public String getNomeProduto() {
        return nomeProduto;
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
    public int getPrecoPorUnidade() {
        return precoPorUnidade;
    }

    @JsonProperty
    public Periodo getPeriodoOferta() {
        return periodoOferta;
    }

    @Override
    public String toString() {
        return "Producao{" +
                "nomeFabricante = '" + nomeFabricante + "', " +
                "nomeProduto = '" + nomeProduto + "'" +
                ", quantidadeMin = " + quantidadeMin +
                ", quantidadeMax = " + quantidadeMax +
                ", precoPorUnidade = " + precoPorUnidade +
                ", periodoOferta = " + periodoOferta.toString() +
                '}';
    }
}
