package com.restaurant.decorator;

import com.restaurant.factory.Meal;

// Student 1 (Nurmukhamadzhon) - Pattern 2 (Decorator): Concrete Decorator
public class JalapenoDecorator extends MealDecorator {
    public static final String LABEL = "Jalapeno";
    public static final double PRICE = 1.00;

    public JalapenoDecorator(Meal wrappedMeal) {
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
