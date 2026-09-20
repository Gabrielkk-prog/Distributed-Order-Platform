package DistributedOrderPlatform.client_service.dto;

import java.time.Instant;
import java.util.UUID;

public record ClientResponse(
                UUID id,
                String name,
                String email,
                Instant createdAt) {
}
