package com.vivaeventos.order_service.domain.exception;

public class EstadoPedidoInvalido extends RuntimeException {
    public EstadoPedidoInvalido(String message) {
        super(message);
    }
}
