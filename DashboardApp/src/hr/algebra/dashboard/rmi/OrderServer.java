package hr.algebra.dashboard.rmi;

import hr.algebra.dal.model.Order;
import hr.algebra.dashboard.controller.OrdersTabViewController;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class OrderServer {

    //<editor-fold desc="RMI variables">
    private static final String CLIENT_NAME = "Client";
    private static final String RMI_CLIENT = "client";
    private static final String RMI_SERVER = "server";
    private static final int REMOTE_PORT = 1099;
    private static final int RANDOM_PORT_HINT = 0;
    //</editor-fold>

    private OrderService server;
    private OrderService client;
    private Registry registry;

    private final OrdersTabViewController ordersController;

    public OrderServer(OrdersTabViewController ordersController) {
        this.ordersController = ordersController;
        publishServer();
        waitForClient();
    }

    private void publishServer() {
        server = new OrderService() {
            @Override
            public void sendOrder(Order order) throws RemoteException {
                ordersController.postOrder(order);
            }
        };

        try {
            registry = LocateRegistry.createRegistry(REMOTE_PORT);
            OrderService stub = (OrderService) UnicastRemoteObject.exportObject(server, RANDOM_PORT_HINT);
            registry.rebind(RMI_SERVER, stub);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void waitForClient() {
        Thread thread = new Thread(() -> {
           while (client == null) {
               try {
                   client = (OrderService) registry.lookup(RMI_CLIENT);
               } catch (RemoteException | NotBoundException e) {
                   System.out.println("Waiting for client");
               }
               System.out.println(client);

               try {
                   Thread.sleep(2000);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
        });
        thread.setDaemon(true);
        thread.start();
    }
}
