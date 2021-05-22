package hr.algebra.dal.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Item implements Serializable {

    private transient int idItem;
    private transient String itemName;
    private transient IntegerProperty stock;
    private transient DoubleProperty price;

    public Item(int idItem, String itemName, int stock, double price) {
        this.idItem = idItem;
        this.itemName = itemName;
        this.stock = new SimpleIntegerProperty(stock);
        this.price = new SimpleDoubleProperty(price);
    }

    public Item() {
        this.idItem = 0;
        this.itemName = "";
        this.stock = new SimpleIntegerProperty();
        this.price = new SimpleDoubleProperty();
    }

    public void setIdItem(int idItem) {
        this.idItem = idItem;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getIdItem() {
        return idItem;
    }

    public String getItemName() {
        return itemName;
    }

    public int getStock() {
        return stock.get();
    }

    public void setStock(int stock) throws RuntimeException {
        if (stock < 0)
            throw new RuntimeException("Stock cannot be a negative number.");

        this.stock.set(stock);
    }

    public void removeFromStock(int amount) throws RuntimeException {
        if (amount > this.stock.get())
            throw new RuntimeException("Cannot remove more than there is in stock.");

        this.stock.subtract(amount);
    }

    public double getPrice() {
        return price.get();
    }

    public void setPrice(double price) throws RuntimeException {
        if (price < 0)
            throw new RuntimeException("Price cannot be a negative number.");

        this.price.set(price);
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeInt(idItem);
        oos.writeUTF(itemName);
        oos.writeInt(stock.get());
        oos.writeDouble(price.get());
    }

    private void readObject(ObjectInputStream ois) throws IOException {
        idItem = ois.readInt();
        itemName = ois.readUTF();
        stock = new SimpleIntegerProperty(ois.readInt());
        price = new SimpleDoubleProperty(ois.readDouble());
    }

    @Override
    public String toString() {
        return itemName + " (" + price.get() + "kn)";
    }
}
