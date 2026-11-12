package com.restaurant.strategy;

// Student 2 (Alan) - Pattern 4 (Strategy): Concrete Strategy
public class StudentDiscountStrategy implements DiscountStrategy {

    @Override
    public double applyDiscount(double totalAmount) {
        return totalAmount * 0.85;
    }

    @Override
    public String getStrategyName() {
        return "Student Discount (15% off)";
    }

    @Override
    public String getCode() {
        return "student";
    }
}
