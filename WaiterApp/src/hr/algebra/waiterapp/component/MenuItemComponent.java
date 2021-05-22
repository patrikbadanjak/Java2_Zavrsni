package hr.algebra.waiterapp.component;

import hr.algebra.dal.model.Item;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class MenuItemComponent extends GridPane implements Serializable {

    private Integer itemAmount = 0;
    private Item item = null;

    //<editor-fold desc="FXML variables">
    @FXML
    private GridPane gridPane;

    @FXML
    private Label lblItemName;

    @FXML
    private Button btnAdd, btnSubtract;

    @FXML
    private TextField tfAmount;
    //</editor-fold>

    public MenuItemComponent(Item item) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/menuItemView.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        this.item = item;

        try {
            loader.load();

            lblItemName.setText(item.toString());

            initButtonActions();
        } catch (IOException ex) {
            System.out.println("Component error: " + ex.getMessage());
        }
    }

    private void initButtonActions() {
        btnAdd.setOnAction(actionEvent -> {
            ++itemAmount;
            tfAmount.setText(itemAmount.toString());
        });

        btnSubtract.setOnAction(actionEvent -> {
            if (itemAmount > 0) {
                --itemAmount;
                tfAmount.setText(itemAmount.toString());
            }
        });
    }

    public String getItemName() {
        return lblItemName.getText();
    }

    public void setItemName(String itemName) {
        lblItemName.setText(itemName);
    }

    public int getItemAmount() {
        return Integer.parseInt(tfAmount.getText());
    }

    public Item getItem() { return this.item; }

    public void resetItemAmount() {
        itemAmount = 0;
        tfAmount.setText(itemAmount.toString());
    }

    public double getTotalPrice() { return itemAmount * item.getPrice(); }

    public Pane getRootPane() {
        return gridPane;
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeInt(itemAmount);
        oos.writeObject(item);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        itemAmount = ois.readInt();
        item = (Item)ois.readObject();
        tfAmount.setText(itemAmount.toString());
    }
}
