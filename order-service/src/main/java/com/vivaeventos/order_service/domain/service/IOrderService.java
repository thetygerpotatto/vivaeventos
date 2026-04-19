package com.vivaeventos.order_service.domain.service;

import com.vivaeventos.order_service.domain.model.Order;
import com.vivaeventos.order_service.domain.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface IOrderService {

    Order createOrder(UUID eventId, UUID buyerId);

    Order addItemToOrder(UUID orderId, UUID ticketTypeId,
                         String ticketTypeName, int quantity, BigDecimal unitPrice);

    Order applyDiscount(UUID orderId, String discountCode);

    Order expireOrder(UUID orderId);

    Order confirmOrder(UUID orderId);

    Optional<Order> findById(UUID orderId);

    List<Order> findByBuyerId(UUID buyerId);

    List<Order> findByEventId(UUID eventId);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findExpiredOrders(LocalDateTime dateTime);
}
