package main.java.com.restaurant.state;
import main.java.com.restaurant.Model.Order;

// Alan  Concrete State 2 (Cooking)
public class CookingState implements OrderState {

    @Override
    public void next(Order order) {
        order.setState(new ReadyState());
    }

    @Override
    public void cancel(Order order) {
        System.out.println("Невозможно отменить заказ #" + order.getOrderId() + " — он уже готовится!");
    }

    @Override
    public String getStatusName() {
        return "Cooking";
    }
}