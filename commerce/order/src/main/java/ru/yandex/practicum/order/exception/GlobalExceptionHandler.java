package ru.yandex.practicum.order.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Глобальный обработчик исключений для Order Service.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoOrderFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoOrderFound(NoOrderFoundException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotAuthorizedUserException.class)
    public ResponseEntity<Map<String, Object>> handleNotAuthorized(NotAuthorizedUserException ex) {
        return buildErrorResponse(ex, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(Exception ex, HttpStatus status) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("message", ex.getMessage());
        body.put("userMessage", ex.getMessage());
        return ResponseEntity.status(status).body(body);
    }
}
