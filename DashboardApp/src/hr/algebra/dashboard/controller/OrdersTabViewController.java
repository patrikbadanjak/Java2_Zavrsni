package hr.algebra.dashboard.controller;

import hr.algebra.dal.model.Order;
import hr.algebra.dashboard.component.OrderComponent;
import hr.algebra.dashboard.rmi.OrderServer;
import hr.algebra.dashboard.thread.ServerThread;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.FlowPane;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class OrdersTabViewController implements Initializable {

    public static final String SERVER_NAME = "Server";

    private ServerThread serverThread;

    private OrderServer orderServer;

    @FXML
    private FlowPane orderContainer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        orderServer = new OrderServer(this);
        initServerThread();
    }

    private void initServerThread() {
        serverThread = new ServerThread();
        serverThread.setDaemon(true);
        serverThread.start();
    }

    public void postOrder(Order order) {
        Platform.runLater(() ->
                orderContainer.getChildren().add(new OrderComponent(this, order))
        );
    }

    public void markOrderAsDone(Order order) {
        serverThread.markOrderAsDone(order);
    }

    public void removeComponent(OrderComponent orderComponent) {
        orderContainer.getChildren().remove(orderComponent);
    }
}
