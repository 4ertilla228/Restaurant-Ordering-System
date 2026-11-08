package main.java.com.restaurant.strategy;

// Alan pattern 4 - Strategy Interface

public interface DiscountStrategy {
    double applyDiscount(double totalAmount);
    String getStrategyName();
}