package main.java.com.restaurant.state;

import main.java.com.restaurant.Model.Order;

// Student 2 (Alan) - Pattern 5: Concrete State 3 (Ready for pickup/delivery)
public class ReadyState implements OrderState {

    @Override
    public void next(Order order) {
        order.setState(new DeliveredState());
    }

    @Override
    public void cancel(Order order) {
        System.out.println("Невозможно отменить заказ #" + order.getOrderId() + " — еда уже приготовлена!");
    }

    @Override
    public String getStatusName() {
        return "Ready";
    }
}