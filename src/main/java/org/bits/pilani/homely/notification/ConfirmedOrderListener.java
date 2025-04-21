package org.bits.pilani.homely.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bits.pilani.homely.entity.Order;
import org.bits.pilani.homely.enums.OrderStatus;
import org.bits.pilani.homely.service.NotificationService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConfirmedOrderListener implements StateChangeListener {

    private final NotificationService notificationService;

    @Override
    public void onStateChange(Order order) {
        if(OrderStatus.CONFIRMED.equals(order.getStatus())) {
            log.info("Order has been confirmed. Notify the customer.");
            String message = String.format("Dear %s, your order #%d has been confirmed.",
                    order.getCustomerName(), order.getId());
            notificationService.sendSms(order.getCustomer().getContactnumber(), message);
        }
    }
}