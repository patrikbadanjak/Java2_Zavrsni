package hr.algebra.dal.repository;

import hr.algebra.dal.model.Employee;
import hr.algebra.dal.model.Item;

import java.util.List;
import java.util.Optional;

public interface Repository {
    Optional<Employee> loginUser(String email, String password) throws Exception;
    List<Item> getItems() throws Exception;
    Item getItem(int idItem) throws Exception;
}
