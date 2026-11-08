package main.java.com.restaurant.Model;

import main.java.com.restaurant.factory.Meal;
import main.java.com.restaurant.observer.OrderObserver;
import main.java.com.restaurant.state.CreatedState;
import main.java.com.restaurant.state.OrderState;
import main.java.com.restaurant.strategy.DiscountStrategy;
import main.java.com.restaurant.strategy.NoDiscountStrategy;

import java.util.ArrayList;
import java.util.List;

// Shared Domain Model connecting Factory/Decorator items, Observer notifications, State & Strategy
public class Order {
    private final int orderId;
    private final List<Meal> meals = new ArrayList<>();
    private final List<OrderObserver> observers = new ArrayList<>();
    private OrderState state;
    private DiscountStrategy discountStrategy;

    public Order(int orderId) {
        this.orderId = orderId;
        this.state = new CreatedState();
        this.discountStrategy = new NoDiscountStrategy();
    }

    public void addMeal(Meal meal) {
        meals.add(meal);
    }

    public List<Meal> getMeals() {
        return meals;
    }

    public int getOrderId() {
        return orderId;
    }

    // Observer pattern management
    public void addObserver(OrderObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(OrderObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (OrderObserver observer : observers) {
            observer.update(orderId, state.getStatusName());
        }
    }

    // State pattern transitions (triggers Observer automatically)
    public void setState(OrderState newState) {
        this.state = newState;
        notifyObservers();
    }

    public void nextState() {
        state.next(this);
    }

    public void cancelOrder() {
        state.cancel(this);
    }

    public String getStatus() {
        return state.getStatusName();
    }

    // Strategy pattern calculation
    public void setDiscountStrategy(DiscountStrategy discountStrategy) {
        this.discountStrategy = discountStrategy;
    }

    public DiscountStrategy getDiscountStrategy() {
        return discountStrategy;
    }

    public double calculateRawTotal() {
        double sum = 0;
        for (Meal meal : meals) {
            sum += meal.getCost();
        }
        return sum;
    }

    public double calculateFinalTotal() {
        return discountStrategy.applyDiscount(calculateRawTotal());
    }
}