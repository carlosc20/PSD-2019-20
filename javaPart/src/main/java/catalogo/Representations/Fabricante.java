package catalogo.Representations;

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

    @JsonProperty
    public List<Producao> getProducoes() {
        return producoes;
    }
}

