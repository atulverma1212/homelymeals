package org.bits.pilani.homely.service;

import org.bits.pilani.homely.dto.OrderItemRequest;
import org.bits.pilani.homely.dto.OrderRequest;
import org.bits.pilani.homely.entity.Order;
import org.bits.pilani.homely.entity.OrderItem;
import org.bits.pilani.homely.entity.StockItem;
import org.bits.pilani.homely.entity.User;
import org.bits.pilani.homely.enums.OrderStatus;
import org.bits.pilani.homely.enums.StockCategory;
import org.bits.pilani.homely.exception.InvalidOrderException;
import org.bits.pilani.homely.exception.OrderNotFoundException;
import org.bits.pilani.homely.notification.StateChangeNotifier;
import org.bits.pilani.homely.repository.OrderRepository;
import org.bits.pilani.homely.repository.StockItemRepository;
import org.bits.pilani.homely.state.OrderStateFactory;
import org.bits.pilani.homely.validator.OrderRequestValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private StockItemRepository stockItemRepository;

    @Mock
    private OrderRequestValidator orderRequestValidator;

    @Mock
    private OrderStateTransitionService stateTransitionService;

    @Mock
    private OrderStateFactory orderStateFactory;

    @Mock
    private UserService userService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private OrderService orderService;

    private User testUser;
    private StockItem testStockItem;
    private OrderRequest validOrderRequest;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        // Set up test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        // Set up test stock item
        testStockItem = new StockItem();
        testStockItem.setId(1L);
        testStockItem.setName("Test Item");
        testStockItem.setQuantity(10.0);
        testStockItem.setUnit("kg");
        testStockItem.setCategory(StockCategory.VEG);
        testStockItem.setPrice(new BigDecimal("100.00"));

        // Set up valid order request
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setStockItemId(1L);
        itemRequest.setQuantity(2.0);

        validOrderRequest = new OrderRequest();
        validOrderRequest.setCustomerId(1L);
        validOrderRequest.setItems(Collections.singletonList(itemRequest));

        // Set up test order
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setCustomer(testUser);
        testOrder.setCustomerName(testUser.getUsername());
        testOrder.setOrderDate(LocalDateTime.now());
        testOrder.setStatus(OrderStatus.PENDING);
        
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(testOrder);
        orderItem.setStockItem(testStockItem);
        orderItem.setQuantity(2.0);
        orderItem.setUnitPrice(testStockItem.getPrice());
        orderItem.setSubtotal(testStockItem.getPrice().multiply(BigDecimal.valueOf(2.0)));
        
        testOrder.setItems(Collections.singletonList(orderItem));
        testOrder.setTotalAmount(new BigDecimal("200.00"));
    }

    @Test
    void createOrder_ValidRequest_ReturnsOrder() {
        // Arrange
        when(userService.getCustomerById(1L)).thenReturn(testUser);
        when(stockItemRepository.findById(1L)).thenReturn(Optional.of(testStockItem));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        
        // Act
        Order result = orderService.createOrder(validOrderRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(testUser, result.getCustomer());
        assertEquals(OrderStatus.PENDING, result.getStatus());
        assertEquals(1, result.getItems().size());
        assertEquals(new BigDecimal("200.00"), result.getTotalAmount());
        
        verify(orderRequestValidator).validate(eq(validOrderRequest), any(BeanPropertyBindingResult.class));
        verify(userService).getCustomerById(1L);
        verify(stockItemRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
        verify(notificationService).sendOrderNotification(anyString());
    }

    @Test
    void createOrder_EmptyItems_ThrowsInvalidOrderException() {
        // Arrange
        OrderRequest emptyRequest = new OrderRequest();
        emptyRequest.setCustomerId(1L);
        emptyRequest.setItems(new ArrayList<>());
        
        // Act & Assert
        InvalidOrderException exception = assertThrows(InvalidOrderException.class, () -> {
            orderService.createOrder(emptyRequest);
        });
        
        assertEquals("Order must contain at least one item", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_ValidationFails_ThrowsInvalidOrderException() {
        // Arrange
        doAnswer(invocation -> {
            Errors errors = invocation.getArgument(1);
            errors.reject("test.error", "Test validation error");
            return null;
        }).when(orderRequestValidator).validate(eq(validOrderRequest), any(Errors.class));
        
        // Act & Assert
        InvalidOrderException exception = assertThrows(InvalidOrderException.class, () -> {
            orderService.createOrder(validOrderRequest);
        });
        
        assertEquals("Test validation error", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void getOrder_ExistingId_ReturnsOrder() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        
        // Act
        Order result = orderService.getOrder(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getOrder_NonExistingId_ThrowsOrderNotFoundException() {
        // Arrange
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () -> {
            orderService.getOrder(999L);
        });
        
        assertEquals("Order not found with id: 999", exception.getMessage());
    }

    @Test
    void getCustomerOrders_ReturnsOrderList() {
        // Arrange
        List<Order> customerOrders = Collections.singletonList(testOrder);
        when(orderRepository.findByCustomerId(1L)).thenReturn(customerOrders);
        
        // Act
        List<Order> result = orderService.getCustomerOrders(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void updateOrderStatus_ValidTransition_UpdatesStatus() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(stateTransitionService.isTransitionAllowed(OrderStatus.PENDING, OrderStatus.CONFIRMED)).thenReturn(true);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        
        // Act
        Order result = orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED);
        
        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.CONFIRMED, result.getStatus());
        
        verify(stateTransitionService).isTransitionAllowed(OrderStatus.PENDING, OrderStatus.CONFIRMED);
        verify(orderStateFactory).getOrderState(OrderStatus.CONFIRMED);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void updateOrderStatus_InvalidTransition_ThrowsInvalidOrderException() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(stateTransitionService.isTransitionAllowed(OrderStatus.PENDING, OrderStatus.COMPLETED)).thenReturn(false);
        
        // Act & Assert
        InvalidOrderException exception = assertThrows(InvalidOrderException.class, () -> {
            orderService.updateOrderStatus(1L, OrderStatus.COMPLETED);
        });
        
        assertEquals("Invalid status transition from PENDING to COMPLETED", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }
}