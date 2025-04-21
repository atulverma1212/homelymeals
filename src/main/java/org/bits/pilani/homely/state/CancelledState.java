package org.bits.pilani.homely.state;

import lombok.RequiredArgsConstructor;
import org.bits.pilani.homely.entity.Order;
import org.bits.pilani.homely.entity.OrderItem;
import org.bits.pilani.homely.entity.StockItem;
import org.bits.pilani.homely.enums.OrderStatus;
import org.bits.pilani.homely.repository.StockItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CancelledState implements OrderState {
    private final StockItemRepository stockItemRepository;

    @Override
    @Transactional
    public void handle(Order order) {
        if (shouldRestoreStock(order.getStatus())) {
            for (OrderItem item : order.getItems()) {
                StockItem stockItem = item.getStockItem();
                stockItem.setQuantity(stockItem.getQuantity() + item.getQuantity());
                stockItemRepository.save(stockItem);
            }
        }
    }

    @Override
    public OrderStatus getStatus() {
        return OrderStatus.CANCELLED;
    }

    private boolean shouldRestoreStock(OrderStatus currentStatus) {
        return currentStatus == OrderStatus.CONFIRMED ||
                currentStatus == OrderStatus.READY;
    }
}