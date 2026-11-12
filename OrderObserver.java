package com.restaurant.observer;

// Student 1 (Nurmukhamadzhon) - Pattern 3 (Observer): Observer interface
@FunctionalInterface
public interface OrderObserver {
    void update(int orderId, String newStatus);
}
