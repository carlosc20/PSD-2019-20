package catalogo.Negociador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;

public class JsonHttpClient {

    private HttpClient client = HttpClient.newHttpClient();
    private final String scheme = "http";
    private String authority;
    private ObjectMapper om;
    private ObjectWriter ow;

    public JsonHttpClient(String authority){
        om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        ow = om.writer();
        this.authority = authority;
    }

    private String getBodyJson(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(scheme + "://" + authority + "/" + path))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public <T> T getObject(String path, Class<T> c) throws IOException, InterruptedException {
        String body = getBodyJson(path);
        if(body.length() != 0)
            return om.readValue(body, c);
        return null;
    }

    public <T> Collection<T> getCollection(String path, Class<T> c) throws IOException, InterruptedException {
        String body = getBodyJson(path);
        if(body.length() != 0)
            return om.readValue(body, om.getTypeFactory().constructCollectionType(Collection.class,c));
        return null;
    }

    public <T> int post(String path, T obj) throws IOException, InterruptedException {
        String data = ow.writeValueAsString(obj);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(scheme + "://" + authority + "/" + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(data))
                .build();

        HttpResponse<?> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        return response.statusCode();
    }

    public <T> int put(String path, T obj) throws IOException, InterruptedException {
        String data = ow.writeValueAsString(obj);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(scheme + "://" + authority + "/" + path))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(data))
                .build();

        HttpResponse<?> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        return response.statusCode();
    }
}
