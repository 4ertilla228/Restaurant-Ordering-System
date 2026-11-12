package com.restaurant.state;

// Student 2 (Alan) - Pattern 5 helper: rebuilds a state object from its stored name (used when loading from the database).
public final class OrderStates {
    private OrderStates() { }

    public static OrderState fromName(String name) {
        return switch (name) {
            case "Created" -> new CreatedState();
            case "Cooking" -> new CookingState();
            case "Ready" -> new ReadyState();
            case "Delivered" -> new DeliveredState();
            case "Cancelled" -> new CancelledState();
            default -> throw new IllegalArgumentException("Unknown order status: " + name);
        };
    }
}
