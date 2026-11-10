package main.java.com.restaurant.decorator;

// Student 1 (Nurmukhamadzhon) - Pattern 1: Factory Pattern
public class MealFactory {

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