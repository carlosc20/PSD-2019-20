package Logic;

import ProtoBuffers.Protos;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Encomenda {

    private String nomeImportador;
    private String nomeFabricante;
    private String nomeProduto;
    private int quantidade;
    private int precoPorUnidade;
    private String estado;

    public Encomenda(){}

    public Encomenda(String nomeImportador, String nomeFabricante, String nomeProduto, int quantidade, int precoUni){
        this.nomeImportador = nomeImportador;
        this.nomeFabricante = nomeFabricante;
        this.nomeProduto = nomeProduto;
        this.quantidade = quantidade;
        this.precoPorUnidade = precoUni;
        this.estado = "emCurso";
    }

    public static Encomenda fromProtoRequest(Protos.OperationRequest request) {
        Protos.OfertaEncomendaRequest r = request.getEncomenda();
        return new Encomenda(request.getNome(), r.getFabricante(), r.getProduto(), r.getQuant(), r.getPreco());
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
    public int getPrecoPorUnidade(){
        return precoPorUnidade;
    }

    @JsonProperty
    public String getEstado() { return estado; }

    public boolean estado(String e){
        return estado.equals(e);
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Encomenda encomenda = (Encomenda) o;
        return Objects.equals(nomeFabricante, encomenda.nomeFabricante) &&
                Objects.equals(nomeProduto, encomenda.nomeProduto);
    }

    public String toString() {
        return "Encomenda{" +
                "nomeImportador = '" + nomeImportador + "', " +
                "nomeFabricante = '" + nomeFabricante + "', " +
                "nomeProduto = " + nomeProduto + ", " +
                "quantidade = " + quantidade + ", " +
                "precoPorUnidade = " + precoPorUnidade +
                '}';
    }
}
