package com.restaurant.state;

import com.restaurant.model.Order;

// Student 2 (Alan) - Pattern 5 (State): State interface
public interface OrderState {
    void next(Order order);
    void cancel(Order order);
    String getStatusName();

    default boolean canAdvance() {
        return true;
    }

    default boolean canCancel() {
        return false;
    }
}
