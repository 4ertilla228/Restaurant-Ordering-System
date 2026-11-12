package com.restaurant.state;

import com.restaurant.model.Order;

// Student 2 (Alan) - Pattern 5 (State): Concrete State 5 (Cancelled, final)
public class CancelledState implements OrderState {

    @Override
    public void next(Order order) {
        System.out.println("Order #" + order.getOrderId() + " is cancelled. No further state transitions.");
    }

    @Override
    public void cancel(Order order) {
        System.out.println("Order #" + order.getOrderId() + " is already cancelled.");
    }

    @Override
    public String getStatusName() {
        return "Cancelled";
    }

    @Override
    public boolean canAdvance() {
        return false;
    }
}
