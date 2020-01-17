package Cliente;

import Cliente.MessagingServices.AuthenticationService;
import Cliente.MessagingServices.FabricanteService;
import Cliente.MessagingServices.ImportadorService;
import Cliente.MessagingServices.Session;

import java.time.Duration;
import java.util.Scanner;

public class Cliente {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        AuthenticationService as = new AuthenticationService("tcp://localhost:5555");
        while(true) {
            System.out.println("Nome de utilizador:");
            String nome = scanner.nextLine();
            if(nome.equals("")) break;

            System.out.println("Password:");
            String password = scanner.nextLine();

            try {
                Session session = as.login(nome, password);
                if (session != null) {
                    System.out.println("Login efetuado com sucesso");
                    if (session.getTipo() == AuthenticationService.FABRICANTE) {
                        sessaoFabricante(scanner, session);
                    } else if (session.getTipo() == AuthenticationService.IMPORTADOR) {
                        sessaoImportador(scanner, session);
                    }
                } else {
                    System.out.println("Login falhou");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private static void sessaoFabricante(Scanner scanner, Session session) {
        FabricanteService fs = new FabricanteService(session, "tcp://localhost:5561");
        while (true) {
            String input = scanner.nextLine();
            if (input == null || input.equals("sair")) {
                System.out.println("Sessão terminada");
                break;
            }
            if(input.equals("help")) {
                System.out.println("sair");
                System.out.println("oferta <produto> <quantMin> <quantMax> <precoUniMin> <duracao em s>");
                continue;
            }
            String[] cmds = input.split(" ");
            try {
                switch (cmds[0].toLowerCase()) {
                    case "oferta":
                        if(checkNArgs(cmds, 5)) break;
                            String produto = cmds[1];
                            int quantMin = Integer.parseInt(cmds[2]);
                            int quantMax = Integer.parseInt(cmds[3]);
                            int precoUniMin = Integer.parseInt(cmds[4]);
                            Duration duracao_s = Duration.ofSeconds(Long.parseLong(cmds[5]));
                            fs.fazerOfertaProducao(produto, quantMin, quantMax, precoUniMin, duracao_s);
                        break;
                    default:
                        System.out.println("Comando não reconhecido");
                }
                System.out.println("Oferta concluída com sucesso.");
            } catch (Exception e) {
                System.out.println("Erro ao fazer oferta.");
                e.printStackTrace();
            }
        }
    }


    private static void sessaoImportador(Scanner scanner, Session session) {
        ImportadorService is = new ImportadorService(session, "tcp://localhost:5561");
        while (true) {
            String input = scanner.nextLine();
            if (input == null || input.equals("sair")) {
                System.out.println("Sessão terminada");
                break;
            }
            if(input.equals("help")) {
                System.out.println("sair");
                System.out.println("oferta <fabricante> <produto> <quant> <preco>");
                System.out.println("subFabricante <fabricante>");
                System.out.println("unSubFabricante <fabricante>");
                System.out.println("subResultados");
                System.out.println("unSubResultados");
                continue;
            }
            String[] cmds = input.split(" ");
            try {
                switch (cmds[0].toLowerCase()) {
                    case "oferta":
                        if(checkNArgs(cmds, 4)) break;
                        String fabricante = cmds[1];
                        String produto = cmds[2];
                        int quant = Integer.parseInt(cmds[3]);
                        int preco = Integer.parseInt(cmds[4]);
                        is.fazerOfertaEncomenda(fabricante, produto, quant, preco);
                        break;
                    case "subFabricante":
                        if(checkNArgs(cmds, 1)) break;
                        is.setNotificacoesFabricante(true, cmds[1]);
                        break;
                    case "unSubFabricante":
                        if(checkNArgs(cmds, 1)) break;
                        is.setNotificacoesFabricante(false, cmds[1]);
                        break;
                    case "subResultados":
                        is.setNotificacoesResultados(true);
                        break;
                    case "unsubResultados":
                        is.setNotificacoesResultados(false);
                        break;
                    default:
                        System.out.println("Comando não reconhecido. Use 'help' para ver lista de comandos");
                }
                System.out.println("Oferta concluída com sucesso.");
            } catch(Exception e) {
                System.out.println("Erro ao executar operação.");
                e.printStackTrace();
            }
        }
    }

    private static boolean checkNArgs(String[] cmds, int min) {
        if (cmds.length < (min + 1)) {
            System.out.println("Argumentos insuficientes, esse comando requere " + min + "no minimo");
            return true;
        }
        return false;
    }
}
