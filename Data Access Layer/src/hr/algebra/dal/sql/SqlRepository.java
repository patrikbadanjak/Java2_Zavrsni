package hr.algebra.dal.sql;

import hr.algebra.dal.model.Employee;
import hr.algebra.dal.model.EmployeeRole;
import hr.algebra.dal.model.Item;
import hr.algebra.dal.repository.Repository;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

public class SqlRepository implements Repository {

    private static final String LOGIN_USER = "{ CALL LoginUser(?, ?) }";
    private static final String GET_ITEMS = "{ CALL GetItems }";
    private static final String GET_ITEM = "{ CALL GetItem(?) }";

    @Override
    public Optional<Employee> loginUser(String email, String password) throws Exception {
        DataSource ds = DataSourceSingleton.getInstance();

        try (Connection con = ds.getConnection();
             CallableStatement stmt = con.prepareCall(LOGIN_USER)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Employee(
                            rs.getInt(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4),
                            EmployeeRole.getEmployeeRole(rs.getInt(5))
                    ));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Item> getItems() throws Exception {
        List<Item> items = new ArrayList<>();

        DataSource ds = DataSourceSingleton.getInstance();
        try (Connection con = ds.getConnection();
             CallableStatement stmt = con.prepareCall(GET_ITEMS)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                items.add(new Item(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getShort(3),
                        rs.getDouble(4)
                ));
            }
        }

        return items;
    }

    @Override
    public Item getItem(int idItem) throws Exception {
        DataSource ds = DataSourceSingleton.getInstance();
        Item item = new Item();

        try (Connection con = ds.getConnection();
            CallableStatement stmt = con.prepareCall(GET_ITEM)) {

            stmt.setInt(1, idItem);

            ResultSet rs = stmt.executeQuery();
            rs.next();

            item.setIdItem(idItem);
            item.setItemName(rs.getString("ItemName"));
            item.setStock(rs.getInt("Stock"));
            item.setPrice(rs.getDouble("Price"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return item;
    }
}
