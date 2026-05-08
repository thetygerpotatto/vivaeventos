package com.vivaeventos.order_service.delivery.rest;


import com.vivaeventos.order_service.delivery.rest.dto.ApplyDiscountRequest;
import com.vivaeventos.order_service.delivery.rest.dto.CreateOrderRequest;
import com.vivaeventos.order_service.delivery.rest.dto.OrderResponse;
import com.vivaeventos.order_service.domain.model.Order;
import com.vivaeventos.order_service.domain.service.IOrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final IOrderService orderService;

    public OrderController(IOrderService orderService){
        this.orderService = orderService;
    }

    //Crear órdenes con items
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request){
        Order order = orderService.createOrder(request.eventId(), request.buyerId());

        request.items().forEach(item -> orderService.addItemToOrder(
                order.getId(),
                item.ticketTypeId(),
                item.ticketTypeName(),
                item.quantity(),
                item.unitPrice()
        )
        );

        Order updatedOrder = orderService.findById(order.getId()).orElseThrow();

        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.from(updatedOrder));
    }

    // Consultar una orden por ID
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID orderId){
        Order order = orderService.findById(orderId).orElseThrow();

        return ResponseEntity.ok(OrderResponse.from(order));
    }

    // Consultar órdenes de un comprador
    @GetMapping ("/buyer/{buyerId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByBuyer(@PathVariable UUID buyerId){
        List<OrderResponse> orders = orderService.findByBuyerId(buyerId)
                .stream()
                .map(OrderResponse::from)
                .toList();

        return ResponseEntity.ok(orders);
    }

    // Consultar órdenes de un evento
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByEvent(@PathVariable UUID eventId){
        List<OrderResponse> orders = orderService.findByEventId(eventId)
                .stream()
                .map(OrderResponse::from)
                .toList();

        return ResponseEntity.ok(orders);
    }

    // Aplicar código de descuento
    @PostMapping("/{orderId}/discount")
    public ResponseEntity<OrderResponse> applyDiscount(@PathVariable UUID orderId, @Valid @RequestBody ApplyDiscountRequest request){

        Order order = orderService.applyDiscount(orderId, request.discountCode());

        return ResponseEntity.ok(OrderResponse.from(order));
    }

    // Expirar una orden manualmente
    @PatchMapping("/{orderId}/expire")
    public ResponseEntity<OrderResponse> expireOrder(@PathVariable UUID orderId) {
        Order order = orderService.expireOrder(orderId);
        return ResponseEntity.ok(OrderResponse.from(order));
    }

    // Confirmar una orden
    @PatchMapping("/{orderId}/confirm")
    public ResponseEntity<OrderResponse> confirmOrder(@PathVariable UUID orderId) {
        Order order = orderService.confirmOrder(orderId);
        return ResponseEntity.ok(OrderResponse.from(order));
    }
}