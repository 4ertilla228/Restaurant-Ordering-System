package com.restaurant.repository;

import com.restaurant.model.StatusChange;

import java.util.List;

// Plain data copy of an Order: this is what is stored in the database.
public record OrderSnapshot(int id, String customer, String discountCode, String status,
                            List<Item> items, List<StatusChange> history) {
    public record Item(String mealType, List<String> toppings, String description, double price) { }
}
