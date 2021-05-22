package hr.algebra.dal.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Employee implements Serializable {
    private transient int idEmployee;
    private transient String firstName;
    private transient String lastName;
    private transient String email;
    private transient EmployeeRole role;

    public Employee(String firstName, String lastName, String email, EmployeeRole role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
    }

    public Employee(int idEmployee, String firstName, String lastName, String email, EmployeeRole role) {
        this(firstName, lastName, email, role);
        this.idEmployee = idEmployee;
    }

    public int getIdEmployee() {
        return idEmployee;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public EmployeeRole getRole() {
        return role;
    }

    public void setRole(EmployeeRole role) {
        this.role = role;
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeInt(idEmployee);
        oos.writeUTF(firstName);
        oos.writeUTF(lastName);
        oos.writeUTF(email);
        oos.writeObject(role);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        idEmployee = ois.readInt();
        firstName = ois.readUTF();
        lastName = ois.readUTF();
        email = ois.readUTF();
        role = (EmployeeRole)ois.readObject();
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
