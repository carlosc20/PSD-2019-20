package Logic;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Importador extends Utilizador{
    private List<Encomenda> encomendasTerminadas;
    private List<Encomenda> encomendasCanceladas;
    private HashMap<UUID, Encomenda> encomendasEmCurso;

    public Importador(){}

    public Importador(Utilizador utilizador){
        super(utilizador);
        encomendasTerminadas = new ArrayList<>();
        encomendasCanceladas = new ArrayList<>();
        encomendasEmCurso = new HashMap<>();
    }

    public void addEncomendaEmCurso(UUID uuid, Encomenda e){
        this.encomendasEmCurso.put(uuid,e);
    }

    public void cancelaEncomenda(UUID uuid){
        this.encomendasCanceladas.add(encomendasEmCurso.get(uuid));
        encomendasEmCurso.remove(uuid);
    }

    public void terminaEncomenda(UUID uuid){
        this.encomendasTerminadas.add(encomendasEmCurso.get(uuid));
        encomendasEmCurso.remove(uuid);
    }

    @JsonProperty
    public HashMap<UUID, Encomenda> getEncomendasEmCurso(){
        return encomendasEmCurso;
    }

    @JsonProperty
    public List<Encomenda> getEncomendasTermindas(){
        return encomendasTerminadas;
    }

    @JsonProperty
    public List<Encomenda> getEncomendasCanceladas(){
        return encomendasCanceladas;
    }

}
