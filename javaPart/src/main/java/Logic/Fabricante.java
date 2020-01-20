package Logic;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

public class Fabricante extends Utilizador{
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
        super(nome,password, "fabricante");
        producaoPorProduto = new HashMap<>();
        encomendasPorProducao = new HashMap<>();
    }

    public synchronized boolean addProducao(String produto, Producao p){
        if(producaoPorProduto.containsKey(produto))
            return false;
        producaoPorProduto.put(produto, p);
        encomendasPorProducao.put(produto, null);
        return true;
    }

    public synchronized Producao removeProducao(String produto){
        encomendasPorProducao.remove(produto);
        return producaoPorProduto.remove(produto);

    }

    public synchronized boolean addEncomenda(String produto, Encomenda e){
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

    public synchronized List<Producao> getProducoesPorEstado(String estado){
        ArrayList<Producao> res = new ArrayList<>();
        for(Producao p : producaoPorProduto.values())
            if(p.estado(estado))
                res.add(p);
        return res;
    }


    public synchronized void updateEncomenda(String produto, Encomenda encomenda){
        List<Encomenda> enc = encomendasPorProducao.get(produto);

        enc.removeIf(e->e.equals(encomenda));

        if(enc.size() == 0)
            encomendasPorProducao.remove(produto);
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

