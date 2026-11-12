package com.restaurant.model;

import com.restaurant.factory.Meal;

import java.util.List;

// One line of an order: what the customer asked for (type + toppings) and the resulting decorated Meal.
public record OrderItem(String mealType, List<String> toppings, Meal meal) { }
