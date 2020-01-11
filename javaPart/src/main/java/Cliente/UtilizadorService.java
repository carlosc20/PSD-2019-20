package Cliente;

public class UtilizadorService {

    private String nome;
    private String password;

    public UtilizadorService(Session session) {
        nome = session.getNome();
        password = session.getPassword();
    }

    void setNotificacoesNegocios(boolean on) {
        // TODO
    }
}
