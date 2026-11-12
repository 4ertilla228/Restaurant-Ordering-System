package com.restaurant.state;

import com.restaurant.model.Order;

// Student 2 (Alan) - Pattern 5 (State): Concrete State 4 (Delivered, final)
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

    @Override
    public boolean canAdvance() {
        return false;
    }
}
