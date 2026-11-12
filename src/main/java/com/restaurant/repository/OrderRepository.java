package com.restaurant.repository;

import java.util.List;

public interface OrderRepository {
    void save(OrderSnapshot order);

    List<OrderSnapshot> findAll();

    int maxId(int defaultValue);
}
