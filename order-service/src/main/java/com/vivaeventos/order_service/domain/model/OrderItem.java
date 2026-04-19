package com.vivaeventos.order_service.domain.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class OrderItem {

    private UUID id;
    private UUID ticketTypeId;
    private String ticketTypeName;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;

    public OrderItem(UUID ticketTypeId, String ticketTypeName, int quantity, BigDecimal unitPrice) {
        this.id = UUID.randomUUID();
        this.ticketTypeId = ticketTypeId;
        this.ticketTypeName = ticketTypeName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

}