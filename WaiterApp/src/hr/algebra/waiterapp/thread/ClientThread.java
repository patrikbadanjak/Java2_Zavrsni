package hr.algebra.waiterapp.thread;

import hr.algebra.dal.model.Order;
import hr.algebra.waiterapp.controller.MainViewController;
import hr.algebra.waiterapp.utils.ByteUtils;
import javafx.application.Platform;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Properties;

public class ClientThread extends Thread{

    public static int CLIENT_PORT;
    public static String GROUP;

    static {
        try {
            Properties properties = new Properties();
            String directoryRoot = System.getProperty("user.dir") + "\\WaiterApp\\";

            properties.load(new FileInputStream(new File(directoryRoot + "socket.properties")));
            CLIENT_PORT = Integer.parseInt( properties.getProperty("CLIENT_PORT") );
            GROUP = properties.getProperty("GROUP");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error parsing properties file in Waiter App");
        }
    }

    private final MainViewController controller;

    public ClientThread(MainViewController controller) {
        this.controller = controller;
    }

    @Override
    public void run() {
        try (MulticastSocket client = new MulticastSocket(CLIENT_PORT)) {
            InetAddress groupAddress = InetAddress.getByName(GROUP);
            System.err.println(controller.hashCode() + " joining group");
            client.joinGroup(groupAddress);

            while (true) {
                System.err.println(controller.hashCode() + " listening...");

                byte[] numberOfOrderBytes = new byte[4];
                DatagramPacket packet = new DatagramPacket(numberOfOrderBytes, numberOfOrderBytes.length);
                client.receive(packet);
                int length = ByteUtils.byteArrayToInt(numberOfOrderBytes);

                byte[] orderBytes = new byte[length];
                packet = new DatagramPacket(orderBytes, orderBytes.length);
                client.receive(packet);
                try (ByteArrayInputStream baos = new ByteArrayInputStream(orderBytes);
                     ObjectInputStream oos = new ObjectInputStream(baos)) {
                    Order order = (Order) oos.readObject();
                    Platform.runLater(() -> {
                        controller.receiveOrder(order);
                    });
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
