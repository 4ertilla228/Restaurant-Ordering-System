package main.java.com.restaurant.observer;
//Нурчик
// Student 1 (Nurmukhamadzhon) - Pattern 3: Observer Interface
public interface OrderObserver {
    void update(int orderId, String newStatus);
}