package com.distributedorderplatform.order_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record OrderRequest(
        @NotBlank String clientId,
        @NotEmpty List<@Valid OrderItemRequest> items) {
}
