package com.vivaeventos.order_service.delivery.rest.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemRequest(

        @NotNull(message = "El ID del tipo de boleta es obligatorio")
        UUID ticketTypeId,

        @NotBlank(message = "El nombre del tipo de boleta es obligatorio")
        String ticketTypeName,

        @Min(value = 1, message = "La cantidad mínima es 1")
        int quantity,

        @NotNull(message = "El precio unitario es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
        BigDecimal unitPrice
) {}