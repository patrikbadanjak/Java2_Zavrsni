package hr.algebra.waiterapp.component;

import hr.algebra.dal.model.Order;
import hr.algebra.waiterapp.controller.MainViewController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class PendingOrderComponent extends GridPane {

    private final Order order;

    private final MainViewController controller;

    //<editor-fold desc="FXML variables">
    @FXML
    private Label lblTableNum, lblOrderStatus;

    @FXML
    private Button btnDelivered;
    //</editor-fold>

    public PendingOrderComponent(MainViewController controller, Order order) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/pendingOrderView.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        this.controller = controller;
        this.order = order;

        try {
            loader.load();

            lblTableNum.setText(String.valueOf( order.getTableID() ));
            lblOrderStatus.setText(order.getOrderStatus());
        } catch (IOException ex) {
            System.out.println("Component error: " + ex.getMessage());
        }
    }

    public int getTableNumber() {
        return this.order.getTableID();
    }

    public void setOrderStatus(boolean isDone) {
        if (isDone) {
            this.order.setOrderStatus("Done");
            lblOrderStatus.setText("Done");
            btnDelivered.setDisable(false);
        } else {
            this.order.setOrderStatus("Preparing");
            lblOrderStatus.setText("Preparing");
            btnDelivered.setDisable(true);
        }
    }

    @FXML
    private void markOrderAsDelivered() {
        controller.moveOrderToCompleted(order);
        hideDeliveredButton();
        System.out.println("Delivered");
    }

    private void hideDeliveredButton() {
        btnDelivered.setVisible(false);
    }
}
