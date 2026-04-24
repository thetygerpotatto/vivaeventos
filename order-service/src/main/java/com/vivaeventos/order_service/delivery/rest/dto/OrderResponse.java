package com.vivaeventos.order_service.delivery.rest.dto;

import com.vivaeventos.order_service.domain.model.Order;
import com.vivaeventos.order_service.domain.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID orderId,
        UUID eventId,
        UUID buyerId,
        OrderStatus status,
        BigDecimal total,
        BigDecimal discountAmount,
        String discountCode,
        LocalDateTime createdAt,
        LocalDateTime expiresAt,
        List<OrderItemResponse> items
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getEventId(),
                order.getBuyerId(),
                order.getStatus(),
                order.getTotal(),
                order.getDiscountAmount(),
                order.getDiscountCode(),
                order.getCreatedAt(),
                order.getExpiresAt(),
                order.getItems().stream()
                        .map(OrderItemResponse::from)
                        .toList()
        );
    }
}