package com.distributedorderplatform.order_service.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
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
    private final KafkaTemplate<String, OrderResponse> kafkaTemplate;

    public OrderService(OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            KafkaTemplate<String, OrderResponse> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.kafkaTemplate = kafkaTemplate;
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
                    orderItem.setOrderId(savedOrder.getId());
                    orderItem.setProductId(item.productId());
                    orderItem.setQuantity(item.quantity());
                    orderItem.setUnitPrice(item.unitPrice());

                    return orderItem;
                })
                .toList();

        // 1. Grava todos os itens no banco de dados Postgres
        orderItemRepository.saveAll(orderItems);

        // 2. Transforma os dados gravados no objeto de resposta (OrderResponse)
        OrderResponse response = toResponse(savedOrder, orderItems);

        // 3. MENSAGERIA: Envia os dados do pedido criado para o tópico do Apache Kafka
        // Nota: Altere "order-events-topic" para "order.created" se quiser que bata
        // exatamente com o listener que configuramos no seu notification-service!
        kafkaTemplate.send("order.created", response.id().toString(), response);

        // 4. Retorna a resposta final para o Controller (e para o Insomnia)
        return response;
    }

    // Calcula matematicamente o valor total com base na quantidade e preço unitário
    private BigDecimal calculateTotal(List<OrderItemRequest> items) {
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return items.stream()
                .map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Converte a entidade salva no banco de dados para o DTO de resposta limpo
    private OrderResponse toResponse(Order order, List<OrderItem> orderItems) {
        List<OrderItemResponse> itemResponses = orderItems.stream()
                .map(item -> new OrderItemResponse(
                        item.getProductId(),
                        item.getQuantity(),
                        item.getUnitPrice()))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getClientId(),
                order.getStatus().toString(), // Convertido para String para bater com o Record
                order.getTotalAmount(),
                itemResponses);
    }
}
