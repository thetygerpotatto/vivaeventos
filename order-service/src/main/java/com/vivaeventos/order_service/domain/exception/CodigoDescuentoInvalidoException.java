package com.vivaeventos.order_service.domain.exception;

public class CodigoDescuentoInvalidoException extends RuntimeException {
    public CodigoDescuentoInvalidoException(String reason) {
        super("Código de descuento inválido: " + reason);
    }
}
