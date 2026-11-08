package main.java.com.restaurant.strategy;

// Alan Concrete
// (25% VIP Discount)
public class VipDiscountStrategy implements DiscountStrategy {

    @Override
    public double applyDiscount(double totalAmount) {
        return totalAmount * 0.75;
    }

    @Override
    public String getStrategyName() {
        return "VIP Loyalty Discount (25% off)";
    }
}