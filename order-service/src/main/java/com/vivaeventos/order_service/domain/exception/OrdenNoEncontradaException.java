package com.vivaeventos.order_service.domain.exception;

public class OrdenNoEncontradaException extends RuntimeException {
    public OrdenNoEncontradaException(String orderId){
        super("Orden no esocntrada con id:" + orderId);
    }
}