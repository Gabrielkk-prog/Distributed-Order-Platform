package DistributedOrderPlatform.stock_service.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderItemEvent(
        UUID productId,
        Integer quantity) {
}