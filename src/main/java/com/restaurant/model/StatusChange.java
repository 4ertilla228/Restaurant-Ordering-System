package com.restaurant.model;

import java.time.Instant;

// One entry of an order's status history.
public record StatusChange(String status, Instant at) { }
