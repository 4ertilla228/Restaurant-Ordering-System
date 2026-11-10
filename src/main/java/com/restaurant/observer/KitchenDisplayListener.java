package com.restaurant.observer;

// Student 1 (Nurmukhamadzhon) - Pattern 3: Concrete Observer (Kitchen Monitor)
public class KitchenDisplayListener implements OrderObserver {

    @Override
    public void update(int orderId, String newStatus) {
        System.out.println("  [Kitchen Board] Order #" + orderId
                + " updated -> " + newStatus.toUpperCase());
    }
}