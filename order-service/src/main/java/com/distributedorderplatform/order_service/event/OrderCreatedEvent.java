package com.distributedorderplatform.order_service.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.distributedorderplatform.order_service.dto.OrderItemRequest;

public record OrderCreatedEvent(
                UUID eventId,
                UUID correlationId,
                UUID orderId,
                String clientId, // Alterado de UUID para String
                BigDecimal totalAmount,
                Instant createdAt,
                List<OrderItemRequest> items // Alterado de OrderItem para OrderItemRequest
) {
}
