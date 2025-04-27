package org.bits.pilani.homely.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.bits.pilani.homely.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private OrderRequest validOrderRequest;
    private Order testOrder;
    private List<Order> testOrders;

    @BeforeEach
    void setUp() {
        // Initialize MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(orderController)
                .setControllerAdvice(new org.bits.pilani.homely.exception.HomelyExceptionHandler())
                .build();

        // Initialize ObjectMapper
        objectMapper = new ObjectMapper();

        // Set up test user
        User testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        // Set up test stock item
        StockItem testStockItem = new StockItem();
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

        // Set up test orders list
        Order testOrder2 = new Order();
        testOrder2.setId(2L);
        testOrder2.setCustomer(testUser);
        testOrder2.setCustomerName(testUser.getUsername());
        testOrder2.setOrderDate(LocalDateTime.now().minusDays(1));
        testOrder2.setStatus(OrderStatus.COMPLETED);
        testOrder2.setItems(Collections.singletonList(orderItem));
        testOrder2.setTotalAmount(new BigDecimal("200.00"));

        testOrders = Arrays.asList(testOrder, testOrder2);
    }

    @Test
    void createOrder_ValidRequest_ReturnsCreatedOrder() throws Exception {
        when(orderService.createOrder(any(OrderRequest.class))).thenReturn(testOrder);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validOrderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.customerName", is("testuser")))
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.totalAmount", is(200.00)));
    }

    @Test
    void createOrder_InvalidRequest_ReturnsBadRequest() throws Exception {
        when(orderService.createOrder(any(OrderRequest.class)))
                .thenThrow(new InvalidOrderException("Order must contain at least one item"));

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validOrderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Order must contain at least one item")));
    }

    @Test
    void getOrder_ExistingId_ReturnsOrder() throws Exception {
        when(orderService.getOrder(1L)).thenReturn(testOrder);

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.customerName", is("testuser")))
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.totalAmount", is(200.00)));
    }

    @Test
    void getOrder_NonExistingId_ReturnsNotFound() throws Exception {
        when(orderService.getOrder(999L))
                .thenThrow(new OrderNotFoundException("Order not found with id: 999"));

        mockMvc.perform(get("/api/orders/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Order not found with id: 999")));
    }

    @Test
    void getCustomerOrders_ReturnsOrderList() throws Exception {
        when(orderService.getCustomerOrders(1L)).thenReturn(testOrders);

        mockMvc.perform(get("/api/orders/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].status", is("PENDING")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].status", is("COMPLETED")));
    }
}
