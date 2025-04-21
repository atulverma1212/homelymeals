package org.bits.pilani.homely.state;

import org.bits.pilani.homely.enums.OrderStatus;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderStateFactory {

    private final EnumMap<OrderStatus, OrderState> orderStates;

    public OrderStateFactory(List<OrderState> orderStates) {
        this.orderStates = orderStates.stream()
                .collect(Collectors.toMap(
                        OrderState::getStatus,
                        state -> state,
                        (existing, replacement) -> replacement,
                        () -> new EnumMap<>(OrderStatus.class)
                ));
    }

    public Optional<OrderState> getOrderState(OrderStatus status) {
        return Optional.ofNullable(orderStates.get(status));
    }

}
