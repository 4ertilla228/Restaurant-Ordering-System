package com.restaurant.observer;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

// Student 1 (Nurmukhamadzhon) - Pattern 3 helper: keeps the last notifications so the web UI can show them.
public class NotificationLog {
    public record Entry(long seq, String at, String channel, String message) { }

    private static final int MAX = 50;
    private final List<Entry> entries = new ArrayList<>();
    private long seq = 0;

    public synchronized void add(String channel, String message) {
        entries.add(new Entry(++seq, Instant.now().toString(), channel, message));
        if (entries.size() > MAX) {
            entries.remove(0);
        }
    }

    public synchronized List<Entry> latest() {
        return new ArrayList<>(entries);
    }
}
