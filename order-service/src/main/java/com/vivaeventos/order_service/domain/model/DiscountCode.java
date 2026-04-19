package com.vivaeventos.order_service.domain.model;

import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class DiscountCode {

    private UUID id;
    private String code;
    private DiscountType type;
    private BigDecimal value;
    private boolean active;
    private LocalDateTime expiresAt;

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
