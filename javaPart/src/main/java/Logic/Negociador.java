package Logic;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Negociador<T> extends Utilizador{
    private List<T> opTerminadas;
    private List<T> opCanceladas;

    public Negociador(){}

    public Negociador(Utilizador utilizador){
        super(utilizador);
        this.opTerminadas = new ArrayList<>();
        this.opCanceladas = new ArrayList<>();
    }

    public Negociador(String nome, String password){
        super(nome,password);
        this.opTerminadas = new ArrayList<>();
        this.opCanceladas = new ArrayList<>();
    }

    public void addOperacaoCancelada(T obj){
        this.opCanceladas.add(obj);
    }

    public void addOperacaoTerminada(T obj){
        this.opTerminadas.add(obj);
    }

    @JsonProperty
    public List<T> getOpTerminadas() {
        return opTerminadas;
    }

    @JsonProperty
    public List<T> getOpCanceladas() {
        return opCanceladas;
    }
}
