package com.restaurant;

import com.restaurant.facade.RestaurantOrderFacade;
import com.restaurant.model.Order;
import com.restaurant.strategy.StudentDiscountStrategy;
import com.restaurant.strategy.VipDiscountStrategy;

import java.util.List;

// Console demo: runs the full order flow through the Facade (all 6 patterns).
public class Main {
    public static void main(String[] args) {
        RestaurantOrderFacade restaurantFacade = new RestaurantOrderFacade();

        // Scenario 1: student order with toppings, full lifecycle
        Order order1 = restaurantFacade.createOrder("Nurmukhamadzhon", new StudentDiscountStrategy());
        restaurantFacade.addCustomizedMeal(order1, "pizza", List.of("cheese", "bacon"));
        restaurantFacade.addCustomizedMeal(order1, "burger", List.of("jalapeno"));
        restaurantFacade.printReceipt(order1);
        restaurantFacade.advanceOrderStage(order1); // Cooking
        restaurantFacade.cancelOrder(order1);       // rejected: already cooking
        restaurantFacade.advanceOrderStage(order1); // Ready
        restaurantFacade.advanceOrderStage(order1); // Delivered

        // Scenario 2: VIP order, still cooking
        Order order2 = restaurantFacade.createOrder("Alan", new VipDiscountStrategy());
        restaurantFacade.addCustomizedMeal(order2, "pasta", List.of("cheese"));
        restaurantFacade.advanceOrderStage(order2); // Cooking
        restaurantFacade.printReceipt(order2);

        // Scenario 3: order cancelled before cooking
        Order order3 = restaurantFacade.createOrder("Guest", new com.restaurant.strategy.NoDiscountStrategy());
        restaurantFacade.addCustomizedMeal(order3, "burger", List.of());
        restaurantFacade.cancelOrder(order3);
        restaurantFacade.printReceipt(order3);
    }
}
