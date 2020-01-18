package catalogo.Resources;

import Logic.Encomenda;
import Logic.Importador;
import Logic.Utilizador;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.ConcurrentHashMap;

@Path("/importadores")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Importadores {
    private ConcurrentHashMap<String, Utilizador> utilizadores;

    public Importadores(ConcurrentHashMap<String, Utilizador> utilizadores){
        this.utilizadores = utilizadores;
    }

    @GET
    @Path("/{nome}/emCurso")
    public Response getImportadorEmCurso(@PathParam("nome") String nome){
        if(utilizadores.containsKey(nome)){
            Importador i = (Importador) utilizadores.get(nome);
            return Response.ok(i.getEncomendasEmCurso()).build();
        }
        return Response.status(405).build();
    }

    @GET
    @Path("/{nome}/terminadas")
    public Response getImportadorTerminadas(@PathParam("nome") String nome){
        if(utilizadores.containsKey(nome)) {
            Importador i = (Importador) utilizadores.get(nome);
            return Response.ok(i.getOpTerminadas()).build();
        }
        return Response.status(405).build();
    }

    @GET
    @Path("/{nome}/canceladas")
    public Response getImportadorCanceladas(@PathParam("nome") String nome){
        if(utilizadores.containsKey(nome)){
            Importador i = (Importador) utilizadores.get(nome);
            return Response.ok(i.getOpCanceladas()).build();
        }
        return Response.status(405).build();
    }

    @POST
    @Path("/{nome}/terminadas")
    public Response postTerminadasImportador(@PathParam("nome") String nome,  Encomenda encomenda){
        Importador i = (Importador) utilizadores.get(nome);
        if(i == null)
            return Response.status(405).build();
        i.addOperacaoTerminada(encomenda);
        return Response.ok(encomenda).build();
    }

    @POST
    @Path("/{nome}/canceladas")
    public Response postCanceladasImportador(@PathParam("nome") String nome, Encomenda encomenda){
        Importador i = (Importador) utilizadores.get(nome);
        if(i == null)
            return Response.status(405).build();
        i.addOperacaoCancelada(encomenda);
        return Response.ok(encomenda).build();
    }

    @POST
    @Path("/{nome}/emCurso")
    public Response postEmCursoImportador(@PathParam("nome") String nome, Encomenda encomenda){
        Importador i = (Importador) utilizadores.get(nome);
        if(i == null)
            return Response.status(405).build();
        i.addEncomendaEmCurso(encomenda);
        return Response.ok().build();
    }


    @DELETE
    @Path("/{nome}/emCurso")
    public Response deleteEmCursoImportador(@PathParam("nome") String nome, Encomenda encomenda){
        Importador i = (Importador) utilizadores.get(nome);
        if(i == null)
            return Response.status(405).build();
        return Response.ok(i.removeEncomendaEmCurso(encomenda)).build();
    }

}
