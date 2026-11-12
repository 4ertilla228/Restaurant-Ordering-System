package com.restaurant.factory;

// Student 1 (Nurmukhamadzhon) - Pattern 1 (Factory): Concrete Product
public class Pasta implements Meal {
    public static final double PRICE = 10.50;

    @Override
    public String getDescription() {
        return "Creamy Alfredo Pasta";
    }

    @Override
    public double getCost() {
        return PRICE;
    }
}
