package com.distributedorderplatform.order_service.controller; // 1. Adicionado o pacote correto

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 2. Corrigido o import para apontar para a pasta service correta
import com.distributedorderplatform.order_service.service.OrderService;
import com.distributedorderplatform.order_service.dto.OrderRequest;
import com.distributedorderplatform.order_service.dto.OrderResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(
            @Valid @RequestBody OrderRequest request) {

        OrderResponse response = service.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
