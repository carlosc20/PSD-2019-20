import org.zeromq.ZMQ;
import org.zeromq.ZContext;

import java.util.ArrayList;
import java.util.HashSet;

public class ClienteTeste {
    public void start(){
        ZContext context = new ZContext();
        ZMQ.Socket connector1 = context.createSocket(ZMQ.REQ);
        ZMQ.Socket connector2 = context.createSocket(ZMQ.REQ);
        ZMQ.Socket rep1 = context.createSocket(ZMQ.ROUTER);
        ZMQ.Socket rep2 = context.createSocket(ZMQ.ROUTER);
        ZMQ.Socket rep3 = context.createSocket(ZMQ.ROUTER);

        ZMQ.Socket pub1 = context.createSocket(ZMQ.PUB);
        ZMQ.Socket pub2 = context.createSocket(ZMQ.PUB);

        ZMQ.Socket sub1 = context.createSocket(ZMQ.SUB);
        ZMQ.Socket sub2 = context.createSocket(ZMQ.SUB);

        //ZMQ.Socket subscriber = context.createSocket(SocketType.SUB);
        rep1.connect("tcp://localhost:5556");
        //rep1.setIdentity("X".getBytes(ZMQ.CHARSET));
        rep2.connect("tcp://localhost:5557");
        //rep2.setIdentity("Y".getBytes(ZMQ.CHARSET));
        rep3.connect("tcp://localhost:5558");
        //rep3.setIdentity("Z".getBytes(ZMQ.CHARSET));

        pub1.connect("tcp://localhost:6666");
        pub2.connect("tcp://localhost:6666");

        sub1.connect("tcp://localhost:6667");
        sub2.connect("tcp://localhost:6667");



        connector1.connect("tcp://localhost:5555");
        connector2.connect("tcp://localhost:5555");
        //subscriber.connect("tcp://localhost:"+);

        Runnable publishA = () -> {
            while(true) {
                pub1.sendMore("fronted");
                pub1.send("okay client3");
            }
        };

        Runnable publishB = () -> {
            while(true) {
                pub2.sendMore("fronted");
                pub2.sendMore("cliente4");
                pub2.send("okay client4");
            }
        };

        Runnable receiving1 = () -> {
            while(true) {
                byte[] sender = rep1.recv(0);
                byte[] cliente = rep1.recv(0);
                byte[] message = rep1.recv(0);
                System.out.println("received at 1");
                System.out.println("Sender: " + new String(sender)+ "Cliente: " + new String(cliente) +" "+ new String(message));
                rep1.sendMore(sender);
                rep1.sendMore(cliente);
                rep1.send("".getBytes(), ZMQ.SNDMORE);
                rep1.send("okay");
            }
        };
        Runnable receiving2 = () -> {
            while(true) {
                byte[] sender = rep2.recv(0);
                byte[] cliente = rep2.recv(0);
                byte[] message = rep2.recv(0);
                System.out.println("received at 2");
                System.out.println("Sender: " + new String(sender)+ "Cliente: " + new String(cliente) +" "+ new String(message));
                rep2.sendMore(sender);
                rep2.sendMore(cliente);
                rep2.send("".getBytes(), ZMQ.SNDMORE);
                rep2.send("okay");
            }
        };

        Runnable receiving3 = () -> {
            while(true) {
                byte[] sender = rep3.recv(0);
                byte[] cliente = rep3.recv(0);
                byte[] message = rep3.recv(0);
                System.out.println("received at 3");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Sender: " + new String(sender)+ " Cliente: " + new String(cliente) +" "+ new String(message));
                rep3.sendMore(sender);
                rep3.sendMore(cliente);
                rep3.send("".getBytes(), ZMQ.SNDMORE);
                rep3.send("okay");
            }
        };

        Runnable client1 = () ->{
            while(true) {
                connector1.setIdentity("2".getBytes(ZMQ.CHARSET));
                connector1.send("connect1");
                String reply = connector1.recvStr();
                System.out.println(reply + " at client 1");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Runnable client2 = () ->{
            while(true) {
                connector2.setIdentity("2".getBytes(ZMQ.CHARSET));
                connector2.send("connect2");
                String reply = connector2.recvStr();
                System.out.println(reply + " at client 2");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Runnable client3 = () ->{
            sub1.subscribe("3");
            while(true) {
                System.out.println("Sou o cliente 3");
                byte[] rec = sub1.recv();
                System.out.println(new String(rec));
            }
        };

        Runnable client4 = () ->{
            sub2.subscribe("4");
            while(true) {
                System.out.println("Sou o cliente 4");
                byte[] rec = sub2.recv();
                System.out.println(new String(rec));
            }
        };

        new Thread(receiving1).start();
        new Thread(receiving2).start();
        new Thread(receiving3).start();
        new Thread(client1).start();
        new Thread(client2).start();
        new Thread(publishA).start();
        //new Thread(publishB).start();
       // new Thread(client3).start();
        //new Thread(client4).start();
    }


    public static void main(String[] args) {
        //Portas onde o cliente se conecta para receber subscrições
        ArrayList<Integer> subPorts = new ArrayList<>();
        //Portas onde o cliente se conecta para enviar publicações
        ArrayList<Integer> pubPorts = new ArrayList<>();
        for(int i = 0; i<4; i++){
            pubPorts.add(10000+i);
            subPorts.add(20000 + i);
        }
        new ClienteTeste().start();
    }
}
