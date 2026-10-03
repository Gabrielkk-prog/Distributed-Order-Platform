package com.distributedorderplatform.order_service.event;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID eventId,
        UUID orderId,
        String clientId,
        BigDecimal totalAmount,
        List<?> items // Removido o campo Instant createdAt daqui também
) {}
