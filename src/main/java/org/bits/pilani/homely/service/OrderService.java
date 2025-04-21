package org.bits.pilani.homely.service;

import lombok.RequiredArgsConstructor;
import org.bits.pilani.homely.dto.OrderRequest;
import org.bits.pilani.homely.entity.Order;
import org.bits.pilani.homely.entity.OrderItem;
import org.bits.pilani.homely.entity.StockItem;
import org.bits.pilani.homely.enums.OrderStatus;
import org.bits.pilani.homely.exception.InvalidOrderException;
import org.bits.pilani.homely.exception.OrderNotFoundException;
import org.bits.pilani.homely.exception.StockItemNotFoundException;
import org.bits.pilani.homely.repository.OrderRepository;
import org.bits.pilani.homely.repository.StockItemRepository;
import org.bits.pilani.homely.state.OrderStateFactory;
import org.bits.pilani.homely.validator.OrderRequestValidator;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final StockItemRepository stockItemRepository;
    private final OrderRequestValidator orderRequestValidator;
    private final OrderStateTransitionService stateTransitionService;
    private final OrderStateFactory orderStateFactory;

    @Transactional
    public Order createOrder(OrderRequest request) {

        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(request, "orderRequest");
        orderRequestValidator.validate(request, errors);

        if (errors.hasErrors()) {
            throw new InvalidOrderException(
                    errors.getAllErrors().stream()
                            .map(DefaultMessageSourceResolvable::getDefaultMessage)
                            .collect(Collectors.joining("; "))
            );
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new InvalidOrderException("Order must contain at least one item");
        }
        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setCustomerName(request.getCustomerName());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> orderItems = request.getItems().stream()
                .map(item -> {
                    StockItem stockItem = stockItemRepository.findById(item.getStockItemId())
                            .orElseThrow(() -> new StockItemNotFoundException(
                                    "Stock item not found with id: " + item.getStockItemId()));


                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setStockItem(stockItem);
                    orderItem.setQuantity(item.getQuantity());
                    orderItem.setUnitPrice(stockItem.getPrice());
                    orderItem.setSubtotal(stockItem.getPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity())));
                    return orderItem;
                })
                .collect(Collectors.toList());

        order.setItems(orderItems);
        order.setTotalAmount(orderItems.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        return orderRepository.save(order);
    }

    public List<Order> getCustomerOrders(String customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    public Order getOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(
                        "Order not found with id: " + id));
    }

    @Transactional
    public Order updateOrderStatus(Long id, OrderStatus status) {
        Order order = getOrder(id);

        if (!stateTransitionService.isTransitionAllowed(order.getStatus(), status)) {
            throw new InvalidOrderException(
                    "Invalid status transition from " + order.getStatus() + " to " + status);
        }

        orderStateFactory.getOrderState(status)
                .ifPresent(orderState -> orderState.handle(order));

        order.setStatus(status);
        return orderRepository.save(order);
    }

    public void getAllOrders(String status, Model model) {
        List<Order> orders;
        if (status != null && !status.equals("all")) {
            orders = orderRepository.findByStatusOrderByOrderDateDesc(OrderStatus.valueOf(status));
        } else {
            orders = orderRepository.findAll(Sort.by(Sort.Direction.DESC, "orderDate"));
        }
        Map<Long, List<String>> orderTransitions = orders.stream()
                .collect(Collectors.toMap(
                        Order::getId,
                        order -> stateTransitionService.getAllowedTransitions(order.getStatus())
                                .stream()
                                .map(OrderStatus::name)
                                .collect(Collectors.toList())
                ));
        model.addAttribute("orders", orders);
        model.addAttribute("orderTransitions", orderTransitions);
    }
}