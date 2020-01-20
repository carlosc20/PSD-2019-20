package Cliente;

import Cliente.MessagingServices.*;
import Logic.Encomenda;
import Logic.Producao;

import java.time.Duration;
import java.util.List;
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
                Session session = as.loginFabricante(nome, password);
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

    private static boolean listening;

    private static void sessaoFabricante(Scanner scanner, Session session) {
        FabricanteService fs = new FabricanteService(session, "tcp://localhost:6666");

        listening = true;
        new Thread(() -> {
            while (listening) {
                try {
                    List<Encomenda> list = fs.getNotification();
                    System.out.println("-----NOTIFICAÇÃO-----");
                    if(list == null) {
                        System.out.println("Resultado: oferta cancelada (quantidade mínima não atingida)");
                        // TODO mais informação
                    } else {
                        System.out.println("Resultado: encomendas aceites:");
                        for (Encomenda e : list) {
                            System.out.println("Quantidade: " + e.getQuantidade());
                            System.out.println("Preço unitário: " + e.getPrecoPorUnidade());
                        }
                    }
                    System.out.println("---------------------");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

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
                        System.out.println("Comando não reconhecido. Use 'help' para ver lista de comandos");
                }
                System.out.println("Oferta concluída com sucesso.");
            } catch (Exception e) {
                System.out.println("Erro ao fazer oferta.");
                e.printStackTrace();
            }
        }
        listening = false;
    }


    private static void sessaoImportador(Scanner scanner, Session session) {
        ImportadorService is = new ImportadorService(session, "tcp://localhost:5561");

        listening = true;
        new Thread(() -> {
            while (listening) {
                try {
                    Notification notif = is.getNotification();
                    switch(notif.getType()){
                        case OFERTA_PRODUCAO:
                            Producao producao = notif.getProducao();
                            System.out.println("-----NOTIFICAÇÃO-----");
                            System.out.println("Nova oferta de produção do fabricante '" + producao.getNomeFabricante() + "'");
                            System.out.println("Produto: " + producao.getNomeProduto());
                            System.out.println("Quantidade max:" + producao.getQuantidadeMax());
                            System.out.println("Quantidade min:" + producao.getQuantidadeMin());
                            System.out.println("Preço unitário min:" + producao.getPrecoPorUnidade());
                            System.out.println("Negociação aberta até " + producao.getPeriodoOferta().getDataFinal());
                            System.out.println("---------------------");
                            break;
                        case RESULTADO_NEGOCIACAO:
                            Encomenda encomenda = notif.getEncomenda();
                            System.out.println("-----NOTIFICAÇÃO-----");
                            System.out.println("Negociação em que fez oferta de encomenda terminou");
                            if(encomenda == null) {
                                System.out.println("Resultado: oferta cancelada (quantidade mínima não atingida)");
                            }
                            else {
                                System.out.println("Resultado: oferta aceite");
                                System.out.println("Fabricante: " + encomenda.getNomeFabricante());
                                System.out.println("Produto: " + encomenda.getQuantidade());
                                System.out.println("Quantidade: " + encomenda.getQuantidade());
                                System.out.println("Preço unitário: " + encomenda.getPrecoPorUnidade());
                            }
                            System.out.println("---------------------");
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

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
                    case "subfabricante":
                        if(checkNArgs(cmds, 1)) break;
                        is.setNotificacoesFabricante(true, cmds[1]);
                        break;
                    case "unsubfabricante":
                        if(checkNArgs(cmds, 1)) break;
                        is.setNotificacoesFabricante(false, cmds[1]);
                        break;
                    case "subresultados":
                        is.setNotificacoesResultados(true);
                        break;
                    case "unsubresultados":
                        is.setNotificacoesResultados(false);
                        break;
                    default:
                        System.out.println("Comando não reconhecido. Use 'help' para ver lista de comandos");
                }
                System.out.println("Operação concluída com sucesso.");
            } catch(Exception e) {
                System.out.println("Erro ao executar operação.");
                e.printStackTrace();
            }
        }
        listening = false;
    }

    private static boolean checkNArgs(String[] cmds, int min) {
        if (cmds.length < (min + 1)) {
            System.out.println("Argumentos insuficientes, esse comando requere " + min + "no minimo");
            return true;
        }
        return false;
    }
}
