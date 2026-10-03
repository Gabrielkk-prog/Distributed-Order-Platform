package DistributedOrderPlatform.stock_service.event;

import java.time.Instant;
import java.util.UUID;

public record StockReservedEvent(
        UUID eventId,
        UUID orderId,
        Instant createdAt) {
}
