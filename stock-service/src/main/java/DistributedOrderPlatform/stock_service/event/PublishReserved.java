package DistributedOrderPlatform.stock_service.event;

import java.util.UUID;
import java.time.Instant;

public record PublishReserved(
        UUID id,
        UUID orderId,
        Instant createdAt) {
}
