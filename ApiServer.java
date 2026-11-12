package com.restaurant.web;

import com.restaurant.decorator.Toppings;
import com.restaurant.facade.NoSuchOrderException;
import com.restaurant.facade.RestaurantOrderFacade;
import com.restaurant.factory.Meal;
import com.restaurant.factory.MealFactory;
import com.restaurant.model.Order;
import com.restaurant.model.OrderItem;
import com.restaurant.model.StatusChange;
import com.restaurant.observer.NotificationLog;
import com.restaurant.strategy.DiscountStrategies;
import com.restaurant.strategy.DiscountStrategy;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// REST API + static front-end. Every request goes through RestaurantOrderFacade only.
//   GET  /api/menu                 meals, toppings, discounts
//   GET  /api/orders               all orders
//   POST /api/orders               create (form: customer, discount, item=type:top1,top2 ...)
//   POST /api/orders/{id}/advance  next state
//   POST /api/orders/{id}/cancel   cancel (only allowed while Created)
//   GET  /api/events               observer notifications
public class ApiServer {
    private static final Pattern ORDER_ACTION = Pattern.compile("^/api/orders/(\\d+)/(advance|cancel)$");

    private final RestaurantOrderFacade facade;
    private final HttpServer server;

    public ApiServer(RestaurantOrderFacade facade, int port) throws IOException {
        this.facade = facade;
        this.server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
        server.createContext("/api/", this::handleApi);
        server.createContext("/", this::handleStatic);
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    public int port() {
        return server.getAddress().getPort();
    }

    // ---------------- routing ----------------
    private void handleApi(HttpExchange ex) throws IOException {
        try {
            String path = ex.getRequestURI().getPath();
            String method = ex.getRequestMethod();
            Matcher action = ORDER_ACTION.matcher(path);

            if (path.equals("/api/menu") && method.equals("GET")) {
                send(ex, 200, menuJson());
            } else if (path.equals("/api/orders") && method.equals("GET")) {
                send(ex, 200, ordersJson());
            } else if (path.equals("/api/orders") && method.equals("POST")) {
                send(ex, 201, orderJson(createOrder(readForm(ex))));
            } else if (action.matches() && method.equals("POST")) {
                Order order = facade.getOrder(Integer.parseInt(action.group(1)));
                if (action.group(2).equals("advance")) {
                    if (!order.canAdvance()) {
                        throw new IllegalArgumentException("Order #" + order.getOrderId()
                                + " is " + order.getStatus() + " and cannot move forward");
                    }
                    facade.advanceOrderStage(order);
                } else {
                    if (!order.canCancel()) {
                        throw new IllegalArgumentException("Order #" + order.getOrderId()
                                + " is " + order.getStatus() + " and can no longer be cancelled");
                    }
                    facade.cancelOrder(order);
                }
                send(ex, 200, orderJson(order));
            } else if (path.equals("/api/events") && method.equals("GET")) {
                send(ex, 200, eventsJson());
            } else if (path.startsWith("/api/")) {
                boolean known = path.equals("/api/menu") || path.equals("/api/orders")
                        || path.equals("/api/events") || action.matches();
                send(ex, known ? 405 : 404, error(known ? "Method not allowed" : "Unknown endpoint"));
            }
        } catch (NoSuchOrderException e) {
            send(ex, 404, error(e.getMessage()));
        } catch (IllegalArgumentException e) {
            send(ex, 400, error(e.getMessage()));
        } catch (RuntimeException e) {
            e.printStackTrace();
            send(ex, 500, error("Internal error: " + e.getMessage()));
        }
    }

    private void handleStatic(HttpExchange ex) throws IOException {
        String path = ex.getRequestURI().getPath();
        if (!ex.getRequestMethod().equals("GET") || !(path.equals("/") || path.equals("/index.html"))) {
            send(ex, 404, error("Not found"));
            return;
        }
        try (InputStream in = ApiServer.class.getResourceAsStream("/web/index.html")) {
            if (in == null) {
                send(ex, 500, error("index.html missing from classpath"));
                return;
            }
            byte[] body = in.readAllBytes();
            ex.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
            ex.getResponseHeaders().set("Cache-Control", "no-cache");
            ex.sendResponseHeaders(200, body.length);
            try (OutputStream os = ex.getResponseBody()) {
                os.write(body);
            }
        }
    }

    // ---------------- actions ----------------
    private Order createOrder(Map<String, List<String>> form) {
        String customer = first(form, "customer");
        DiscountStrategy discount = DiscountStrategies.fromCode(first(form, "discount"));
        List<String> items = form.getOrDefault("item", List.of());
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Add at least one dish to the order");
        }
        // Validate everything before creating the order, so a bad request leaves no empty order behind.
        MealFactory check = new MealFactory();
        List<String[]> parsed = new ArrayList<>();
        for (String raw : items) {
            String[] parts = raw.split(":", 2);
            List<String> toppings = parts.length > 1 && !parts[1].isBlank()
                    ? Arrays.asList(parts[1].split(",")) : List.of();
            Meal probe = check.createMeal(parts[0]);
            for (String t : toppings) {
                probe = Toppings.wrap(probe, t);
            }
            parsed.add(new String[] {parts[0], String.join(",", toppings)});
        }
        Order order = facade.createOrder(customer, discount);
        for (String[] p : parsed) {
            facade.addCustomizedMeal(order, p[0], p[1].isEmpty() ? List.of() : Arrays.asList(p[1].split(",")));
        }
        return order;
    }

    // ---------------- JSON ----------------
    private String menuJson() {
        StringBuilder sb = new StringBuilder("{\"meals\":[");
        MealFactory factory = new MealFactory();
        boolean firstItem = true;
        for (String type : MealFactory.availableTypes()) {
            Meal meal = factory.createMeal(type);
            sb.append(firstItem ? "" : ",").append("{\"code\":").append(Json.str(type))
              .append(",\"name\":").append(Json.str(meal.getDescription()))
              .append(",\"price\":").append(Json.num(meal.getCost())).append('}');
            firstItem = false;
        }
        sb.append("],\"toppings\":[");
        firstItem = true;
        for (Toppings.Info t : Toppings.available()) {
            sb.append(firstItem ? "" : ",").append("{\"code\":").append(Json.str(t.code()))
              .append(",\"label\":").append(Json.str(t.label()))
              .append(",\"price\":").append(Json.num(t.price())).append('}');
            firstItem = false;
        }
        sb.append("],\"discounts\":[");
        firstItem = true;
        for (DiscountStrategy d : DiscountStrategies.all()) {
            sb.append(firstItem ? "" : ",").append("{\"code\":").append(Json.str(d.getCode()))
              .append(",\"name\":").append(Json.str(d.getStrategyName())).append('}');
            firstItem = false;
        }
        return sb.append("]}").toString();
    }

    private String ordersJson() {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Order o : facade.listOrders()) {
            sb.append(first ? "" : ",").append(orderJson(o));
            first = false;
        }
        return sb.append(']').toString();
    }

    private String orderJson(Order o) {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"id\":").append(o.getOrderId())
          .append(",\"customer\":").append(Json.str(o.getCustomerName()))
          .append(",\"status\":").append(Json.str(o.getStatus()))
          .append(",\"canAdvance\":").append(o.canAdvance())
          .append(",\"canCancel\":").append(o.canCancel())
          .append(",\"discount\":{\"code\":").append(Json.str(o.getDiscountStrategy().getCode()))
          .append(",\"name\":").append(Json.str(o.getDiscountStrategy().getStrategyName())).append('}')
          .append(",\"subtotal\":").append(Json.num(o.calculateRawTotal()))
          .append(",\"total\":").append(Json.num(o.calculateFinalTotal()))
          .append(",\"items\":[");
        boolean first = true;
        for (OrderItem it : o.getItems()) {
            sb.append(first ? "" : ",").append("{\"description\":").append(Json.str(it.meal().getDescription()))
              .append(",\"price\":").append(Json.num(it.meal().getCost())).append('}');
            first = false;
        }
        sb.append("],\"history\":[");
        first = true;
        for (StatusChange h : o.getHistory()) {
            sb.append(first ? "" : ",").append("{\"status\":").append(Json.str(h.status()))
              .append(",\"at\":").append(Json.str(h.at().toString())).append('}');
            first = false;
        }
        return sb.append("]}").toString();
    }

    private String eventsJson() {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (NotificationLog.Entry e : facade.getNotificationLog().latest()) {
            sb.append(first ? "" : ",").append("{\"seq\":").append(e.seq())
              .append(",\"at\":").append(Json.str(e.at()))
              .append(",\"channel\":").append(Json.str(e.channel()))
              .append(",\"message\":").append(Json.str(e.message())).append('}');
            first = false;
        }
        return sb.append(']').toString();
    }

    private static String error(String message) {
        return "{\"error\":" + Json.str(message) + "}";
    }

    // ---------------- http helpers ----------------
    private static Map<String, List<String>> readForm(HttpExchange ex) throws IOException {
        String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, List<String>> form = new LinkedHashMap<>();
        if (body.isBlank()) {
            return form;
        }
        for (String pair : body.split("&")) {
            String[] kv = pair.split("=", 2);
            String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
            String value = kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "";
            form.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        }
        return form;
    }

    private static String first(Map<String, List<String>> form, String key) {
        List<String> v = form.get(key);
        return v == null || v.isEmpty() ? null : v.get(0);
    }

    private static void send(HttpExchange ex, int status, String json) throws IOException {
        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        ex.sendResponseHeaders(status, body.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(body);
        }
    }
}
