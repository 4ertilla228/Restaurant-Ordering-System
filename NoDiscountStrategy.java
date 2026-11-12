package com.restaurant.strategy;

// Student 2 (Alan) - Pattern 4 (Strategy): Concrete Strategy
public class NoDiscountStrategy implements DiscountStrategy {

    @Override
    public double applyDiscount(double totalAmount) {
        return totalAmount * 1.0;
    }

    @Override
    public String getStrategyName() {
        return "Regular Price (0% off)";
    }

    @Override
    public String getCode() {
        return "none";
    }
}
