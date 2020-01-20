package catalogo.Resources;

import Logic.Encomenda;
import Logic.Importador;
import Logic.Utilizador;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;

@Path("/importacoes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Importacoes {
    private HashMap<String, Utilizador> utilizadores;

    public Importacoes(HashMap<String, Utilizador> utilizadores){
        this.utilizadores = utilizadores;

        Importador i = new Importador("Marco", "123");
        utilizadores.put("Marco",i);
    }

    @GET
    @Path("/{nome}/emCurso")
    public Response getImportadorEmCurso(@PathParam("nome") String nome){
        if(utilizadores.containsKey(nome)){
            Importador i = (Importador) utilizadores.get(nome);
            return Response.ok(i.getEncomendasEmCurso("emCurso")).build();
        }
        return Response.status(405).build();
    }

    @GET
    @Path("/{nome}/canceladas")
    public Response getImportadorCanceladas(@PathParam("nome") String nome){
        if(utilizadores.containsKey(nome)) {
            Importador i = (Importador) utilizadores.get(nome);
            return Response.ok(i.getEncomendasEmCurso("canceladas")).build();
        }
        return Response.status(405).build();
    }

    @GET
    @Path("/{nome}/aceites")
    public Response getImportadorAceites(@PathParam("nome") String nome){
        if(utilizadores.containsKey(nome)){
            Importador i = (Importador) utilizadores.get(nome);
            return Response.ok(i.getEncomendasEmCurso("aceites")).build();
        }
        return Response.status(405).build();
    }

    @PUT
    @Path("/{nome}/aceites")
    public Response postAceitesImportador(@PathParam("nome") String nome,  Encomenda encomenda){
        Importador i = (Importador) utilizadores.get(nome);
        if(i == null)
            return Response.status(405).build();
        i.updateEncomenda("aceite", encomenda);
        return Response.status(201).build();
    }

    @PUT
    @Path("/{nome}/cancelada")
    public Response postCanceladasImportador(@PathParam("nome") String nome, Encomenda encomenda){
        Importador i = (Importador) utilizadores.get(nome);
        if(i == null)
            return Response.status(405).build();
        i.updateEncomenda("cancelada", encomenda);
        return Response.status(201).build();
    }

    @POST
    @Path("/{nome}/emCurso")
    public Response postEmCursoImportador(@PathParam("nome") String nome, Encomenda encomenda){
        Importador i = (Importador) utilizadores.get(nome);
        if(i == null)
            return Response.status(405).build();
        i.addEncomenda(encomenda);
        return Response.status(201).build();
    }


    @DELETE
    @Path("/{nome}/emCurso")
    public Response deleteEmCursoImportador(@PathParam("nome") String nome, Encomenda encomenda){
        Importador i = (Importador) utilizadores.get(nome);
        if(i == null)
            return Response.status(405).build();
        i.removeEncomenda(encomenda);
        return Response.ok().build();
    }

}
