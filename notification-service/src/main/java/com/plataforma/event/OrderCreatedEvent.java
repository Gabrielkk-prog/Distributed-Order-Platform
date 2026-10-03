package com.plataforma.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID eventId,
        UUID orderId,
        UUID clientId,
        BigDecimal totalAmount,
        Instant createdAt) {
}