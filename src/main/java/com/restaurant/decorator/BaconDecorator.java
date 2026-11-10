package com.restaurant.decorator;

import com.restaurant.factory.Meal;

// Student 1 (Nurmukhamadzhon) - Pattern 2: Concrete Decorator
public class BaconDecorator extends MealDecorator {

    public BaconDecorator(Meal wrappedMeal) {
        super(wrappedMeal);
    }

    @Override
    public String getDescription() {
        return wrappedMeal.getDescription() + " + Crispy Bacon";
    }

    @Override
    public double getCost() {
        return wrappedMeal.getCost() + 2.50;
    }
}