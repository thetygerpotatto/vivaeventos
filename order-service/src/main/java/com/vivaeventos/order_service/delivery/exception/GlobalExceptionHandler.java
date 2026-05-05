package com.vivaeventos.order_service.delivery.exception;


import com.vivaeventos.order_service.domain.exception.EstadoPedidoInvalido;
import com.vivaeventos.order_service.domain.exception.CodigoDescuentoInvalidoException;
import com.vivaeventos.order_service.domain.exception.OrdenExpiradaException;
import com.vivaeventos.order_service.domain.exception.OrdenNoEncontradaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.xml.transform.sax.SAXResult;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OrdenNoEncontradaException.class)
    public ResponseEntity<Map<String, Object>> handleOrdenNoEncontrada(OrdenExpiradaException ex){
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(OrdenExpiradaException.class)
    public ResponseEntity<Map<String, Object>> handleOrdenExpirada(OrdenExpiradaException ex){
        return buildResponse(HttpStatus.GONE, ex.getMessage());
    }

    @ExceptionHandler(EstadoPedidoInvalido.class)
    public ResponseEntity<Map<String, Object>> handleEstadoPedidoInvalido(EstadoPedidoInvalido ex){
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(CodigoDescuentoInvalidoException.class)
    public ResponseEntity<Map<String, Object>> handleCodigoDescuentoInvalido(CodigoDescuentoInvalidoException ex){
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex){
        Map<String, Object> errors = new HashMap<>();
        errors.put("timestamp", LocalDateTime.now());
        errors.put("status", HttpStatus.BAD_REQUEST.value());
        errors.put("error", "Error de validación");

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err -> fieldErrors.put(err.getField(), err.getDefaultMessage()));
        errors.put("fields", fieldErrors);

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", message);
        return ResponseEntity.status(status).body(body);
    }
}
