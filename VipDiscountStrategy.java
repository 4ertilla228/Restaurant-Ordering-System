package com.restaurant.strategy;

// Student 2 (Alan) - Pattern 4 (Strategy): Concrete Strategy
public class VipDiscountStrategy implements DiscountStrategy {

    @Override
    public double applyDiscount(double totalAmount) {
        return totalAmount * 0.75;
    }

    @Override
    public String getStrategyName() {
        return "VIP Loyalty Discount (25% off)";
    }

    @Override
    public String getCode() {
        return "vip";
    }
}
