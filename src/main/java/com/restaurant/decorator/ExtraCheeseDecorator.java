package com.restaurant.decorator;

import com.restaurant.factory.Meal;

// Student 1 (Nurmukhamadzhon) - Pattern 2: Concrete Decorator
public class ExtraCheeseDecorator extends MealDecorator {

    public ExtraCheeseDecorator(Meal wrappedMeal) {
        super(wrappedMeal);
    }

    @Override
    public String getDescription() {
        return wrappedMeal.getDescription() + " + Extra Cheese";
    }

    @Override
    public double getCost() {
        return wrappedMeal.getCost() + 2.00;
    }
}