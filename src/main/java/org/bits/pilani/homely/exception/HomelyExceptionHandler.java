package org.bits.pilani.homely.exception;


import org.bits.pilani.homely.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class HomelyExceptionHandler {

    @ExceptionHandler(HomelyException.class)
    public ResponseEntity<ErrorResponse> handleHomelyException(HomelyException ex) {
        return getErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFoundException(OrderNotFoundException ex) {
        return getErrorResponse(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(StockItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStockItemNotFoundException(StockItemNotFoundException ex) {
        return getErrorResponse(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidOrderException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOrderException(InvalidOrderException ex) {
        return getErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidStateTransitionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidStateTransitionException(InvalidStateTransitionException ex) {
        return getErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ErrorResponse> getErrorResponse(HomelyException ex, HttpStatus status) {
        ErrorResponse error = new ErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                status.value()
        );
        return new ResponseEntity<>(error, status);
    }
}