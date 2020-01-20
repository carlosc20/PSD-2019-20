package Logic;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Importador extends Utilizador{
    private List<Encomenda> encomendas;

    public Importador(){}

    public Importador(Utilizador utilizador){
        super(utilizador);
        encomendas = new ArrayList<>();
    }

    public Importador(String name, String password){
        super(name, password, "importador");
        encomendas = new ArrayList<>();
    }

    public synchronized void addEncomenda(Encomenda e){
        encomendas.add(e);
    }

    public synchronized List<Encomenda> getEncomendasEmCurso(String estado){
        ArrayList<Encomenda> res = new ArrayList<>();
        for(Encomenda e: encomendas)
            if(e.estado(estado))
                res.add(e);
        return res;
    }

    public synchronized void updateEncomenda(String estado, Encomenda encomenda){
        for(Encomenda e : encomendas)
            if(e.equals(encomenda))
                e.setEstado(estado);
    }

    public synchronized void removeEncomenda(Encomenda encomenda){
        for(Encomenda e : encomendas)
            if(e.equals(encomenda))
                encomendas.remove(encomenda);
    }




    @JsonProperty
    public List<Encomenda> getEncomendas(){
        return encomendas;
    }

}
