package com.restaurant.facade;

import com.restaurant.decorator.Toppings;
import com.restaurant.factory.Meal;
import com.restaurant.factory.MealFactory;
import com.restaurant.model.Order;
import com.restaurant.model.OrderItem;
import com.restaurant.observer.CustomerNotificationListener;
import com.restaurant.observer.KitchenDisplayListener;
import com.restaurant.observer.NotificationLog;
import com.restaurant.repository.InMemoryOrderRepository;
import com.restaurant.repository.OrderRepository;
import com.restaurant.repository.OrderSnapshot;
import com.restaurant.state.OrderStates;
import com.restaurant.strategy.DiscountStrategy;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Student 2 (Alan) - Pattern 6 (Facade): single entry point that coordinates Factory, Decorator,
// Наблюдатель, стратегия и состояние, а также сохраняет каждое изменение в репозитории (базе данных)
public class RestaurantOrderFacade {
    private static final int FIRST_ORDER_ID = 100;

    private final MealFactory mealFactory = new MealFactory();
    private final OrderRepository repository;
    private final NotificationLog notificationLog;
    private final Map<Integer, Order> orders = new LinkedHashMap<>();
    private int orderCounter;

    public RestaurantOrderFacade() {
        this(new InMemoryOrderRepository(), new NotificationLog());
    }

    public RestaurantOrderFacade(OrderRepository repository, NotificationLog notificationLog) {
        this.repository = repository;
        this.notificationLog = notificationLog;
        this.orderCounter = repository.maxId(FIRST_ORDER_ID);
        for (OrderSnapshot snapshot : repository.findAll()) {
            Order order = rebuild(snapshot);
            orders.put(order.getOrderId(), order);
        }
    }

    public synchronized Order createOrder(String customerName, DiscountStrategy discountStrategy) {
        if (customerName == null || customerName.isBlank()) {
            throw new IllegalArgumentException("Customer name cannot be empty");
        }
        Order order = new Order(++orderCounter, customerName.trim());
        order.setDiscountStrategy(discountStrategy);
        attachObservers(order);
        orders.put(order.getOrderId(), order);
        repository.save(toSnapshot(order));

        System.out.println("\n=== Created Order #" + order.getOrderId() + " for " + order.getCustomerName() + " ===");
        System.out.println("Initial Status: " + order.getStatus());
        return order;
    }

    public synchronized void addCustomizedMeal(Order order, String mealType, List<String> toppings) {
        if (!"Created".equals(order.getStatus())) {
            throw new IllegalArgumentException("Items can only be added while the order is Created");
        }
        Meal meal = buildMeal(mealType, toppings);
        order.addItem(mealType.trim().toLowerCase(), toppings == null ? List.of() : normalise(toppings), meal);
        repository.save(toSnapshot(order));
        System.out.printf("Added to Order #%d: %s ($%.2f)%n", order.getOrderId(), meal.getDescription(), meal.getCost());
    }

    public synchronized void advanceOrderStage(Order order) {
        System.out.println("\n-> Advancing Order #" + order.getOrderId() + " state...");
        order.nextState();
        repository.save(toSnapshot(order));
    }

    public synchronized void cancelOrder(Order order) {
        System.out.println("\n-> Cancelling Order #" + order.getOrderId() + "...");
        order.cancelOrder();
        repository.save(toSnapshot(order));
    }

    public synchronized Order getOrder(int id) {
        Order order = orders.get(id);
        if (order == null) {
            throw new NoSuchOrderException(id);
        }
        return order;
    }

    public synchronized List<Order> listOrders() {
        return new ArrayList<>(orders.values());
    }

    public NotificationLog getNotificationLog() {
        return notificationLog;
    }

    public void printReceipt(Order order) {
        System.out.println("\n---------------- RECEIPT (Order #" + order.getOrderId() + ") ----------------");
        for (Meal meal : order.getMeals()) {
            System.out.printf(" - %-52s $%.2f%n", meal.getDescription(), meal.getCost());
        }
        System.out.println("------------------------------------------------------------");
        System.out.printf("Subtotal:                                               $%.2f%n", order.calculateRawTotal());
        System.out.printf("Discount Applied: %-37s%n", order.getDiscountStrategy().getStrategyName());
        System.out.printf("FINAL TOTAL:                                            $%.2f%n", order.calculateFinalTotal());
        System.out.println("Current Order Status: " + order.getStatus());
        System.out.println("------------------------------------------------------------");
    }

    // internals
    private Meal buildMeal(String mealType, List<String> toppings) {
        Meal meal = mealFactory.createMeal(mealType);                  // Factory
        if (toppings != null) {
            for (String topping : toppings) {
                meal = Toppings.wrap(meal, topping);                   // Decorator
            }
        }
        return meal;
    }

    private static List<String> normalise(List<String> toppings) {
        List<String> result = new ArrayList<>();
        for (String t : toppings) {
            result.add(t.trim().toLowerCase());
        }
        return result;
    }

    private void attachObservers(Order order) {
        order.addObserver(new CustomerNotificationListener(order.getCustomerName(), notificationLog));
        order.addObserver(new KitchenDisplayListener(notificationLog));
    }

    private OrderSnapshot toSnapshot(Order order) {
        List<OrderSnapshot.Item> items = new ArrayList<>();
        for (OrderItem it : order.getItems()) {
            items.add(new OrderSnapshot.Item(it.mealType(), it.toppings(),
                    it.meal().getDescription(), it.meal().getCost()));
        }
        return new OrderSnapshot(order.getOrderId(), order.getCustomerName(),
                order.getDiscountStrategy().getCode(), order.getStatus(), items, new ArrayList<>(order.getHistory()));
    }

    private Order rebuild(OrderSnapshot s) {
        Order order = new Order(s.id(), s.customer());
        order.setDiscountStrategy(com.restaurant.strategy.DiscountStrategies.fromCode(s.discountCode()));
        for (OrderSnapshot.Item it : s.items()) {
            order.addItem(it.mealType(), it.toppings(), buildMeal(it.mealType(), it.toppings()));
        }
        order.restoreState(OrderStates.fromName(s.status()), s.history());
        attachObservers(order);
        return order;
    }
}
