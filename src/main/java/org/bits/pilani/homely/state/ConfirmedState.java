package org.bits.pilani.homely.state;

import lombok.RequiredArgsConstructor;
import org.bits.pilani.homely.entity.Order;
import org.bits.pilani.homely.entity.OrderItem;
import org.bits.pilani.homely.entity.StockItem;
import org.bits.pilani.homely.enums.OrderStatus;
import org.bits.pilani.homely.exception.InvalidStateTransitionException;
import org.bits.pilani.homely.repository.StockItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConfirmedState implements OrderState {
    private final StockItemRepository stockItemRepository;

    @Override
    @Transactional
    public void handle(Order order) {
        for (OrderItem item : order.getItems()) {
            StockItem stockItem = item.getStockItem();
            double newQuantity = stockItem.getQuantity() - item.getQuantity();

            if (newQuantity < 0) {
                throw new InvalidStateTransitionException(
                        "Insufficient stock for item: " + stockItem.getName());
            }

            stockItem.setQuantity(newQuantity);
            stockItemRepository.save(stockItem);
        }
    }

    @Override
    public OrderStatus getStatus() {
        return OrderStatus.CONFIRMED;
    }
}