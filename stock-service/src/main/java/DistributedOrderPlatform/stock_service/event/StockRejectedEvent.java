package DistributedOrderPlatform.stock_service.event;

import java.time.Instant;
import java.util.UUID;

public record StockRejectedEvent(
        UUID eventId,
        UUID orderId,
        String reason,
        Instant createdAt) {
}