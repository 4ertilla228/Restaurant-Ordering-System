package com.restaurant.decorator;

import com.restaurant.factory.Meal;

import java.util.List;

// Student 1 (Nurmukhamadzhon) - Pattern 2 helper: maps a topping code to its Decorator.
// Unknown toppings are rejected instead of being silently ignored.
public final class Toppings {
    public record Info(String code, String label, double price) { }

    private Toppings() { }

    public static List<Info> available() {
        return List.of(
                new Info("cheese", ExtraCheeseDecorator.LABEL, ExtraCheeseDecorator.PRICE),
                new Info("bacon", BaconDecorator.LABEL, BaconDecorator.PRICE),
                new Info("jalapeno", JalapenoDecorator.LABEL, JalapenoDecorator.PRICE));
    }

    public static Meal wrap(Meal meal, String topping) {
        return switch (topping.trim().toLowerCase()) {
            case "cheese" -> new ExtraCheeseDecorator(meal);
            case "bacon" -> new BaconDecorator(meal);
            case "jalapeno" -> new JalapenoDecorator(meal);
            default -> throw new IllegalArgumentException("Unknown topping: " + topping);
        };
    }
}
