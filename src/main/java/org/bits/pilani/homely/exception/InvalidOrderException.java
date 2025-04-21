package org.bits.pilani.homely.exception;

public class InvalidOrderException extends HomelyException {
    public InvalidOrderException(String message) {
        super(message, "INVALID_ORDER");
    }
}