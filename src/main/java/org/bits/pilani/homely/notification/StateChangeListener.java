package org.bits.pilani.homely.notification;

import org.bits.pilani.homely.entity.Order;
import org.bits.pilani.homely.state.OrderState;

@FunctionalInterface
public interface StateChangeListener {
    void onStateChange(Order order);
}