package Cliente;

import java.time.Duration;
import java.util.Scanner;

public class Cliente {


    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in);

        AuthenticationService as = new AuthenticationService();
        while(true) {
            System.out.println("Nome de utilizador:");
            String nome = scanner.nextLine();
            System.out.println("Password:");
            String password = scanner.nextLine();

            Session session = as.login(nome, password);
            if (session != null) {
                System.out.println("Login efetuado com sucesso");
                if (session.getTipo() == 1) {
                    sessaoFabricante(scanner, session);
                } else if (session.getTipo() == 2) {
                    sessaoImportador(scanner, session);
                }
            }
            System.out.println("Login falhou");
        }
    }

    private static void sessaoFabricante(Scanner scanner, Session session) {
        FabricanteService fs = new FabricanteService(session);
        while (true) {
            String input = scanner.nextLine();
            if (input == null || input.equals("sair")) {
                System.out.println("Adeus");
                break;
            }
            String[] cmds = input.split(" ");

            switch (cmds[0].toLowerCase()) {
                case "help":
                    System.out.println("sair");
                    System.out.println("register <username> <password>");
                    break;
                case "oferta":
                    if (cmds.length < 6) {
                        System.out.println("Argumentos insuficientes");
                        continue;
                    }
                    try {
                        // TODO tempo
                        fs.fazerOfertaProducao(cmds[1], Integer.parseInt(cmds[2]), Integer.parseInt(cmds[3]), Integer.parseInt(cmds[4]), Duration.ofSeconds(Long.parseLong(cmds[4])));
                        System.out.println("Oferta concluída com sucesso.");
                    } catch (Exception e) {
                        System.out.println("Erro ao fazer oferta.");
                        e.printStackTrace();
                    }
                    break;
                default:
                    System.out.println("Comando não reconhecido");
            }
        }
    }

    private static void sessaoImportador(Scanner scanner, Session session) {
        ImportadorService is = new ImportadorService(session);
        while (true) {
            String input = scanner.nextLine();
            if (input == null || input.equals("sair")) {
                System.out.println("Adeus");
                break;
            }
            String[] cmds = input.split(" ");

            switch (cmds[0].toLowerCase()) {
                case "help":
                    System.out.println("sair");
                    System.out.println("register <username> <password>");
                    break;
                case "oferta":
                    if (cmds.length < 5) {
                        System.out.println("Argumentos insuficientes");
                        continue;
                    }
                    try {
                        is.fazerOfertaEncomenda(cmds[1],cmds[2],Integer.parseInt(cmds[3]), Integer.parseInt(cmds[4]));
                        System.out.println("Oferta concluída com sucesso.");
                    } catch (Exception e) {
                        System.out.println("Erro ao fazer oferta.");
                        e.printStackTrace();
                    }
                    break;
                case "subFabricante":
                    if (cmds.length < 2) {
                        System.out.println("Argumentos insuficientes");
                        continue;
                    }
                    try {
                        is.setNotificacoesFabricante(true, cmds[1]);
                        System.out.println("Opeações executada com sucesso");
                    } catch (Exception e) {
                        System.out.println("Erro ao executar operação.");
                        e.printStackTrace();
                    }
                    break;
                case "unSubFabricante":
                    if (cmds.length < 2) {
                        System.out.println("Argumentos insuficientes");
                        continue;
                    }
                    try {
                        is.setNotificacoesFabricante(false, cmds[1]);
                        System.out.println("Opeações executada com sucesso");
                    } catch (Exception e) {
                        System.out.println("Erro ao executar operação.");
                        e.printStackTrace();
                    }
                    break;
                case "subResultados":
                    try {
                        is.setNotificacoesResultados(true);
                        System.out.println("Opeações executada com sucesso");
                    } catch (Exception e) {
                        System.out.println("Erro ao executar operação.");
                        e.printStackTrace();
                    }
                    break;
                case "unsubResultados":
                    try {
                        is.setNotificacoesResultados(false);
                        System.out.println("Opeações executada com sucesso");
                    } catch (Exception e) {
                        System.out.println("Erro ao executar operação.");
                        e.printStackTrace();
                    }
                    break;
                default:
                    System.out.println("Comando não reconhecido");
            }
        }
    }
}
