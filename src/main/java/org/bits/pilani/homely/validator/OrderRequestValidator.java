package org.bits.pilani.homely.validator;

import org.bits.pilani.homely.dto.OrderRequest;
import org.bits.pilani.homely.entity.StockItem;
import org.bits.pilani.homely.exception.InvalidOrderException;
import org.bits.pilani.homely.repository.StockItemRepository;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;
import org.springframework.validation.Errors;

@Component
public class OrderRequestValidator implements Validator {

    private final StockItemRepository stockItemRepository;

    public OrderRequestValidator(StockItemRepository stockItemRepository) {
        this.stockItemRepository = stockItemRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return OrderRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        OrderRequest request = (OrderRequest) target;

        if (request.getItems() == null || request.getItems().isEmpty()) {
            errors.reject("items.empty", "Order must contain at least one item");
            return;
        }

        request.getItems().forEach(item -> {
            StockItem stockItem = stockItemRepository.findById(item.getStockItemId())
                    .orElseThrow(() -> new InvalidOrderException(
                            "Stock item not found with id: " + item.getStockItemId()));

            if (item.getQuantity() <= 0) {
                errors.rejectValue("items", "quantity.invalid",
                        "Quantity must be greater than zero for item: " + stockItem.getName());
            }

            if (item.getQuantity() > stockItem.getQuantity()) {
                errors.rejectValue("items", "quantity.insufficient",
                        "Insufficient stock for item: " + stockItem.getName() +
                                ". Available: " + stockItem.getQuantity() +
                                ", Requested: " + item.getQuantity());
            }
        });
    }
}