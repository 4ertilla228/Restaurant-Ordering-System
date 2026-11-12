package com.restaurant.state;

import com.restaurant.model.Order;

// Student 2 (Alan) - Pattern 5 (State): Concrete State 2 (Cooking)
public class CookingState implements OrderState {

    @Override
    public void next(Order order) {
        order.setState(new ReadyState());
    }

    @Override
    public void cancel(Order order) {
        System.out.println("Cannot cancel order #" + order.getOrderId() + " - it is already being cooked!");
    }

    @Override
    public String getStatusName() {
        return "Cooking";
    }
}
