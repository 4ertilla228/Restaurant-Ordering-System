package main.java.com.restaurant.state;

import main.java.com.restaurant.Model.Order;

// Alan - Concrete State 1
// (Created)
public class CreatedState implements OrderState {

    @Override
    public void next(Order order) {
        order.setState(new CookingState());
    }

    @Override
    public void cancel(Order order) {
        System.out.println("Order #" + order.getOrderId() + " has been cancelled before cooking.");
    }

    @Override
    public String getStatusName() {
        return "Created";
    }
}