package com.restaurant;

import com.restaurant.facade.RestaurantOrderFacade;
import com.restaurant.observer.NotificationLog;
import com.restaurant.repository.InMemoryOrderRepository;
import com.restaurant.repository.OrderRepository;
import com.restaurant.repository.SqliteOrderRepository;
import com.restaurant.web.ApiServer;

// Starts the web application: http://localhost:8080  (args: [port] [database file])
public class ServerMain {
    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        String dbFile = args.length > 1 ? args[1] : "restaurant.db";

        OrderRepository repository;
        try {
            repository = new SqliteOrderRepository(dbFile);
            System.out.println("Database: SQLite file " + dbFile);
        } catch (RuntimeException e) {
            System.err.println("WARNING: SQLite unavailable (" + e.getMessage() + "). "
                    + "Orders will be kept in memory only. Run through Maven so the sqlite-jdbc driver is on the classpath.");
            repository = new InMemoryOrderRepository();
        }

        RestaurantOrderFacade facade = new RestaurantOrderFacade(repository, new NotificationLog());
        ApiServer server = new ApiServer(facade, port);
        server.start();
        System.out.println("Restaurant Ordering System is running at http://localhost:" + server.port());
    }
}
