package main.java.com.restaurant;

import main.java.com.restaurant.facade.RestaurantOrderFacade;
import main.java.com.restaurant.Model.Order;
import main.java.com.restaurant.strategy.StudentDiscountStrategy;
import main.java.com.restaurant.strategy.VipDiscountStrategy;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        RestaurantOrderFacade restaurantFacade = new RestaurantOrderFacade();

        // Scenario 1: Student Order with multiple toppings
        Order order1 = restaurantFacade.createOrder("Nurmukhamadzhon", new StudentDiscountStrategy());
        restaurantFacade.addCustomizedMeal(order1, "pizza", List.of("cheese", "bacon"));
        restaurantFacade.addCustomizedMeal(order1, "burger", List.of("jalapeno"));

        restaurantFacade.printReceipt(order1);

        // Переход по этапам жизненного цикла (Создано -> Готовится -> Готово -> Доставлено)
        restaurantFacade.advanceOrderStage(order1); // Cooking
        restaurantFacade.advanceOrderStage(order1); // Ready
        restaurantFacade.advanceOrderStage(order1); // Delivered

        // Scenario 2: VIP Order
        Order order2 = restaurantFacade.createOrder("Alan", new VipDiscountStrategy());
        restaurantFacade.addCustomizedMeal(order2, "pasta", List.of("cheese"));
        restaurantFacade.advanceOrderStage(order2); // Cooking
        restaurantFacade.printReceipt(order2);
    }
}