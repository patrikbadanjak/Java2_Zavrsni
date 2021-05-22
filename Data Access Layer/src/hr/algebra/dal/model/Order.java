package hr.algebra.dal.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient int idOrder = -1;
    private transient int tableID;
    private transient short numOfPeople;
    private transient String orderStatus;
    private transient List<MenuItem> orderedItems;
    private transient LocalDateTime timeOfOrder;

    public Order(int tableID, short numOfPeople, String orderStatus, List<MenuItem> orderedItems, LocalDateTime timeOfOrder) {
        this.tableID = tableID;
        this.numOfPeople = numOfPeople;
        this.orderStatus = orderStatus;
        this.orderedItems = orderedItems;
        this.timeOfOrder = timeOfOrder;
    }

    public Order(int idOrder, int tableID, short numOfPeople, String orderStatus, List<MenuItem> orderedItems, LocalDateTime timeOfOrder) {
        this(tableID, numOfPeople, orderStatus, orderedItems, timeOfOrder);
        this.idOrder = idOrder;
    }

    public Order(int tableID, short numOfPeople, List<MenuItem> orderedItems) {
        this(tableID, numOfPeople, "Preparing", orderedItems, LocalDateTime.now());
    }

    public Order() {
        this(0, (short) 0, "Preparing", new ArrayList<>(), LocalDateTime.now());
    }

    public void setIdOrder(int idOrder) {
        this.idOrder = idOrder;
    }

    public void setTableID(int tableID) {
        this.tableID = tableID;
    }

    public void setNumOfPeople(short numOfPeople) {
        this.numOfPeople = numOfPeople;
    }

    public void setOrderedItems(List<MenuItem> orderedItems) {
        this.orderedItems = orderedItems;
    }

    public void setTimeOfOrder(LocalDateTime timeOfOrder) {
        this.timeOfOrder = timeOfOrder;
    }

    public int getTableID() {
        return tableID;
    }

    public short getNumOfPeople() {
        return numOfPeople;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public List<MenuItem> getOrderedItems() {
        return orderedItems;
    }

    public double getOrderTotal() {
        double total = 0d;

        for (MenuItem item : orderedItems)
            total += item.getItemTotal();

        return total;
    }

    public LocalDateTime getTimeOfOrder() {
        return timeOfOrder;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeInt(idOrder);
        oos.writeInt(tableID);
        oos.writeShort(numOfPeople);
        oos.writeUTF(orderStatus);
        oos.writeObject(orderedItems);
        oos.writeUTF(timeOfOrder.toString());
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        idOrder = ois.readInt();
        tableID = ois.readInt();
        numOfPeople = ois.readShort();
        orderStatus = ois.readUTF();
        orderedItems = (List<MenuItem>) ois.readObject();
        timeOfOrder = LocalDateTime.parse(ois.readUTF());
    }
}
