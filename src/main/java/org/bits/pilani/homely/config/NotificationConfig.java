package org.bits.pilani.homely.config;

import org.bits.pilani.homely.notification.StateChangeListener;
import org.bits.pilani.homely.service.OrderService;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class NotificationConfig {

    public NotificationConfig(OrderService orderService, List<StateChangeListener> listener) {
        listener.forEach(orderService::addStateChangeListener);
    }
}