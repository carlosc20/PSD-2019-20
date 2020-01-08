package catalogo.Resources;

import catalogo.Representations.Periodo;
import catalogo.Representations.Producao;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Path("/producoes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Producoes {
    private final String template;
    private volatile String defaultName;
    private HashMap<String, List<Producao>> producoes;

    public Producoes(String template, String defaultName, HashMap<String, List<Producao>> producoes){
        this.template = template;
        this.defaultName = defaultName;
        this.producoes = producoes;

        //--------------------------------Teste------
        Periodo periodo = new Periodo(LocalDateTime.now(), LocalDateTime.now());
        Producao p = new Producao("chouriço", 20, 40, 2.5, periodo);
        ArrayList<Producao> l = new ArrayList<>();
        l.add(p);
        producoes.put("Carlos", l);
    }

    @GET
    @Path("/{nome}")
    public List<Producao> getEncomendasByClient(@PathParam("nome") String name){
        return producoes.get(name);
    }

    @GET
    public HashMap<String, List<Producao>> getProducoes () {
        return producoes;
    }

    @POST
    @Path("/{nome}")
    public Response postEncomenda(@PathParam("nome") String name, Producao producao) {
        System.out.println("Name :" + name);
        if(!producoes.containsKey(name))
            return Response.status(401).build();
        producoes.get(name).add(producao);
        return Response.ok(producao).build();
    }

}
