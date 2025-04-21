package org.bits.pilani.homely.exception;

public class InvalidStateTransitionException extends HomelyException {
    public InvalidStateTransitionException(String message) {
        super(message, "INVALID_STATE_TRANSITION");
    }
}