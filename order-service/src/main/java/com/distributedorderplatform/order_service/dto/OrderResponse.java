package com.distributedorderplatform.order_service.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import com.distributedorderplatform.order_service.entity.OrderStatus;

public record OrderResponse(
                UUID id,
                UUID clientId,
                OrderStatus status,
                BigDecimal totalAmount,
                List<OrderItemResponse> items, // <-- CORRIGIDO PARA USAR O DTO DE RESPONSE
                Instant createdAt) {
}
