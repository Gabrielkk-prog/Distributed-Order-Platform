package com.distributedorderplatform.order_service.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.antlr.v4.runtime.misc.NotNull;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

public record OrderItemResponse(

        UUID productId,
        Integer quantity,
        BigDecimal unitPrice) {
}