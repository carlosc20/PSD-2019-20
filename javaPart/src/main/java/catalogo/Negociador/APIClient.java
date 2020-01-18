package catalogo.Negociador;

import Logic.Periodo;
import Logic.Producao;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class APIClient {
    private ObjectMapper om;
    private ObjectWriter ow;
    private ExecutorService e;
    public static final String sURI = "http://localhost:12345";

    public APIClient(){
        om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        ow = om.writer();
        //caso não haja dependências podem ser usadas várias //TODO pensar melhor no assunto
        e = Executors.newFixedThreadPool(8);
    }

    //para gets em que resultado é unitário
    public <T> void getUnit(String params, Class c, Consumer<T> callback) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(sURI + params))
                .build();

        e.execute(() -> client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        try {
                             callback.accept((T) om.readValue(response.body(), c));
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    }));
    }

    //para coleções
    public <T> void getCollection(String params, Class c, Consumer<T> callback) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(sURI + params))
                .build();

        e.execute(() -> client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    try {
                        callback.accept((om.readValue(response.body(), om.getTypeFactory().constructCollectionType(Collection.class,c))));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }));
    }

    public <T> void post(String params, T obj, Consumer<Integer> callback){
        //TODO envia-lo nos parametros
        UUID uuid = UUID.randomUUID();
        String data;
        try {
            data = ow.writeValueAsString(obj);
            HttpClient client = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(sURI + params))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(data))
                    .build();

        //discarding -> ignora o body da resposta
        e.execute(() -> client.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                            .thenAccept(response -> callback.accept(response.statusCode())));
        } catch (JsonProcessingException e1) {
            e1.printStackTrace();
        }
    }


    public static void main(String[] args) {
        /*
        APIClient c = new APIClient();
        c.<Producao>getUnit("/producoes/Carlos", Producao.class, x-> System.out.println(x.toString()));
        c.<List<Producao>>getCollection("/producoes/Carlos", Producao.class, x-> {
            for(Producao p : x)
                System.out.println(p.toString());
        });

        Periodo periodo = new Periodo(LocalDateTime.now(), LocalDateTime.now());
        Producao p = new Producao("chouriço", 20, 40, 2.5, periodo);
        c.post("/producoes/Carlos", p, z -> System.out.println(z));
        */
    }
}
