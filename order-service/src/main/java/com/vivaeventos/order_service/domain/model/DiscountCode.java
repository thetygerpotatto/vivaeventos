package com.vivaeventos.order_service.domain.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "discount_codes")
@Getter
public class DiscountCode {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal value;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    // Constructor vacío requerido por JPA
    protected DiscountCode() {}

    public DiscountCode(String code, DiscountType type, BigDecimal value, LocalDateTime expiresAt) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.type = type;
        this.value = value;
        this.active = true;
        this.expiresAt = expiresAt;
    }

    public boolean isValid() {
        return active && LocalDateTime.now().isBefore(expiresAt);
    }

    public BigDecimal calculateDiscount(BigDecimal orderTotal) {
        if (type == DiscountType.PERCENTAGE) {
            return orderTotal.multiply(value).divide(BigDecimal.valueOf(100));
        }
        return value.min(orderTotal);
    }
}