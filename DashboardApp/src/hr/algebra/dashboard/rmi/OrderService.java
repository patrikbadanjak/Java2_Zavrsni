package hr.algebra.dashboard.rmi;

import hr.algebra.dal.model.Order;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface OrderService extends Remote {
    void sendOrder(Order order) throws RemoteException;
}
