package hr.algebra.dal.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Table implements Serializable {

    private transient int idRestaurant;
    private transient Restaurant restaurant;
    private transient int tableNumber;

    public Table(int idRestaurant, Restaurant restaurant, int tableNumber) {
        this.idRestaurant = idRestaurant;
        this.restaurant = restaurant;
        this.tableNumber = tableNumber;
    }

    public int getIdRestaurant() {
        return idRestaurant;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeInt(idRestaurant);
        oos.writeObject(restaurant);
        oos.writeInt(tableNumber);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        idRestaurant = ois.readInt();
        restaurant = (Restaurant)ois.readObject();
        tableNumber = ois.readInt();
    }

    @Override
    public String toString() {
        return "Table " + getTableNumber();
    }
}
