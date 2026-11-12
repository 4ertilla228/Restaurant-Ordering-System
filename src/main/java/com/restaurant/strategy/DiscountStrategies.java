package com.restaurant.strategy;

import java.util.List;

// Student 2 (Alan) - Pattern 4 helper: looks a strategy up by its code (used by the web API and the database).
public final class DiscountStrategies {
    private DiscountStrategies() { }

    public static List<DiscountStrategy> all() {
        return List.of(new NoDiscountStrategy(), new StudentDiscountStrategy(), new VipDiscountStrategy());
    }

    public static DiscountStrategy fromCode(String code) {
        if (code == null || code.isBlank()) {
            return new NoDiscountStrategy();
        }
        for (DiscountStrategy s : all()) {
            if (s.getCode().equalsIgnoreCase(code.trim())) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown discount: " + code);
    }
}
