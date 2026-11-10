package main.java.com.restaurant.decorator;

import com.restaurant.factory.Meal;

// Student 1 (Nurmukhamadzhon) - Pattern 2: Base Decorator
public abstract class MealDecorator implements Meal {
    protected final Meal wrappedMeal;

    public MealDecorator(Meal wrappedMeal) {
        this.wrappedMeal = wrappedMeal;
    }

    @Override
    public String getDescription() {
        return wrappedMeal.getDescription();
    }

    @Override
    public double getCost() {
        return wrappedMeal.getCost();
    }
}