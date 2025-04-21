package org.bits.pilani.homely.exception;

public class OrderNotFoundException extends HomelyException {
    public OrderNotFoundException(String message) {
        super(message, "ORDER_NOT_FOUND");
    }
}