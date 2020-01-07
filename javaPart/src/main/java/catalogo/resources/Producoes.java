package catalogo.Resources;

import catalogo.Representations.Producao;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
    }

    @GET
    public HashMap<String, List<Producao>> getProducoes () {
        return producoes;
    }

    @GET
    @Path("{clientName}")
    public List<Producao> getEncomendasByClient(@QueryParam("clientName") String name){
        return producoes.get(name);
    }

    @POST
    @Path("{clientName}")
    public Response postEncomenda(@PathParam("clientName") String name, Producao encomenda) {
        System.out.println("Name :" + name);
        if(!producoes.containsKey(name))
            return Response.status(401).build();
        producoes.get(name).add(encomenda);
        return Response.ok().build();
    }

}
