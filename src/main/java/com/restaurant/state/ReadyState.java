package com.restaurant.state;

import com.restaurant.model.Order;

// Student 2 (Alan) - Pattern 5 (State): Concrete State 3 (Ready for pickup/delivery)
public class ReadyState implements OrderState {

    @Override
    public void next(Order order) {
        order.setState(new DeliveredState());
    }

    @Override
    public void cancel(Order order) {
        System.out.println("Cannot cancel order #" + order.getOrderId() + " - the food is already prepared!");
    }

    @Override
    public String getStatusName() {
        return "Ready";
    }
}
