package Cliente.MessagingServices;

public class Session {
    private String nome;
    private String password;
    private int tipo;

    public Session(String nome, String password, int tipo) {
        this.nome = nome;
        this.password = password;
        this.tipo = tipo;
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
