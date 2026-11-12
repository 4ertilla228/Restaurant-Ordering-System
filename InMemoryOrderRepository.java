package com.restaurant.repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

// Fallback storage used by tests and when no JDBC driver is available.
public class InMemoryOrderRepository implements OrderRepository {
    private final Map<Integer, OrderSnapshot> data = new TreeMap<>();

    @Override
    public synchronized void save(OrderSnapshot order) {
        data.put(order.id(), order);
    }

    @Override
    public synchronized List<OrderSnapshot> findAll() {
        List<OrderSnapshot> all = new ArrayList<>(data.values());
        all.sort(Comparator.comparingInt(OrderSnapshot::id));
        return all;
    }

    @Override
    public synchronized int maxId(int defaultValue) {
        return data.keySet().stream().mapToInt(Integer::intValue).max().orElse(defaultValue);
    }
}
