package catalogo.Resources;

import Logic.*;

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
    private HashMap<String, Utilizador> utilizadores;

    public Producoes(HashMap<String, Utilizador> utilizadores){
        this.utilizadores = utilizadores;

        //------Teste------
        Fabricante f1 = new Fabricante("Carlos", "123");
        Fabricante f2 = new Fabricante("Daniel", "123");
        Fabricante f3 = new Fabricante("Maria", "123");
        Fabricante f4 = new Fabricante("Luís", "123");

        Periodo periodo = new Periodo(LocalDateTime.now(), LocalDateTime.now());

        Producao p1 = new Producao("Carlos", "chouriço", 20, 40, 2, periodo);
        Producao p2 = new Producao("Carlos", "linguiça", 20, 40, 1, periodo);
        Producao p3 = new Producao("Daniel", "chouriço", 2000, 4000, 10, periodo);
        Producao p4 = new Producao("Maria", "bolachas", 21000, 40000, 5, periodo);
        Producao p5 = new Producao("Maria", "sal", 20, 40, 25, periodo);
        Producao p6 = new Producao("Luís", "foguetes", 1, 2, 25000, periodo);

        f1.addProducao("chouriço", p1);
        f1.addProducao("linguiça", p2);
        f2.addProducao("chouriço", p3);
        f3.addProducao("bolachas", p4);
        f3.addProducao("sal", p5);
        f4.addProducao("foguetes", p6);

        Encomenda e1 = new Encomenda("Marco", "Carlos", "chouriço", 1, 2);
        Encomenda e2 = new Encomenda("Marco", "Daniel", "chouriço", 1, 2);

        f1.addEncomenda("chouriço", e1);
        f2.addEncomenda("chouriço", e2);

        utilizadores.put("Carlos", f1);
        utilizadores.put("Daniel", f2);
        utilizadores.put("Maria", f3);
        utilizadores.put("Luís", f4);
    }


    @GET
    @Path("/{nome}/{produto}/encomendas")
    public Response getEncomendas(@PathParam("nome") String nome, @PathParam("produto") String produto) {
        if(utilizadores.containsKey(nome)) {
            Fabricante f = (Fabricante) utilizadores.get(nome);
            return Response.ok(f.getEncomendas(produto)).build();
        }
        return Response.status(405).build();
    }

    @GET
    @Path("/{nome}/{produto}")
    public Response getProducao(@PathParam("nome") String nome, @PathParam("produto") String produto) {
        if(utilizadores.containsKey(nome)) {
            Fabricante f = (Fabricante) utilizadores.get(nome);
            return Response.ok(f.get(produto)).build();
        }
        return Response.status(405).build();
    }

    @GET
    @Path("/{nome}/aceites")
    public Response getImportadorTerminadas(@PathParam("nome") String nome){
        if(utilizadores.containsKey(nome)) {
            Fabricante f = (Fabricante) utilizadores.get(nome);
            return Response.ok(f.getProducoesPorEstado("aceites")).build();
        }
        return Response.status(405).build();
    }

    @GET
    @Path("/{nome}/canceladas")
    public Response getImportadorCanceladas(@PathParam("nome") String nome){
        if(utilizadores.containsKey(nome)){
            Fabricante f = (Fabricante) utilizadores.get(nome);
            return Response.ok(f.getProducoesPorEstado("canceladas")).build();
        }
        return Response.status(405).build();
    }

    @GET
    @Path("/{nome}")
    public Response getFabricanteProducoes(@PathParam("nome") String nome){
        if(utilizadores.containsKey(nome)) {
            Fabricante f = (Fabricante) utilizadores.get(nome);
            return Response.ok(f.getProducoes()).build();
        }
        return Response.status(405).build();
    }

    @GET
    public List<Producao> getProducoes () {
        List<Producao> producoes = new ArrayList<>();
        for(Utilizador u : utilizadores.values()){
            if(u instanceof Fabricante){
                Fabricante f = (Fabricante) u;
                producoes.addAll((f.getProducoes()));
            }
        }
        return producoes;
    }

    @POST
    @Path("/{nome}/{produto}")
    public Response postProducao(@PathParam("nome") String nome, @PathParam("produto") String produto, Producao p) {
        if(utilizadores.containsKey(nome)) {
            System.out.println("Name :" + nome);
            Fabricante f = (Fabricante) utilizadores.get(nome);
            if(f.addProducao(produto, p))
                return Response.status(201).build();
            return Response.status(405).build();
        }
        return Response.status(405).build();
    }

    @POST
    @Path("/{nome}/{produto}/encomendas")
    public Response postEncomenda(@PathParam("nome") String nome, @PathParam("produto") String produto, Encomenda encomenda) {
        if(utilizadores.containsKey(nome)) {
            System.out.println("Name :" + nome);
            Fabricante f = (Fabricante) utilizadores.get(nome);
            if(f.addEncomenda(produto,encomenda))
                return Response.status(201).build();
            return Response.status(405).build();
        }
        return Response.status(405).build();
    }

    @PUT
    @Path("/{nome}/{produto}/encomendas/aceites")
    public Response putEncomendaAceite(@PathParam("nome") String nome, @PathParam("produto") String produto, Encomenda e){
        Fabricante f = (Fabricante) utilizadores.get(nome);
        if(f == null)
            return Response.status(405).build();
        e.setEstado("aceite");
        f.updateEncomenda(produto, e);
        return Response.ok().build();
    }

    @PUT
    @Path("/{nome}/{produto}/encomendas/canceladas")
    public Response putEncomendaCancelada(@PathParam("nome") String nome, @PathParam("produto") String produto, Encomenda e){
        Fabricante f = (Fabricante) utilizadores.get(nome);
        if(f == null)
            return Response.status(405).build();
        e.setEstado("cancelada");
        f.updateEncomenda(produto, e);
        return Response.ok().build();
    }

    @PUT
    @Path("/{nome}/aceite")
    public Response putAceitesFabricante(@PathParam("nome") String nome,  Producao producao){
        Fabricante f = (Fabricante) utilizadores.get(nome);
        if(f == null)
            return Response.status(405).build();
        producao.setEstado("aceite");
        f.addProducao(nome, producao);
        return Response.status(201).build();
    }


    @POST
    @Path("/{nome}/cancelada")
    public Response postCanceladasFabricante(@PathParam("nome") String nome, Producao producao){
        Fabricante f = (Fabricante) utilizadores.get(nome);
        if(f == null)
            return Response.status(405).build();
        producao.setEstado("cancelada");
        f.addProducao(nome, producao);
        return Response.status(201).build();
    }

    @DELETE
    @Path("/{nome}/{produto}")
    public Response deleteProducao(@PathParam("nome") String nome, @PathParam("produto") String produto){
        Fabricante f = (Fabricante) utilizadores.get(nome);
        if(f == null)
            return Response.status(405).build();
        return Response.ok(f.removeProducao(produto)).build();
    }

}
