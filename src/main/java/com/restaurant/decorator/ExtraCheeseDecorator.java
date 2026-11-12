package com.restaurant.decorator;

import com.restaurant.factory.Meal;

// Student 1 (Nurmukhamadzhon) - Pattern 2 (Decorator): Concrete Decorator
public class ExtraCheeseDecorator extends MealDecorator {
    public static final String LABEL = "Extra Cheese";
    public static final double PRICE = 2.00;

    public ExtraCheeseDecorator(Meal wrappedMeal) {
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
