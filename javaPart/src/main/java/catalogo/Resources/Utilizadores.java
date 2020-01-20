package catalogo.Resources;

import Logic.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.HashMap;


@Path("/utilizadores")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Utilizadores {
    private HashMap<String, Utilizador> utilizadores;

    public Utilizadores(HashMap<String, Utilizador> utilizadores){
        this.utilizadores = utilizadores;
    }

    @GET
    public Collection<Utilizador> getUtilizadores() {
        return utilizadores.values();
    }

    @GET
    @Path("/{nome}")
    public Utilizador getUtilizador(@PathParam("nome") String nome){
        return utilizadores.get(nome);
    }
}
