package Cliente;

public class Cliente {


    public static void main(String[] args) throws Exception {

        // TODO input dados login
        AuthenticationService as = new AuthenticationService();
        Session s = as.login("Carlos", "1234");

        if(s.getTipo() == 1) {
            FabricanteService fs = new FabricanteService(s);
            // TODO input

        } else if (s.getTipo() == 2) {
            ImportadorService is = new ImportadorService(s);
            // TODO input

        }
    }
}
