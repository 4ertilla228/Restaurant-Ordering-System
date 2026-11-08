package main.java.com.restaurant.state;
import main.java.com.restaurant.Model.Order;

// Alan - Pattern 5: State Interface
public interface OrderState {
    void next(Order order);
    void cancel(Order order);
    String getStatusName();
}