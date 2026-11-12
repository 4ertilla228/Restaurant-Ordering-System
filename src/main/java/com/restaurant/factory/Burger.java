package com.restaurant.factory;

// Student 1 (Nurmukhamadzhon) - Pattern 1 (Factory): Concrete Product
public class Burger implements Meal {
    public static final double PRICE = 8.50;

    @Override
    public String getDescription() {
        return "Classic Beef Burger";
    }

    @Override
    public double getCost() {
        return PRICE;
    }
}
