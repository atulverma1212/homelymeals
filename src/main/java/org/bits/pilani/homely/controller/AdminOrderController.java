package org.bits.pilani.homely.controller;

import lombok.RequiredArgsConstructor;
import org.bits.pilani.homely.entity.Order;
import org.bits.pilani.homely.enums.OrderStatus;
import org.bits.pilani.homely.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public String getOrdersPage(
            @RequestParam(required = false) String status,
            Model model) {
        orderService.getAllOrders(status, model);
        return "admin/orders";
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        Order order = orderService.getOrder(id);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/{id}/status")
    @ResponseBody
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        String status = payload.get("status");
        Order updatedOrder = orderService.updateOrderStatus(id, OrderStatus.valueOf(status));
        return ResponseEntity.ok(updatedOrder);
    }

}