package Logic;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Fabricante extends Utilizador{
    private List<Producao> producoes;

    public Fabricante(){}

    public Fabricante(Utilizador utilizador) {
        super(utilizador);
        producoes= new ArrayList<>();
    }

    public void addProducao(Producao p){
        this.producoes.add(p);
    }

    @JsonProperty
    public List<Producao> getProducoes() {
        return producoes;
    }
}

