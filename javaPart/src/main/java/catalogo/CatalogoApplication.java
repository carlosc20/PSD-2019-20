package catalogo;

import catalogo.Resources.Producoes;
import catalogo.Resources.Utilizadores;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.util.HashMap;


public class CatalogoApplication extends Application<CatalogoConfiguration> {
    public static void main(String[] args) throws Exception {
        new CatalogoApplication().run("server", "catalogo.yml");
    }

    @Override
    public String getName() { return "Catalogo"; }

    @Override
    public void initialize(Bootstrap<CatalogoConfiguration> bootstrap) { }

    @Override
    public void run(CatalogoConfiguration configuration, Environment environment){
        HashMap producoes = new HashMap();
        HashMap encomendas = new HashMap();
        environment.jersey().register(
                new Producoes(configuration.template, configuration.defaultName));
        //environment.healthChecks().register("template",
          //      new Producoes(configuration.template));
        environment.jersey().register(
                new Utilizadores(configuration.template, configuration.defaultName, producoes, encomendas));
        //environment.healthChecks().register("template",
          //      new Fabricantes(configuration.template));

    }
}
