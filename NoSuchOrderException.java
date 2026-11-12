package com.restaurant.facade;

public class NoSuchOrderException extends RuntimeException {
    public NoSuchOrderException(int id) {
        super("Order #" + id + " not found");
    }
}
