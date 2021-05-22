package hr.algebra.dashboard.component;

import hr.algebra.dal.model.Order;
import hr.algebra.dashboard.controller.OrdersTabViewController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;

public class OrderComponent extends VBox {

    private final Order order;

    //<editor-fold desc="FXML variables">
    @FXML
    private Label lblTblNum, lblNumOfPpl, lblOrderStatus, lblTotal;

    @FXML
    private Button btnOrderComplete;
    //</editor-fold>

    private Image check, cross;

    private final Color green, red;

    private final OrdersTabViewController controller;

    public OrderComponent(OrdersTabViewController controller, Order order) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/orderView.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        this.controller = controller;
        this.order = order;

        green = Color.web("#05c19c");
        red = Color.web("#961717");

        try {
            loader.load();
            setLabels();

            check = new Image(getClass().getResourceAsStream("../resources/img/check.png"));
            cross = new Image(getClass().getResourceAsStream("../resources/img/cross.png"));

            setButtonImageAndColor(btnOrderComplete, check, green);
        } catch (IOException ex) {
            System.out.println("Component error: " + ex.getMessage());
        }

    }

    private void setLabels() {
        lblTblNum.setText( String.valueOf(order.getTableID()) );
        lblNumOfPpl.setText( String.valueOf(order.getNumOfPeople()) );
        lblOrderStatus.setText(order.getOrderStatus());
        lblTotal.setText( String.format("%.2f", order.getOrderTotal()) );
    }

    public int getTableID() {
        return this.order.getTableID();
    }

    @FXML
    private void viewOrder() {
        System.out.println("View order");
    }

    @FXML
    private void toggleOrderCompletion() {
        sendSignalToClient();
        toggleButtonStyling();
        removeOrderFromContainer();
    }

    private void removeOrderFromContainer() {
        controller.removeComponent(this);
    }

    private void sendSignalToClient() {
        controller.markOrderAsDone(order);
    }

    private void toggleButtonStyling() {
        if ("Done".equals(order.getOrderStatus())) {
            order.setOrderStatus("Preparing");
            lblOrderStatus.setText("Preparing");
            setButtonImageAndColor(btnOrderComplete, check, green);
        } else if ("Preparing".equals(order.getOrderStatus())) {
            order.setOrderStatus("Done");
            lblOrderStatus.setText("Done");
            setButtonImageAndColor(btnOrderComplete, cross, red);
        }
    }

    private void setButtonImageAndColor(Button button, Image image, Color color) {
        button.setGraphic(new ImageView(image));
        button.setStyle("-fx-background-color:" + toHexString(color));
        button.applyCss();
    }

    private String format(double val) {
        String in = Integer.toHexString((int) Math.round(val * 255));
        return in.length() == 1 ? "0" + in : in;
    }

    public String toHexString(Color value) {
        return "#" + (format(value.getRed()) + format(value.getGreen()) + format(value.getBlue()) + format(value.getOpacity()))
                .toUpperCase();
    }
}
