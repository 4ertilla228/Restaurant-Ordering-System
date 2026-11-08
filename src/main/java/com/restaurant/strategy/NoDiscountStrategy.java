package main.java.com.restaurant.strategy;

// Alan - Concrete Strategy

// Standard Price
public class NoDiscountStrategy implements DiscountStrategy {

    @Override
    public double applyDiscount(double totalAmount) {
        return totalAmount;
    }

    @Override
    public String getStrategyName() {
        return "Regular Price (0% off)";
    }
}