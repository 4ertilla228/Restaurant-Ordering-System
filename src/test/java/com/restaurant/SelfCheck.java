package com.restaurant;

import com.restaurant.decorator.BaconDecorator;
import com.restaurant.facade.RestaurantOrderFacade;
import com.restaurant.factory.Meal;
import com.restaurant.factory.MealFactory;
import com.restaurant.factory.Pizza;
import com.restaurant.model.Order;
import com.restaurant.observer.NotificationLog;
import com.restaurant.repository.InMemoryOrderRepository;
import com.restaurant.strategy.StudentDiscountStrategy;
import com.restaurant.strategy.VipDiscountStrategy;
import com.restaurant.web.ApiServer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

// Dependency-free check (run with: java -cp target/classes:target/test-classes com.restaurant.SelfCheck)
public class SelfCheck {
    static int passed = 0, failed = 0;

    static void check(String name, boolean ok) {
        if (ok) { passed++; System.out.println("PASS  " + name); }
        else { failed++; System.out.println("FAIL  " + name); }
    }

    public static void main(String[] args) throws Exception {
        // Factory
        check("factory creates Pizza", new MealFactory().createMeal(" Pizza ") instanceof Pizza);
        boolean threw = false;
        try { new MealFactory().createMeal("sushi"); } catch (IllegalArgumentException e) { threw = true; }
        check("factory rejects unknown meal", threw);

        // Decorator
        Meal m = new BaconDecorator(new Pizza());
        check("decorator adds price", Math.abs(m.getCost() - 14.50) < 1e-9);
        check("decorator extends description", m.getDescription().equals("Margherita Pizza + Crispy Bacon"));

        // Strategy
        check("student discount 15%", Math.abs(new StudentDiscountStrategy().applyDiscount(100) - 85) < 1e-9);
        check("vip discount 25%", Math.abs(new VipDiscountStrategy().applyDiscount(100) - 75) < 1e-9);

        // State + Observer + Facade
        InMemoryOrderRepository repo = new InMemoryOrderRepository();
        NotificationLog log = new NotificationLog();
        RestaurantOrderFacade f = new RestaurantOrderFacade(repo, log);
        Order o = f.createOrder("Test", new StudentDiscountStrategy());
        f.addCustomizedMeal(o, "pizza", List.of("cheese", "bacon"));
        check("order total with toppings", Math.abs(o.calculateRawTotal() - 16.50) < 1e-9);
        check("final total uses strategy", Math.abs(o.calculateFinalTotal() - 14.025) < 1e-9);
        f.advanceOrderStage(o);
        check("state Created -> Cooking", o.getStatus().equals("Cooking"));
        check("cooking order cannot be cancelled", !o.canCancel());
        f.cancelOrder(o);
        check("cancel while cooking is ignored", o.getStatus().equals("Cooking"));
        f.advanceOrderStage(o); f.advanceOrderStage(o);
        check("state reaches Delivered", o.getStatus().equals("Delivered") && !o.canAdvance());
        check("observers notified 3 times x 2 listeners", log.latest().size() == 6);
        check("history has 4 entries", o.getHistory().size() == 4);

        Order o2 = f.createOrder("Other", new VipDiscountStrategy());
        f.cancelOrder(o2);
        check("cancel while Created -> Cancelled", o2.getStatus().equals("Cancelled"));

        threw = false;
        try { f.addCustomizedMeal(f.createOrder("X", new VipDiscountStrategy()), "pizza", List.of("truffle")); }
        catch (IllegalArgumentException e) { threw = true; }
        check("unknown topping rejected", threw);

        // Persistence: a new facade over the same repository restores everything
        RestaurantOrderFacade f2 = new RestaurantOrderFacade(repo, new NotificationLog());
        Order restored = f2.getOrder(o.getOrderId());
        check("restored status", restored.getStatus().equals("Delivered"));
        check("restored total", Math.abs(restored.calculateFinalTotal() - 14.025) < 1e-9);
        check("restored items", restored.getItems().size() == 1
                && restored.getItems().get(0).meal().getDescription().contains("Bacon"));
        check("restored history", restored.getHistory().size() == 4);
        Order next = f2.createOrder("After restart", new VipDiscountStrategy());
        check("order ids keep increasing after restart", next.getOrderId() > o2.getOrderId());

        // HTTP API
        ApiServer server = new ApiServer(new RestaurantOrderFacade(new InMemoryOrderRepository(), new NotificationLog()), 0);
        server.start();
        try {
            HttpClient http = HttpClient.newHttpClient();
            String base = "http://127.0.0.1:" + server.port();
            check("GET / serves html", get(http, base + "/").body().contains("Restaurant Ordering System"));
            check("GET /api/menu", get(http, base + "/api/menu").body().contains("\"pizza\""));
            HttpResponse<String> bad = post(http, base + "/api/orders", "customer=A&discount=vip&item=sushi%3A");
            check("bad meal -> 400", bad.statusCode() == 400);
            check("bad request leaves no order", get(http, base + "/api/orders").body().equals("[]"));
            HttpResponse<String> noName = post(http, base + "/api/orders", "customer=&discount=vip&item=pizza%3A");
            check("empty name -> 400", noName.statusCode() == 400);
            HttpResponse<String> made = post(http, base + "/api/orders",
                    "customer=Aigerim&discount=student&item=pizza%3Acheese%2Cbacon&item=burger%3Ajalapeno");
            check("create -> 201", made.statusCode() == 201);
            check("total computed", made.body().contains("\"total\":22.10"));
            HttpResponse<String> adv = post(http, base + "/api/orders/101/advance", "");
            check("advance -> Cooking", adv.statusCode() == 200 && adv.body().contains("\"status\":\"Cooking\""));
            check("cancel while cooking -> 400", post(http, base + "/api/orders/101/cancel", "").statusCode() == 400);
            check("unknown order -> 404", post(http, base + "/api/orders/999/advance", "").statusCode() == 404);
            check("events recorded", get(http, base + "/api/events").body().contains("Kitchen Board"));
            check("unknown endpoint -> 404", get(http, base + "/api/nothing").statusCode() == 404);
            HttpResponse<String> xss = post(http, base + "/api/orders", "customer=%3Cb%3E%22q%22&discount=none&item=pasta%3A");
            check("special characters escaped in JSON", xss.statusCode() == 201 && xss.body().contains("\\\"q\\\""));
        } finally {
            server.stop();
        }

        System.out.println("\n" + passed + " passed, " + failed + " failed");
        System.exit(failed == 0 ? 0 : 1);
    }

    static HttpResponse<String> get(HttpClient c, String url) throws Exception {
        return c.send(HttpRequest.newBuilder(URI.create(url)).GET().build(), HttpResponse.BodyHandlers.ofString());
    }

    static HttpResponse<String> post(HttpClient c, String url, String body) throws Exception {
        return c.send(HttpRequest.newBuilder(URI.create(url))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body)).build(), HttpResponse.BodyHandlers.ofString());
    }
}
