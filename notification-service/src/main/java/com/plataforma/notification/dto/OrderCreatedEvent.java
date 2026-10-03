package com.plataforma.notification.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID orderId,
        String clientId,
        BigDecimal totalAmount) {
}
