package Cliente;

public abstract class UtilizadorService {

    private String nome;
    private String password;

    public UtilizadorService(Session session) {
        nome = session.getNome();
        password = session.getPassword();
    }

    public String getNome() {
        return nome;
    }

    public String getPassword() {
        return password;
    }


}
