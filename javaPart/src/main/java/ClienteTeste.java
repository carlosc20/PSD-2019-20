import org.zeromq.ZMQ;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class ClienteTeste {
    public void start(){
        ZContext context = new ZContext();
        ZMQ.Socket connector = context.createSocket(ZMQ.REQ);
        ZMQ.Socket rep1 = context.createSocket(ZMQ.REP);
        ZMQ.Socket rep2 = context.createSocket(ZMQ.REP);
        ZMQ.Socket rep3 = context.createSocket(ZMQ.REP);
        //ZMQ.Socket subscriber = context.createSocket(SocketType.SUB);
        rep1.connect("tcp://localhost:5556");
        rep1.setIdentity("X".getBytes(ZMQ.CHARSET));
        rep2.connect("tcp://localhost:5557");
        rep2.setIdentity("Y".getBytes(ZMQ.CHARSET));
        rep3.connect("tcp://localhost:5558");
        rep3.setIdentity("Z".getBytes(ZMQ.CHARSET));

        connector.connect("tcp://localhost:5555");
        //subscriber.connect("tcp://localhost:"+);

        String room = "default";
        //subscriber.subscribe(room.getBytes());

        HashSet<String> rooms = new HashSet<>();
        rooms.add("default");
        rooms.add("miei");
        rooms.add("sd");
        rooms.add("cd");

        Runnable receiving1 = () -> {
            while(true) {
                byte[] reply1 = rep1.recv(0);
                System.out.println(new String(reply1));
                rep1.send("okay");
            }
        };
        Runnable receiving2 = () -> {
            while(true) {
                byte[] reply2 = rep2.recv(0);
                System.out.println(new String(reply2));
                rep2.send("okay");
            }
        };

        Runnable receiving3 = () -> {
            while(true) {
                byte[] reply3 = rep3.recv(0);
                System.out.println(new String(reply3));
                rep3.send("okey");
            }
        };
        new Thread(receiving1).start();
        new Thread(receiving2).start();
        new Thread(receiving3).start();
        while(true) {
            System.out.println("Sending");
            connector.setIdentity("Hello".getBytes(ZMQ.CHARSET));
            connector.send("connect");
            String reply = connector.recvStr();
            System.out.println(reply);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
