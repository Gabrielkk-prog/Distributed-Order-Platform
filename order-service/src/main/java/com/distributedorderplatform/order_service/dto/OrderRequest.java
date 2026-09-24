package com.distributedorderplatform.order_service.dto;

import java.util.List;
import java.util.UUID;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;

public record OrderRequest(
        @NotNull UUID clientId,
        @NotEmpty List<OrderItemRequest> items // <-- CORRIGIDO AQUI!
) {
}
