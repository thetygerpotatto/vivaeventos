package com.vivaeventos.order_service.domain.repository;

import com.vivaeventos.order_service.domain.model.Order;
import com.vivaeventos.order_service.domain.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface IOrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findByBuyerId(UUID buyerId);

    List<Order> findByEventId(UUID eventId);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByStatusAndExpiresAtBefore(OrderStatus status, LocalDateTime dateTime);
}