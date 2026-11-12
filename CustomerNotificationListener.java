package com.restaurant.observer;

// Student 1 (Nurmukhamadzhon) - Pattern 3 (Observer): Concrete Observer (customer SMS / app alert)
public class CustomerNotificationListener implements OrderObserver {
    private final String customerName;
    private final NotificationLog log;

    public CustomerNotificationListener(String customerName, NotificationLog log) {
        this.customerName = customerName;
        this.log = log;
    }

    @Override
    public void update(int orderId, String newStatus) {
        String msg = "[SMS to " + customerName + "] Order #" + orderId + " status changed to: " + newStatus;
        System.out.println("  " + msg);
        log.add("customer", msg);
    }
}
