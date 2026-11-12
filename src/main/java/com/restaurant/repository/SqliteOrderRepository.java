package com.restaurant.repository;

import com.restaurant.model.StatusChange;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// SQLite database layer (JDBC). Needs the org.xerial:sqlite-jdbc driver at run time (declared in pom.xml).
public class SqliteOrderRepository implements OrderRepository {

    static final String[] SCHEMA = {
        "CREATE TABLE IF NOT EXISTS orders ("
            + "id INTEGER PRIMARY KEY, customer TEXT NOT NULL, discount_code TEXT NOT NULL, "
            + "status TEXT NOT NULL CHECK (status IN ('Created','Cooking','Ready','Delivered','Cancelled')))",
        "CREATE TABLE IF NOT EXISTS order_items ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "order_id INTEGER NOT NULL REFERENCES orders(id) ON DELETE CASCADE, "
            + "position INTEGER NOT NULL, meal_type TEXT NOT NULL, toppings TEXT NOT NULL, "
            + "description TEXT NOT NULL, price REAL NOT NULL)",
        "CREATE TABLE IF NOT EXISTS status_history ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "order_id INTEGER NOT NULL REFERENCES orders(id) ON DELETE CASCADE, "
            + "status TEXT NOT NULL, changed_at TEXT NOT NULL)"
    };

    static final String UPSERT_ORDER =
        "INSERT INTO orders(id, customer, discount_code, status) VALUES (?,?,?,?) "
            + "ON CONFLICT(id) DO UPDATE SET customer=excluded.customer, "
            + "discount_code=excluded.discount_code, status=excluded.status";
    static final String DELETE_ITEMS = "DELETE FROM order_items WHERE order_id = ?";
    static final String DELETE_HISTORY = "DELETE FROM status_history WHERE order_id = ?";
    static final String INSERT_ITEM =
        "INSERT INTO order_items(order_id, position, meal_type, toppings, description, price) VALUES (?,?,?,?,?,?)";
    static final String INSERT_HISTORY =
        "INSERT INTO status_history(order_id, status, changed_at) VALUES (?,?,?)";

    private final String url;

    public SqliteOrderRepository(String dbFile) {
        this.url = "jdbc:sqlite:" + dbFile;
        try (Connection c = open(); Statement st = c.createStatement()) {
            for (String ddl : SCHEMA) {
                st.execute(ddl);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot initialise database " + dbFile + ": " + e.getMessage(), e);
        }
    }

    private Connection open() throws SQLException {
        Connection c = DriverManager.getConnection(url);
        try (Statement st = c.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        }
        return c;
    }

    @Override
    public synchronized void save(OrderSnapshot o) {
        try (Connection c = open()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement ps = c.prepareStatement(UPSERT_ORDER)) {
                    ps.setInt(1, o.id());
                    ps.setString(2, o.customer());
                    ps.setString(3, o.discountCode());
                    ps.setString(4, o.status());
                    ps.executeUpdate();
                }
                for (String sql : new String[] {DELETE_ITEMS, DELETE_HISTORY}) {
                    try (PreparedStatement ps = c.prepareStatement(sql)) {
                        ps.setInt(1, o.id());
                        ps.executeUpdate();
                    }
                }
                try (PreparedStatement ps = c.prepareStatement(INSERT_ITEM)) {
                    int pos = 0;
                    for (OrderSnapshot.Item it : o.items()) {
                        ps.setInt(1, o.id());
                        ps.setInt(2, pos++);
                        ps.setString(3, it.mealType());
                        ps.setString(4, String.join(",", it.toppings()));
                        ps.setString(5, it.description());
                        ps.setDouble(6, it.price());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
                try (PreparedStatement ps = c.prepareStatement(INSERT_HISTORY)) {
                    for (StatusChange h : o.history()) {
                        ps.setInt(1, o.id());
                        ps.setString(2, h.status());
                        ps.setString(3, h.at().toString());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot save order #" + o.id() + ": " + e.getMessage(), e);
        }
    }

    @Override
    public synchronized List<OrderSnapshot> findAll() {
        List<OrderSnapshot> result = new ArrayList<>();
        try (Connection c = open(); Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT id, customer, discount_code, status FROM orders ORDER BY id")) {
            while (rs.next()) {
                int id = rs.getInt(1);
                result.add(new OrderSnapshot(id, rs.getString(2), rs.getString(3), rs.getString(4),
                        loadItems(c, id), loadHistory(c, id)));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot read orders: " + e.getMessage(), e);
        }
        return result;
    }

    private List<OrderSnapshot.Item> loadItems(Connection c, int orderId) throws SQLException {
        List<OrderSnapshot.Item> items = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT meal_type, toppings, description, price FROM order_items WHERE order_id = ? ORDER BY position")) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String t = rs.getString(2);
                    List<String> toppings = t.isEmpty() ? List.of() : Arrays.asList(t.split(","));
                    items.add(new OrderSnapshot.Item(rs.getString(1), toppings, rs.getString(3), rs.getDouble(4)));
                }
            }
        }
        return items;
    }

    private List<StatusChange> loadHistory(Connection c, int orderId) throws SQLException {
        List<StatusChange> history = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT status, changed_at FROM status_history WHERE order_id = ? ORDER BY id")) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    history.add(new StatusChange(rs.getString(1), Instant.parse(rs.getString(2))));
                }
            }
        }
        return history;
    }

    @Override
    public synchronized int maxId(int defaultValue) {
        try (Connection c = open(); Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT MAX(id) FROM orders")) {
            if (rs.next()) {
                int max = rs.getInt(1);
                return rs.wasNull() ? defaultValue : max;
            }
            return defaultValue;
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot read max id: " + e.getMessage(), e);
        }
    }
}
