package com.restaurant.state;

import com.restaurant.model.Order;

// Student 2 (Alan) - Pattern 5 (State): Concrete State 1 (Created)
public class CreatedState implements OrderState {

    @Override
    public void next(Order order) {
        order.setState(new CookingState());
    }

    @Override
    public void cancel(Order order) {
        System.out.println("Order #" + order.getOrderId() + " has been cancelled before cooking.");
        order.setState(new CancelledState());
    }

    @Override
    public String getStatusName() {
        return "Created";
    }

    @Override
    public boolean canCancel() {
        return true;
    }
}
