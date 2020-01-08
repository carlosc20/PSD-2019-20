package catalogo.Resources;

import Logic.Fabricante;
import Logic.Importador;
import Logic.Utilizador;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;


@Path("/utilizadores")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Utilizadores {
    private final String template;
    private volatile String defaultName;
    private HashMap producoes;
    private HashMap encomendas;
    private HashMap<String, Utilizador> utilizadores;

    public Utilizadores(String template, String defaultName, HashMap producoes, HashMap encomendas){
        this.template = template;
        this.defaultName = defaultName;
        this.producoes = producoes;
        this.encomendas = encomendas;
        this.utilizadores = new HashMap<>();
    }

    @GET
    public HashMap<String, Utilizador> getUtilizadores() {
        return utilizadores;
    }

    @GET
    @Path("/{nome}")
    public Utilizador getUtilizador(@PathParam("nome") String nome){
        return utilizadores.get(nome);
    }

    @POST
    @Path("/importador")
    public Response postImportador(Utilizador utilizador) {
        if(utilizadores.containsKey(utilizador.getNome()))
            return Response.status(400).build();
        utilizadores.put(utilizador.getNome(), new Importador(utilizador));
        encomendas.put(utilizador.getNome(), new ArrayList());
        return Response.ok(utilizador).build();
    }

    @POST
    @Path("/fabricante")
    public Response postFabricante(Utilizador utilizador) {
        if(utilizadores.containsKey(utilizador.getNome()))
            return Response.status(400).build();
        utilizadores.put(utilizador.getNome(), new Fabricante(utilizador));
        producoes.put(utilizador.getNome(), new ArrayList());
        return Response.ok(utilizador).build();
    }
}
