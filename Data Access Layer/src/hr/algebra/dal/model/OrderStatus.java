package hr.algebra.dal.model;

public enum OrderStatus {
    PREPARING(1),
    DONE(2),
    DELIVERED(3);

    private final int statusID;

    OrderStatus(int statusID) {
        this.statusID = statusID;
    }

    public static OrderStatus getOrderStatus(int statusID) {
        switch (statusID) {
            case 1:
                return PREPARING;
            case 2:
                return DONE;
            case 3:
                return DELIVERED;
            default:
                return PREPARING;
        }
    }
}
