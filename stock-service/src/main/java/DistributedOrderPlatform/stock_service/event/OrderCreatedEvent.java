package DistributedOrderPlatform.stock_service.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID eventId,
        UUID correlationId,
        UUID orderId,
        UUID clientId,
        BigDecimal totalAmount,
        Instant createdAt,
        List<OrderItemEvent> items) {
}