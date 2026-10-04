package com.distributedorderplatform.order_service.event;

import java.time.Instant;
import java.util.UUID;

public record StockReservedEvent(
        UUID id,
        UUID orderId,
        String message,
        Instant createdAt) {
}
