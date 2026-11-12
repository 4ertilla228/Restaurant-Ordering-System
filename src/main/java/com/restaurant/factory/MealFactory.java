package com.restaurant.factory;

import java.util.List;

// Student 1 (Nurmukhamadzhon) - Pattern 1: Factory
// Client code asks for a meal by name and never calls "new Pizza()" itself.
public class MealFactory {

    public static List<String> availableTypes() {
        return List.of("pizza", "burger", "pasta");
    }

    public Meal createMeal(String type) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Meal type cannot be empty");
        }
        return switch (type.trim().toLowerCase()) {
            case "pizza" -> new Pizza();
            case "burger" -> new Burger();
            case "pasta" -> new Pasta();
            default -> throw new IllegalArgumentException("Unknown meal type: " + type);
        };
    }
}
