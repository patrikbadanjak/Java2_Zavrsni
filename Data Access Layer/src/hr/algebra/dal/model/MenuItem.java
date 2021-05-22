package hr.algebra.dal.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class MenuItem implements Serializable {

    private Item item = null;
    private Integer itemAmount = 0;

    public MenuItem(Item item) {
        this.item = item;
    }

    public MenuItem(Item item, Integer itemAmount) {
        this(item);
        this.itemAmount = itemAmount;
    }

    public Item getItem() {
        return item;
    }

    public Integer getItemAmount() {
        return itemAmount;
    }

    public double getItemTotal() {
        return item.getPrice() * itemAmount;
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeObject(item);
        oos.writeInt(itemAmount.intValue());
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        item = (Item) ois.readObject();
        itemAmount = ois.readInt();
    }

    @Override
    public String toString() {
        return item + " # of items: " + getItemAmount();
    }
}
