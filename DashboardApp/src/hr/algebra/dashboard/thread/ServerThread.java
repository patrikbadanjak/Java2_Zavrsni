package hr.algebra.dashboard.thread;

import hr.algebra.dal.model.Order;
import hr.algebra.dashboard.utils.ByteUtils;

import java.io.*;
import java.net.*;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingDeque;

public class ServerThread extends Thread{

    public static int SERVER_PORT;
    public static int CLIENT_PORT;
    public static String GROUP;

    static {
        try {
            Properties properties = new Properties();
            String directoryRoot = System.getProperty("user.dir") + "\\DashboardApp\\";

            properties.load(new FileInputStream(new File(directoryRoot + "socket.properties")));
            SERVER_PORT = Integer.parseInt(properties.getProperty("SERVER_PORT"));
            CLIENT_PORT = Integer.parseInt(properties.getProperty("CLIENT_PORT"));
            GROUP = properties.getProperty("GROUP");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error statically loading properties file");
        }
    }

    private final LinkedBlockingDeque<Order> orders = new LinkedBlockingDeque<>();

    public void markOrderAsDone(Order order) { orders.add(order); }

    @Override
    public void run() {
        try (DatagramSocket server = new DatagramSocket(SERVER_PORT)) {
            System.err.println("Server multicasting on port: " + server.getLocalPort());

            while (true) {
                if (!orders.isEmpty()) {

                    byte[] orderBytes = new byte[0];
                    try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                         ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                        oos.writeObject(orders.getFirst());
                        orders.clear();
                        oos.flush();
                        orderBytes = baos.toByteArray();
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.err.println("Error writing object");
                    }

                    byte[] numberOfOrderBytes = ByteUtils.intToByteArray(orderBytes.length);
                    InetAddress groupAddress = InetAddress.getByName(GROUP);
                    DatagramPacket packet =
                            new DatagramPacket(
                                numberOfOrderBytes,
                                numberOfOrderBytes.length,
                                groupAddress,
                                CLIENT_PORT);
                    server.send(packet);

                    packet = new DatagramPacket(orderBytes, orderBytes.length, groupAddress, CLIENT_PORT);
                    server.send(packet);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
