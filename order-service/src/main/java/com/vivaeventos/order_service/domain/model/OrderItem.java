package com.vivaeventos.order_service.domain.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
@Getter
public class OrderItem {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID ticketTypeId;

    @Column(nullable = false)
    private String ticketTypeName;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal subtotal;

    // Constructor vacío
    protected OrderItem() {}

    public OrderItem(UUID ticketTypeId, String ticketTypeName, int quantity, BigDecimal unitPrice) {
        this.id = UUID.randomUUID();
        this.ticketTypeId = ticketTypeId;
        this.ticketTypeName = ticketTypeName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}