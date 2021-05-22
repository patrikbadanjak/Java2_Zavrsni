package hr.algebra.dal.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Restaurant implements Serializable {

    private transient int idRestaurant;
    private transient String restaurantName;
    private transient String restaurantAddress;
    private transient String telephoneNumber;

    public Restaurant(int idRestaurant, String restaurantName, String restaurantAddress, String telephoneNumber) {
        this.restaurantName = restaurantName;
        this.restaurantAddress = restaurantAddress;
        this.telephoneNumber = telephoneNumber;
        this.idRestaurant = idRestaurant;
    }

    public int getIdRestaurant() {
        return idRestaurant;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeInt(idRestaurant);
        oos.writeUTF(restaurantName);
        oos.writeUTF(restaurantAddress);
        oos.writeUTF(telephoneNumber);
    }

    private void readObject(ObjectInputStream ois) throws IOException {
        idRestaurant = ois.readInt();
        restaurantName = ois.readUTF();
        restaurantAddress = ois.readUTF();
        telephoneNumber = ois.readUTF();
    }

    @Override
    public String toString() {
        return restaurantName;
    }
}
