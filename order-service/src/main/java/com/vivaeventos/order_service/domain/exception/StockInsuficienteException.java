package com.vivaeventos.order_service.domain.exception;

public class StockInsuficienteException extends RuntimeException {
    public StockInsuficienteException(String ticketTypeName) {
        super("No hay cupos disponibles para:" + ticketTypeName);
    }
}
