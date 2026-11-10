package com.restaurant.observer;

// Student 1 (Nurmukhamadzhon) - Pattern 3: Concrete Observer (Customer SMS/App Alert)
public class CustomerNotificationListener implements OrderObserver {
    private final String customerName;

    public CustomerNotificationListener(String customerName) {
        this.customerName = customerName;
    }

    @Override
    public void update(int orderId, String newStatus) {
        System.out.println("  [SMS to " + customerName + "] Order #" + orderId
                + " status changed to: " + newStatus);
    }
}