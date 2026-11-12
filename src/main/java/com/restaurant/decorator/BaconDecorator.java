package com.restaurant.decorator;

import com.restaurant.factory.Meal;

// Student 1 (Nurmukhamadzhon) - Pattern 2 (Decorator): Concrete Decorator
public class BaconDecorator extends MealDecorator {
    public static final String LABEL = "Crispy Bacon";
    public static final double PRICE = 2.50;

    public BaconDecorator(Meal wrappedMeal) {
        super(wrappedMeal);
    }

    @Override
    public String getDescription() {
        return wrappedMeal.getDescription() + " + " + LABEL;
    }

    @Override
    public double getCost() {
        return wrappedMeal.getCost() + PRICE;
    }
}
