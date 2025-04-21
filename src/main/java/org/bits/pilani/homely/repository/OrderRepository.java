package org.bits.pilani.homely.repository;


import org.bits.pilani.homely.entity.Order;
import org.bits.pilani.homely.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerId(String customerId);

    List<Order> findByCustomerIdAndStatus(String customerId, OrderStatus status);

    long countByCustomerId(String customerId);

    List<Order> findByStatusOrderByOrderDateDesc(OrderStatus status);
}