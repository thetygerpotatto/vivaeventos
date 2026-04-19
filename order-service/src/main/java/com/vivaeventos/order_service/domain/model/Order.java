package com.vivaeventos.order_service.domain.model;

import com.vivaeventos.order_service.domain.exception.EstadoPedidoInvalido;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Getter
public class Order {

    private static final int RESERVATION_MINUTES = 10;

    private UUID id;
    private UUID eventId;
    private UUID buyerId;
    private List<OrderItem> items;
    private OrderStatus status;
    private BigDecimal total;
    private BigDecimal discountAmount;
    private String discountCode;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;


    public Order(UUID eventId, UUID buyerId) {
        this.id = UUID.randomUUID();
        this.eventId = eventId;
        this.buyerId = buyerId;
        this.items = new ArrayList<>();
        this.status = OrderStatus.CREATED;
        this.total = BigDecimal.ZERO;
        this.discountAmount = BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = this.createdAt.plusMinutes(RESERVATION_MINUTES);
    }


    public void addItem(OrderItem item) {
        this.items.add(item);
        recalculateTotal();
    }

    public void applyDiscount(BigDecimal discountAmount, String discountCode) {
        this.discountAmount = discountAmount;
        this.discountCode = discountCode;
        recalculateTotal();
    }

    public void expire() {
        if (this.status != OrderStatus.CREATED && this.status != OrderStatus.PENDING) {
            throw new EstadoPedidoInvalido("Solo se puede expirar una orden CREATED o PENDING");
        }
        this.status = OrderStatus.EXPIRED;
    }

    public void confirm() {
        if (this.status != OrderStatus.PENDING) {
            throw new EstadoPedidoInvalido("Solo se puede confirmar una orden PENDING");
        }
        this.status = OrderStatus.CONFIRMED;
    }

    public void markAsPending() {
        if (this.status != OrderStatus.CREATED) {
            throw new EstadoPedidoInvalido("Solo se puede pasar a PENDING desde CREATED");
        }
        this.status = OrderStatus.PENDING;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }

    private void recalculateTotal() {
        BigDecimal subtotal = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.total = subtotal.subtract(discountAmount).max(BigDecimal.ZERO);
    }
}