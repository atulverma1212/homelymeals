package org.bits.pilani.homely.exception;

public class HomelyException extends RuntimeException {
    private final String errorCode;

    public HomelyException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}




