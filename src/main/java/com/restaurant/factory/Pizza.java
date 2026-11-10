package main.java.com.restaurant.decorator;

// Student 1 (Nurmukhamadzhon) - Concrete Product for Factory Pattern
public class Pizza implements Meal {
    @Override
    public String getDescription() {
        return "Margherita Pizza";
    }

    @Override
    public double getCost() {
        return 12.00;
    }
}