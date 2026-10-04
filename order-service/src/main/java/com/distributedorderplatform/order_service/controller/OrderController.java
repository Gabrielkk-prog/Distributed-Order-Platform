package com.distributedorderplatform.order_service.controller;

import com.distributedorderplatform.order_service.dto.OrderRequest;
import com.distributedorderplatform.order_service.dto.OrderResponse;
import com.distributedorderplatform.order_service.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody OrderRequest request) {
        // 1. O service cria o pedido e retorna o record populado
        OrderResponse response = orderService.create(request);

        // 2. CORREÇÃO: Você PRECISA passar o objeto 'response' dentro do método .body()
        return ResponseEntity
                .status(HttpStatus.CREATED) // Retorna o status 201
                .body(response); // 🚀 ISSO ENVIA O JSON PARA O INSOMNIA!
    }
}
