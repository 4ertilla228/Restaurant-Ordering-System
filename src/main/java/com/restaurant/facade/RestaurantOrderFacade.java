package main.java.com.restaurant.facade;
// Алан

import main.java.com.restaurant.decorator.BaconDecorator;
import main.java.com.restaurant.decorator.ExtraCheeseDecorator;
import main.java.com.restaurant.decorator.JalapenoDecorator;
import main.java.com.restaurant.factory.Meal;
import main.java.com.restaurant.factory.MealFactory;
import main.java.com.restaurant.Model.Order;
import main.java.com.restaurant.observer.CustomerNotificationListener;
import main.java.com.restaurant.observer.KitchenDisplayListener;
import main.java.com.restaurant.strategy.DiscountStrategy;

import java.util.List;

// Student 2 (Alan) - Pattern 6: Facade coordinating all 6 patterns
public class RestaurantOrderFacade {
    private final MealFactory mealFactory;
    private int orderCounter = 100;

    public RestaurantOrderFacade() {
        this.mealFactory = new MealFactory();
    }

    public Order createOrder(String customerName, DiscountStrategy discountStrategy) {
        Order order = new Order(++orderCounter);
        order.setDiscountStrategy(discountStrategy);

        // Attach default observers (Customer & Kitchen)
        order.addObserver(new CustomerNotificationListener(customerName));
        order.addObserver(new KitchenDisplayListener());

        System.out.println("\n=== Created Order #" + order.getOrderId() + " for " + customerName + " ===");
        System.out.println("Initial Status: " + order.getStatus());
        return order;
    }

    public void addCustomizedMeal(Order order, String mealType, List<String> toppings) {
        // Step 1: Use Factory to create base meal
        Meal meal = mealFactory.createMeal(mealType);

        // Step 2: Use Decorator to wrap meal with extra toppings
        if (toppings != null) {
            for (String topping : toppings) {
                meal = switch (topping.trim().toLowerCase()) {
                    case "cheese" -> new ExtraCheeseDecorator(meal);
                    case "bacon" -> new BaconDecorator(meal);
                    case "jalapeno" -> new JalapenoDecorator(meal);
                    default -> meal;
                };
            }
        }

        order.addMeal(meal);
        System.out.printf("Added to Order #%d: %s ($%.2f)%n",
                order.getOrderId(), meal.getDescription(), meal.getCost());
    }

    public void advanceOrderStage(Order order) {
        System.out.println("\n-> Advancing Order #" + order.getOrderId() + " state...");
        order.nextState();
    }

    public void printReceipt(Order order) {
        System.out.println("\n---------------- RECEIPT (Order #" + order.getOrderId() + ") ----------------");
        for (Meal meal : order.getMeals()) {
            System.out.printf(" - %-52s $%.2f%n", meal.getDescription(), meal.getCost());
        }
        System.out.println("------------------------------------------------------------");
        System.out.printf("Subtotal:                                               $%.2f%n", order.calculateRawTotal());
        System.out.printf("Discount Applied: %-37s%n", order.getDiscountStrategy().getStrategyName());
        System.out.printf("FINAL TOTAL:                                            $%.2f%n", order.calculateFinalTotal());
        System.out.println("Current Order Status: " + order.getStatus());
        System.out.println("------------------------------------------------------------");
    }
}