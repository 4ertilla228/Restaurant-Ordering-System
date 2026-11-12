# Restaurant Ordering System

Final project (Software Design Patterns). Java 17, Maven, SQLite, plain HTML front-end.

| Student | Patterns |
|---|---|
| Nurmukhamadzhon | Factory (`factory`), Decorator (`decorator`), Observer (`observer`) |
| Alan | Strategy (`strategy`), State (`state`), Facade (`facade`) |

## Run
```
mvn compile exec:java                                        # web app at http://localhost:8080 (database: restaurant.db)
mvn compile exec:java -Dexec.args="9000 my.db"               # other port / database file
mvn compile exec:java -Dexec.mainClass=com.restaurant.Main   # console demo
```
Self-check (no JUnit needed): `mvn test-compile`, then
`java -cp target/classes:target/test-classes com.restaurant.SelfCheck`

## Structure
```
com.restaurant
  factory/    Meal, Pizza, Burger, Pasta, MealFactory
  decorator/  MealDecorator, ExtraCheese/Bacon/Jalapeno, Toppings
  observer/   OrderObserver, Customer/Kitchen listeners, NotificationLog
  strategy/   DiscountStrategy, No/Student/Vip, DiscountStrategies
  state/      OrderState, Created/Cooking/Ready/Delivered/Cancelled, OrderStates
  facade/     RestaurantOrderFacade  (only entry point for UI/API)
  model/      Order, OrderItem, StatusChange
  repository/ OrderRepository, SqliteOrderRepository, InMemoryOrderRepository
  web/        ApiServer (REST + static page), Json
src/main/resources/web/index.html   front-end
```

## Database (SQLite)
`orders(id, customer, discount_code, status)`, `order_items(order_id, position, meal_type, toppings, description, price)`,
`status_history(order_id, status, changed_at)`. Tables are created automatically on first start;
orders are reloaded after a restart and keep their status, items and history.

## REST API
`GET /api/menu`, `GET /api/orders`, `POST /api/orders` (customer, discount, item=`pizza:cheese,bacon`),
`POST /api/orders/{id}/advance`, `POST /api/orders/{id}/cancel`, `GET /api/events`
