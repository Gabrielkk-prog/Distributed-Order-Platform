package com.distributedorderplatform.order_service.service;

import com.distributedorderplatform.order_service.dto.OrderRequest;
import com.distributedorderplatform.order_service.dto.OrderResponse;
import com.distributedorderplatform.order_service.entity.Order;
import com.distributedorderplatform.order_service.entity.OrderStatus;
import com.distributedorderplatform.order_service.repository.OrderRepository;
import com.distributedorderplatform.order_service.event.OrderCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.List; // 👈 Garanta que possui o import de List

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderService(OrderRepository orderRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.kafkaTemplate = kafkaTemplate;
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

        // 5. Publica a mensagem no tópico do Apache Kafka
        kafkaTemplate.send("order.created", event);

        // 6. 🚀 CORREÇÃO AQUI: Retorna o DTO passando uma lista vazia em vez de 'null'
        return new OrderResponse(
                savedOrder.getId(),
                savedOrder.getClientId(),
                savedOrder.getStatus().name(),
                savedOrder.getTotalAmount(),
                List.of() // 👈 MUDADO DE 'null' PARA 'List.of()' para o Jackson conseguir ler!
        );
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

        // 🚀 O ACERTO ESTÁ AQUI: Deve mudar para CANCELLED
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

}
