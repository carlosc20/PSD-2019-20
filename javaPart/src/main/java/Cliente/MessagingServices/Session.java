package Cliente.MessagingServices;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Session {
    private String nome;
    private String password;
    private int tipo;

    private ZContext context;
    private ZMQ.Socket socket;

    Session(String nome, String password, int tipo, ZContext context, ZMQ.Socket socket) {
        this.nome = nome;
        this.password = password;
        this.tipo = tipo;
        this.context = context;
        this.socket = socket;
    }

    public ZContext getContext() {
        return context;
    }

    public ZMQ.Socket getSocket() {
        return socket;
    }

    public String getNome() {
        return nome;
    }

    public String getPassword() {
        return password;
    }

    public int getTipo() {
        return tipo;
    }
}
