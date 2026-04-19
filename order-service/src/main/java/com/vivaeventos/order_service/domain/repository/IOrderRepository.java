package com.vivaeventos.order_service.domain.repository;

import com.vivaeventos.order_service.domain.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IOrderRepository extends JpaRepository<Order, UUID> {
}
