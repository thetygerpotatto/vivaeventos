package com.vivaeventos.order_service.delivery.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(

        @NotNull(message = "El ID del evento es obligatorio")
        UUID eventId,

        @NotNull(message = "El ID del comprador es obligatorio")
        UUID buyerId,

        @NotEmpty(message = "La orden debe tener al menos un ítem")
        @Valid
        List<OrderItemRequest> items
) {}