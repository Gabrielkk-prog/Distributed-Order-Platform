package com.distributedorderplatform.order_service.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

public record OrderItemRequest(

                @NotNull UUID productId,

                @NotNull @Positive Integer quantity,

                @NotNull @Positive BigDecimal unitPrice) {
}