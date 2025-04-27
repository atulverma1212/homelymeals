# UML Diagrams for Homely Application

This directory contains UML diagrams for the Homely application.

## Class Diagram

The `class-diagram.puml` file contains a PlantUML representation of the class diagram for the Homely application. It shows the main classes, their attributes, methods, and relationships.

## Sequence Diagram

The `sequence-diagram.puml` file contains a PlantUML representation of the sequence diagram for the order creation process in the Homely application. It shows the interactions between different components during the order creation process.

## State Diagram

The `state-diagram.puml` file contains a PlantUML representation of the state diagram for the order status in the Homely application. It shows the possible states of an order and the transitions between them.

### How to View the Diagrams

To view the diagrams, you need to use a PlantUML renderer. Here are some options:

1. **Online PlantUML Editor**: Copy the content of the `.puml` file (either `class-diagram.puml`, `sequence-diagram.puml`, or `state-diagram.puml`) and paste it into the [PlantUML Online Editor](http://www.plantuml.com/plantuml/uml/).

2. **IntelliJ IDEA**: If you're using IntelliJ IDEA, you can install the PlantUML plugin and view the diagrams directly in the IDE.

3. **VS Code**: If you're using VS Code, you can install the PlantUML extension and view the diagrams directly in the editor.

4. **Command Line**: You can use the PlantUML command-line tool to generate images from the PlantUML files:
   ```
   java -jar plantuml.jar class-diagram.puml
   java -jar plantuml.jar sequence-diagram.puml
   java -jar plantuml.jar state-diagram.puml
   ```

### Diagram Overview

#### Class Diagram

The class diagram shows the following components of the Homely application:

- **Entities**: User, Role, Order, OrderItem, StockItem, Setting
- **Enums**: OrderStatus, StockCategory
- **Services**: OrderService, StockService, UserService
- **Controllers**: OrderController, AuthController, UserRestController
- **Repositories**: OrderRepository, StockItemRepository, UserRepository, RoleRepository, SettingRepository

The diagram also shows the relationships between these components, such as associations, dependencies, and inheritance.

#### Sequence Diagram

The sequence diagram illustrates the order creation process in the Homely application. It shows the following interactions:

1. Customer sends an order request to the OrderController
2. OrderController forwards the request to the OrderService
3. OrderService validates the request using OrderRequestValidator
4. If validation fails, an InvalidOrderException is thrown
5. If validation passes, OrderService retrieves the customer from UserService
6. For each item in the order, OrderService retrieves the StockItem from StockItemRepository
7. OrderService creates OrderItems and calculates subtotals
8. OrderService calculates the total amount for the order
9. OrderService saves the order to OrderRepository
10. OrderService sends a notification via NotificationService
11. OrderService returns the created order to OrderController
12. OrderController returns the order to the Customer

This diagram helps visualize the flow of data and control during the order creation process.

#### State Diagram

The state diagram illustrates the lifecycle of an order in the Homely application. It shows the following states and transitions:

- **PENDING**: Initial state when an order is created
- **CONFIRMED**: Order has been confirmed by the system
- **PREPARING**: Order is being prepared
- **READY**: Order preparation is complete
- **OUT_FOR_DELIVERY**: Order is being delivered
- **COMPLETED**: Order has been completed
- **CANCELLED**: Order has been cancelled

The diagram shows the possible transitions between these states, such as:
- PENDING → CONFIRMED (Confirm Order)
- PENDING → CANCELLED (Cancel Order)
- CONFIRMED → PREPARING (Start Preparation)
- PREPARING → READY (Preparation Complete)
- READY → OUT_FOR_DELIVERY (Start Delivery)
- OUT_FOR_DELIVERY → COMPLETED (Delivery Complete)
- Various states → CANCELLED (Cancel Order)

This diagram helps visualize the possible states of an order and the valid transitions between them.
