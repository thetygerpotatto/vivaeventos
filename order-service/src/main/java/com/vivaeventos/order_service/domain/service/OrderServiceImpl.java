package com.vivaeventos.order_service.domain.service;

import com.vivaeventos.order_service.domain.exception.CodigoDescuentoInvalidoException;
import com.vivaeventos.order_service.domain.exception.OrdenExpiradaException;
import com.vivaeventos.order_service.domain.exception.OrdenNoEncontradaException;
import com.vivaeventos.order_service.domain.model.DiscountCode;
import com.vivaeventos.order_service.domain.model.Order;
import com.vivaeventos.order_service.domain.model.OrderItem;
import com.vivaeventos.order_service.domain.model.OrderStatus;
import com.vivaeventos.order_service.domain.repository.IDiscountCodeRepository;
import com.vivaeventos.order_service.domain.repository.IOrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderServiceImpl implements IOrderService {

    private final IOrderRepository orderRepository;
    private final IDiscountCodeRepository discountCodeRepository;

    // Inyección por constructor
    public OrderServiceImpl(IOrderRepository orderRepository,
                             IDiscountCodeRepository discountCodeRepository) {
        this.orderRepository = orderRepository;
        this.discountCodeRepository = discountCodeRepository;
    }

    @Override
    public Order createOrder(UUID eventId, UUID buyerId) {
        Order order = new Order(eventId, buyerId);
        return orderRepository.save(order);
    }

    @Override
    public Order addItemToOrder(UUID orderId, UUID ticketTypeId, String ticketTypeName, int quantity, BigDecimal unitPrice) {
        Order order = findOrderOrThrow(orderId);

        if (order.isExpired()) {
            throw new OrdenExpiradaException();
        }

        OrderItem item = new OrderItem(ticketTypeId, ticketTypeName, quantity, unitPrice);
        order.addItem(item);

        return orderRepository.save(order);
    }

    @Override
    public Order applyDiscount(UUID orderId, String discountCode) {
        Order order = findOrderOrThrow(orderId);

        if (order.isExpired()) {
            throw new OrdenExpiradaException();
        }

        DiscountCode code = discountCodeRepository.findByCode(discountCode)
                .orElseThrow(() -> new CodigoDescuentoInvalidoException("El código no existe"));

        if (!code.isValid()) {
            throw new CodigoDescuentoInvalidoException("El código está vencido o inactivo");
        }

        BigDecimal discountAmount = code.calculateDiscount(order.getTotal());
        order.applyDiscount(discountAmount, discountCode);

        return orderRepository.save(order);
    }

    @Override
    public Order expireOrder(UUID orderId) {
        Order order = findOrderOrThrow(orderId);
        order.expire();
        return orderRepository.save(order);
    }

    @Override
    public Order confirmOrder(UUID orderId) {
        Order order = findOrderOrThrow(orderId);
        order.confirm();
        return orderRepository.save(order);
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    public List<Order> findByBuyerId(UUID buyerId) {
        return orderRepository.findByBuyerId(buyerId);
    }

    @Override
    public List<Order> findByEventId(UUID eventId) {
        return orderRepository.findByEventId(eventId);
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Override
    public List<Order> findExpiredOrders(LocalDateTime dateTime) {
        return orderRepository.findByStatusAndExpiresAtBefore(OrderStatus.CREATED, dateTime);
    }

    private Order findOrderOrThrow(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrdenNoEncontradaException(orderId.toString()));
    }
}