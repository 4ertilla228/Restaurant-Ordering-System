package com.restaurant.model;

import com.restaurant.factory.Meal;
import com.restaurant.observer.OrderObserver;
import com.restaurant.state.CreatedState;
import com.restaurant.state.OrderState;
import com.restaurant.strategy.DiscountStrategy;
import com.restaurant.strategy.NoDiscountStrategy;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Shared domain model: joins Factory/Decorator (items), Observer (listeners), State (lifecycle) and Strategy (price).
public class Order {
    private final int orderId;
    private final String customerName;
    private final List<OrderItem> items = new ArrayList<>();
    private final List<OrderObserver> observers = new ArrayList<>();
    private final List<StatusChange> history = new ArrayList<>();
    private OrderState state;
    private DiscountStrategy discountStrategy;

    public Order(int orderId, String customerName) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.state = new CreatedState();
        this.discountStrategy = new NoDiscountStrategy();
        history.add(new StatusChange(state.getStatusName(), Instant.now()));
    }

    // ---- items (Factory + Decorator results) ----
    public void addItem(String mealType, List<String> toppings, Meal meal) {
        items.add(new OrderItem(mealType, List.copyOf(toppings), meal));
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public List<Meal> getMeals() {
        List<Meal> meals = new ArrayList<>();
        for (OrderItem item : items) {
            meals.add(item.meal());
        }
        return meals;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    // ---- Observer ----
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

    // ---- State (every transition notifies the Observers) ----
    public void setState(OrderState newState) {
        this.state = newState;
        history.add(new StatusChange(newState.getStatusName(), Instant.now()));
        notifyObservers();
    }

    // Used only when loading an order from the database: no notifications are sent.
    public void restoreState(OrderState restored, List<StatusChange> restoredHistory) {
        this.state = restored;
        this.history.clear();
        this.history.addAll(restoredHistory);
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

    public boolean canAdvance() {
        return state.canAdvance();
    }

    public boolean canCancel() {
        return state.canCancel();
    }

    public List<StatusChange> getHistory() {
        return Collections.unmodifiableList(history);
    }

    // ---- Strategy ----
    public void setDiscountStrategy(DiscountStrategy discountStrategy) {
        this.discountStrategy = discountStrategy;
    }

    public DiscountStrategy getDiscountStrategy() {
        return discountStrategy;
    }

    public double calculateRawTotal() {
        double sum = 0;
        for (OrderItem item : items) {
            sum += item.meal().getCost();
        }
        return sum;
    }

    public double calculateFinalTotal() {
        return discountStrategy.applyDiscount(calculateRawTotal());
    }
}
