package com.vivaeventos.order_service.domain.exception;

public class OrdenExpiradaException extends RuntimeException {
    public OrdenExpiradaException() {
        super("La orden ha expirado. Cupo liberado");
    }
}
