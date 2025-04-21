package org.bits.pilani.homely.state;

import org.bits.pilani.homely.entity.Order;
import org.bits.pilani.homely.enums.OrderStatus;

public interface OrderState {
    void handle(Order order);
    OrderStatus getStatus();
}