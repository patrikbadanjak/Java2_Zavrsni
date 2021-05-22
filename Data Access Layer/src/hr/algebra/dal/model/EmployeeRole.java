package hr.algebra.dal.model;

import java.io.Serializable;

public enum EmployeeRole implements Serializable {
    WAITER(1),
    MANAGER(2);

    private final int roleID;

    EmployeeRole(int roleID) {
        this.roleID = roleID;
    }
    public static EmployeeRole getEmployeeRole(int roleID) {
        switch (roleID) {
            case 1:
                return WAITER;
            case 2:
                return MANAGER;
            default:
                return WAITER;
        }
    }
}
