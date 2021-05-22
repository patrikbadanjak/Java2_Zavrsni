package hr.algebra.waiterapp.rmi;

import com.sun.jndi.rmi.registry.RegistryContextFactory;
import hr.algebra.dal.model.Order;
import hr.algebra.dashboard.rmi.OrderService;
import hr.algebra.waiterapp.controller.MainViewController;
import hr.algebra.waiterapp.jndi.InitialDirContextCloseable;

import javax.naming.Context;
import javax.naming.NamingException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;

public class OrderClient {

    //<editor-fold desc="RMI variables">
    private static final String SERVER_NAME = "Server";
    private static final String RMI_CLIENT = "client";
    private static final String RMI_SERVER = "server";
    private static final int REMOTE_PORT = 1099;
    private static final int RANDOM_PORT_HINT = 0;
    //</editor-fold>

    public static final String RMI_URL = "rmi://localhost:1099";

    private OrderService client;
    private OrderService server;
    private Registry registry;

    private final MainViewController controller;

    public OrderClient(MainViewController controller) {
        this.controller = controller;
        publishClient();
        fetchServer();
    }

    private void publishClient() {
        client = new OrderService() {
            @Override
            public void sendOrder(Order order) throws RemoteException {
                controller.receiveOrder(order);
            }
        };

        try {
            registry = LocateRegistry.getRegistry(REMOTE_PORT);
            OrderService stub = (OrderService) UnicastRemoteObject.exportObject(client, RANDOM_PORT_HINT);
            registry.rebind(RMI_CLIENT, stub);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void fetchServer() {
        final Hashtable<String, String> properties = new Hashtable<>();
        properties.put(Context.INITIAL_CONTEXT_FACTORY, RegistryContextFactory.class.getName());
        properties.put(Context.PROVIDER_URL, RMI_URL);

        try (InitialDirContextCloseable context = new InitialDirContextCloseable(properties)) {
            server = (OrderService) context.lookup(RMI_SERVER);
        } catch (NamingException e) {
            System.out.println("Error looking up RMI server: " + e.getMessage());
        }
    }

    public void sendOrder(Order order) {
        try {
            server.sendOrder(order);
        } catch (RemoteException e) {
            System.out.println("Unable to send clients order: " + e.getMessage());
        }
    }
}
