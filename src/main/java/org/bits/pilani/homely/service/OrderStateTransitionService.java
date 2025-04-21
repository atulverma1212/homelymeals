package org.bits.pilani.homely.service;


import lombok.RequiredArgsConstructor;
import org.bits.pilani.homely.enums.OrderStatus;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderStateTransitionService {
    private final Map<OrderStatus, Set<OrderStatus>> allowedTransitions;

    public OrderStateTransitionService() {
        this.allowedTransitions = new EnumMap<>(OrderStatus.class);

        allowedTransitions.put(OrderStatus.PENDING, Set.of(
                OrderStatus.CONFIRMED,
                OrderStatus.CANCELLED
        ));
        allowedTransitions.put(OrderStatus.CONFIRMED, Set.of(
                OrderStatus.READY, OrderStatus.CANCELLED
        ));
        allowedTransitions.put(OrderStatus.READY, Set.of(OrderStatus.OUT_FOR_DELIVERY, OrderStatus.CANCELLED));
        allowedTransitions.put(OrderStatus.OUT_FOR_DELIVERY, Set.of(OrderStatus.COMPLETED));
        allowedTransitions.put(OrderStatus.CANCELLED, Set.of());
        allowedTransitions.put(OrderStatus.COMPLETED, Set.of());
    }

    public Set<OrderStatus> getAllowedTransitions(OrderStatus currentStatus) {
        return allowedTransitions.getOrDefault(currentStatus, Set.of());
    }

    public boolean isTransitionAllowed(OrderStatus from, OrderStatus to) {
        return getAllowedTransitions(from).contains(to);
    }
}
