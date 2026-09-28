package com.distributedorderplatform.order_service.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        String clientId,
        String status,
        BigDecimal totalAmount,
        List<OrderItemResponse> items) {
}
