package catalogo.Representations;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Importador extends Utilizador{
    private List<Encomenda> encomendas;

    public Importador(){}

    public Importador(Utilizador utilizador){
        super(utilizador);
        encomendas = new ArrayList<>();
    }

    @JsonProperty
    public List<Encomenda> getEncomendas(){
        return encomendas;
    }

}
