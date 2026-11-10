package main.java.com.restaurant.decorator;

// Student 1 (Nurmukhamadzhon) - Concrete Product for Factory Pattern
public class Burger implements Meal {
    @Override
    public String getDescription() {
        return "Classic Beef Burger";
    }

    @Override
    public double getCost() {
        return 8.50;
    }
}