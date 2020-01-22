import catalogo.Negociador.Negociador;

public class ComecaNegociadores {

    public static void main(String[] args) {
        new Thread(() -> new Negociador().start("tcp://localhost:5556", "tcp://localhost:6666", "X")).start();
        new Thread(() -> new Negociador().start("tcp://localhost:5557", "tcp://localhost:6667", "Y")).start();
        //new Thread(() -> new Negociador().start("tcp://localhost:5558", "tcp://localhost:6668", "Z")).start();
    }
}
