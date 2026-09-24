package com.distributedorderplatform.order_service.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.distributedorderplatform.order_service.dto.OrderItemRequest;
import com.distributedorderplatform.order_service.dto.OrderItemResponse;
import com.distributedorderplatform.order_service.dto.OrderRequest;
import com.distributedorderplatform.order_service.dto.OrderResponse;
import com.distributedorderplatform.order_service.entity.Order;
import com.distributedorderplatform.order_service.entity.OrderStatus;
import com.distributedorderplatform.order_service.entity.OrderItem.OrderItem;
import com.distributedorderplatform.order_service.repository.OrderItemRepository;
import com.distributedorderplatform.order_service.repository.OrderRepository;

import jakarta.transaction.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    // Construtor para injeção de dependência dos dois repositórios
    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional
    public OrderResponse create(OrderRequest request) {

        BigDecimal total = calculateTotal(request.items());

        Order order = new Order();
        order.setClientId(request.clientId());
        order.setStatus(OrderStatus.CREATED);
        order.setTotalAmount(total);

        Order savedOrder = orderRepository.save(order);

        List<OrderItem> orderItems = request.items()
                .stream()
                .map(item -> {
                    OrderItem orderItem = new OrderItem();

                    // CORREÇÃO DOS MÉTODOS (Preste atenção nas letras MAIÚSCULAS do meio)
                    orderItem.setOrderId(savedOrder.getId());
                    orderItem.setProductId(item.productId()); // 'I' maiúsculo
                    orderItem.setQuantity(item.quantity());
                    orderItem.setUnitPrice(item.unitPrice()); // 'P' maiúsculo

                    return orderItem;
                })
                .toList();

        orderItemRepository.saveAll(orderItems);

        return toResponse(savedOrder, orderItems);
    }

    private BigDecimal calculateTotal(List<OrderItemRequest> items) {
        return items.stream()
                .map(item -> item.unitPrice() // 'P' maiúsculo aqui também!
                        .multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private OrderResponse toResponse(Order order, List<OrderItem> items) {
        List<OrderItemResponse> itemResponses = items.stream()
                .map(item -> new OrderItemResponse(
                        item.getProductId(),
                        item.getQuantity(),
                        item.getUnitPrice()))
                .toList();

        // Agora enviando os 6 parâmetros corretos exigidos pelo OrderResponse
        return new OrderResponse(
                order.getId(),
                order.getClientId(),
                order.getStatus(),
                order.getTotalAmount(),
                itemResponses, // Lista de itens convertida para Response
                order.getCreatedAt() // Enviando o createdAt que estava faltando!
        );
    }
}