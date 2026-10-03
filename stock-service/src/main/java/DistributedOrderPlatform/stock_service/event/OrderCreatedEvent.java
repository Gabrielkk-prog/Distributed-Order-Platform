package DistributedOrderPlatform.stock_service.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID eventId,
        UUID orderId,
        String clientId, // Alterado de UUID para String para bater com o envio
        BigDecimal totalAmount,
        Instant createdAt,
        List<OrderItemEvent> items) {
}
