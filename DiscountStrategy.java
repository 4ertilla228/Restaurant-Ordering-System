package com.restaurant.strategy;

// Student 2 (Alan) - Pattern 4 (Strategy): Strategy interface
public interface DiscountStrategy {
    double applyDiscount(double totalAmount);
    String getStrategyName();
    String getCode();
}
