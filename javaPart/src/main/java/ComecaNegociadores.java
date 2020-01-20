import catalogo.Negociador.Negociador;

public class ComecaNegociadores {

    public static void main(String[] args) {
        new Thread(() -> new Negociador().start("tcp://*:5556", "tcp://*:6666", "X")).start();
        new Thread(() -> new Negociador().start("tcp://*:5557", "tcp://*:6667", "Y")).start();
        new Thread(() -> new Negociador().start("tcp://*:5558", "tcp://*:6668", "Z")).start();
    }
}
