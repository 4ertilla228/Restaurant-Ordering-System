package main.java.com.restaurant.state;

import main.java.com.restaurant.Model.Order;

// Student 2 (Alan) - Pattern 5: Concrete State 4 (Completed/Delivered)
public class DeliveredState implements OrderState {

    @Override
    public void next(Order order) {
        System.out.println("Order #" + order.getOrderId() + " is already delivered. No further state transitions.");
    }

    @Override
    public void cancel(Order order) {
        System.out.println("Cannot cancel a delivered order.");
    }

    @Override
    public String getStatusName() {
        return "Delivered";
    }
}