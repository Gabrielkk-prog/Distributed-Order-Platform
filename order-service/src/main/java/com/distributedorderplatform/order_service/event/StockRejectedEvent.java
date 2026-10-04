package com.distributedorderplatform.order_service.event;

import java.time.Instant;
import java.util.UUID;

public record StockRejectedEvent(
        UUID id,
        UUID orderId,
        String reason,
        Instant createdAt) {
}
