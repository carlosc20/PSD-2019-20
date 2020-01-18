package Logic;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class Fabricante extends Negociador<Producao>{
    //TODO controlo de concorrência
    private HashMap<String, Producao> producaoPorProduto;
    private HashMap<String, List<Encomenda>> encomendasPorProducao;

    public Fabricante(){}

    public Fabricante(Utilizador utilizador) {
        super(utilizador);
        producaoPorProduto = new HashMap<>();
        encomendasPorProducao = new HashMap<>();
    }

    public Fabricante(String nome, String password) {
        super(nome,password);
        producaoPorProduto = new HashMap<>();
        encomendasPorProducao = new HashMap<>();
    }

    public void addProducao(String produto, Producao p){
        producaoPorProduto.put(produto, p);
        encomendasPorProducao.put(produto, null);
    }

    public Producao removeProducao(String produto){
        encomendasPorProducao.remove(produto);
        return producaoPorProduto.remove(produto);

    }

    public boolean addEncomenda(String produto, Encomenda e){
        if(encomendasPorProducao.containsKey(produto)) {
            List<Encomenda> encomendas = encomendasPorProducao.get(produto);
            if(encomendas == null){
                encomendas = new ArrayList<>();
                encomendas.add(e);
                encomendasPorProducao.put(produto, encomendas);
            }
            else
                encomendasPorProducao.get(produto).add(e);
            return true;
        }
        return false;
    }

    @JsonProperty
    public Collection<Producao> getProducoes() {
        return producaoPorProduto.values();
    }

    @JsonProperty
    public List<Encomenda> getEncomendas(String produto){
        return encomendasPorProducao.get(produto);
    }

    @JsonProperty
    public Producao get(String produto){
        return producaoPorProduto.get(produto);
    }
}

