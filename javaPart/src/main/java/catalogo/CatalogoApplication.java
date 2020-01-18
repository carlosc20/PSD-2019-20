package catalogo;

import Logic.Utilizador;
import catalogo.Resources.Importacoes;
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
        HashMap<String, Utilizador> utilizadoresHashMap = new HashMap<>();
        environment.jersey().register(
                new Utilizadores(utilizadoresHashMap));
        environment.jersey().register(
                new Producoes(utilizadoresHashMap));
        environment.jersey().register(
                new Importacoes(utilizadoresHashMap));
        //environment.healthChecks().register("template",
          //      new Producoes(configuration.template));

        //environment.healthChecks().register("template",
          //      new Producoes(configuration.template));

    }
}
