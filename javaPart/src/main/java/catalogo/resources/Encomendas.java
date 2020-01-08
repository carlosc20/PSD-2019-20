package catalogo.Resources;


import Logic.Encomenda;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;


@Path("/encomendas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Encomendas {
    private final String template;
    private volatile String defaultName;
    private HashMap<String, List<Encomenda>> encomendas;

    public Encomendas(String template, String defaultName, HashMap<String, List<Encomenda>> map){
        this.template = template;
        this.defaultName = defaultName;
        this.encomendas = map;
    }

    @GET
    public HashMap<String, List<Encomenda>> getEncomendas () {
        return encomendas;
    }

    @GET
    @Path("{nome}")
    public List<Encomenda> getEncomendasByClient(@QueryParam("nome") String name){
        return encomendas.get(name);
    }

    @POST
    @Path("/{nome}")
    public Response postEncomenda(@PathParam("nome") String name, Encomenda encomenda) {
        System.out.println("Name :" + name);
        if(!encomendas.containsKey(name))
            return Response.status(401).build();
        encomendas.get(name).add(encomenda);
        return Response.ok(encomenda).build();
    }

}
