package com.vivaeventos.order_service.domain.model;

import com.vivaeventos.order_service.domain.exception.EstadoPedidoInvalido;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
public class Order {

    private static final int RESERVATION_MINUTES = 10;

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID eventId;

    @Column(nullable = false)
    private UUID buyerId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private List<OrderItem> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(20)")
    private OrderStatus status;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal total;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal discountAmount;

    @Column
    private String discountCode;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    // Constructor vacío
    protected Order() {}

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

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    private void recalculateTotal() {
        BigDecimal subtotal = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.total = subtotal.subtract(discountAmount).max(BigDecimal.ZERO);
    }
}