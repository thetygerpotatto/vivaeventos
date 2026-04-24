package com.vivaeventos.order_service.delivery.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record ApplyDiscountRequest(

        @NotBlank(message = "El código de descuento no puede estar vacío")
        String discountCode
) {}