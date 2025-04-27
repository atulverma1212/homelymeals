package org.bits.pilani.homely.exception;


import org.bits.pilani.homely.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        ErrorResponse errorResponse = new ErrorResponse("Validation Error", errors.toString(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(org.springframework.web.server.ResponseStatusException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getStatusCode().toString(),
                ex.getReason(),
                ex.getStatusCode().value()
        );
        return new ResponseEntity<>(errorResponse, ex.getStatusCode());
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
