package main.java.com.restaurant.strategy;


// Alan - Concrete Strategy

// (15% Student Discount)
public class StudentDiscountStrategy implements DiscountStrategy {

    @Override
    public double applyDiscount(double totalAmount) {
        return totalAmount * 0.85;
    }

    @Override
    public String getStrategyName() {
        return "Student Discount (15% off)";
    }
}