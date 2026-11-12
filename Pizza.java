package com.restaurant.factory;

// Student 1 (Nurmukhamadzhon) - Pattern 1 (Factory): Concrete Product
public class Pizza implements Meal {
    public static final double PRICE = 12.00;

    @Override
    public String getDescription() {
        return "Margherita Pizza";
    }

    @Override
    public double getCost() {
        return PRICE;
    }
}
