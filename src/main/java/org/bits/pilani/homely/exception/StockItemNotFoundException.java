package org.bits.pilani.homely.exception;

public class StockItemNotFoundException extends HomelyException {
    public StockItemNotFoundException(String message) {
        super(message, "STOCK_ITEM_NOT_FOUND");
    }
}