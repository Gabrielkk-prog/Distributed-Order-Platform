package com.distributedorderplatform.order_service.service;

import com.distributedorderplatform.order_service.dto.OrderRequest;
import com.distributedorderplatform.order_service.dto.OrderResponse;
import com.distributedorderplatform.order_service.entity.Order;
import com.distributedorderplatform.order_service.entity.OrderStatus;
import com.distributedorderplatform.order_service.entity.OutboxEvent;
import com.distributedorderplatform.order_service.entity.OutboxStatus;
import com.distributedorderplatform.order_service.repository.OrderRepository;
import com.distributedorderplatform.order_service.repository.OutboxEventRepository;

import com.distributedorderplatform.order_service.event.OrderCreatedEvent;
import com.distributedorderplatform.order_service.infrastruture.config.EventSerializer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxEventRepository outboxEventRepository; // 👈 Injetado novo repositório
    private final EventSerializer eventSerializer; // 👈 Injetado novo serializador

    // Construtor atualizado com as dependências do padrão Outbox
    public OrderService(OrderRepository orderRepository,
            OutboxEventRepository outboxEventRepository,
            EventSerializer eventSerializer) {
        this.orderRepository = orderRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.eventSerializer = eventSerializer;
    }

    @Transactional
    public OrderResponse create(OrderRequest request) {
        // 1. Calcula o valor total multiplicando a quantidade pelo preço de cada item
        BigDecimal totalCalculado = request.items().stream()
                .map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2. Instancia a entidade e popula com os dados validados
        Order order = new Order();
        order.setClientId(request.clientId());
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(totalCalculado);

        // 3. Salva o pedido no banco PostgreSQL
        Order savedOrder = orderRepository.save(order);

        // 4. Monta o Evento de Pedido Criado
        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID(),
                savedOrder.getId(),
                savedOrder.getClientId(),
                savedOrder.getTotalAmount(),
                request.items());

        // 5. 🔄 PADRÃO OUTBOX: Serializa o evento e grava na tabela do banco
        String payload = eventSerializer.serialize(event);

        OutboxEvent outboxEvent = new OutboxEvent(
                event.eventId(),
                savedOrder.getId(),
                "OrderCreated",
                payload,
                OutboxStatus.PENDING,
                Instant.now());

        outboxEventRepository.save(outboxEvent); // Grava na mesma transação do banco!

        // 6. Retorna o DTO passando uma lista vazia em vez de 'null'
        return new OrderResponse(
                savedOrder.getId(),
                savedOrder.getClientId(),
                savedOrder.getStatus().name(),
                savedOrder.getTotalAmount(),
                List.of());
    }

    @Transactional
    public void confirmOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

        order.setStatus(OrderStatus.APPROVED);
        orderRepository.save(order);
    }

    @Transactional
    public void cancelOrder(UUID orderId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }
}
