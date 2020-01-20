package Logic;

import ProtoBuffers.Protos;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Encomenda {

    private String importador;
    private String fabricante;
    private String produto;
    private int quantidade;
    private int precoPorUnidade;
    private String estado;

    public Encomenda(){}

    public Encomenda(String importador, String fabricante, String produto, int quantidade, int precoUni){
        this.importador = importador;
        this.fabricante = fabricante;
        this.produto = produto;
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

    @JsonProperty
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
        return Objects.equals(fabricante, encomenda.fabricante) &&
                Objects.equals(produto, encomenda.produto);
    }

    public String toString() {
        return "Encomenda{" +
                "importador = '" + importador + "', " +
                "fabricante = '" + fabricante + "', " +
                "produto = " + produto + ", " +
                "quantidade = " + quantidade + ", " +
                "precoPorUnidade = " + precoPorUnidade +
                '}';
    }
}
