package Logic;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Importador extends Negociador<Encomenda>{
    private List<Encomenda> encomendasEmCurso;

    public Importador(){}

    public Importador(Utilizador utilizador){
        super(utilizador);
        encomendasEmCurso = new ArrayList<>();
    }

    public Importador(String name, String password){
        super(name, password);
        encomendasEmCurso = new ArrayList<>();
    }

    public void addEncomendaEmCurso(Encomenda e){
        encomendasEmCurso.add(e);
    }

    public boolean removeEncomendaEmCurso(Encomenda e){
        return encomendasEmCurso.remove(e);
    }

    @JsonProperty
    public List<Encomenda> getEncomendasEmCurso(){
        return encomendasEmCurso;
    }

}
