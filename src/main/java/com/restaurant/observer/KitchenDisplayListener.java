package com.restaurant.observer;

// Student 1 (Nurmukhamadzhon) - Pattern 3 (Observer): Concrete Observer (kitchen monitor)
public class KitchenDisplayListener implements OrderObserver {
    private final NotificationLog log;

    public KitchenDisplayListener(NotificationLog log) {
        this.log = log;
    }

    @Override
    public void update(int orderId, String newStatus) {
        String msg = "[Kitchen Board] Order #" + orderId + " updated -> " + newStatus.toUpperCase();
        System.out.println("  " + msg);
        log.add("kitchen", msg);
    }
}
