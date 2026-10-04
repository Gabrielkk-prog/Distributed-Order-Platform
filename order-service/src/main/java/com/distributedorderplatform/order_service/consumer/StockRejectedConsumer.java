package com.distributedorderplatform.order_service.consumer;

import com.distributedorderplatform.order_service.event.StockRejectedEvent;
import com.distributedorderplatform.order_service.repository.ProcessedEventRepository;
import com.distributedorderplatform.order_service.service.OrderService;
import com.distributedorderplatform.order_service.entity.ProcessedEvent; // 👈 1. IMPORT THE ENTITY
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.time.Instant;

@Component
public class StockRejectedConsumer {

    private final OrderService orderService;
    private final ProcessedEventRepository processedEventRepository;

    // 👈 2. FIX: Constructor now injects BOTH properties to avoid compilation
    // errors
    public StockRejectedConsumer(OrderService orderService, ProcessedEventRepository processedEventRepository) {
        this.orderService = orderService;
        this.processedEventRepository = processedEventRepository;
    }

    @KafkaListener(topics = "stock.rejected", groupId = "order-service")
    public void consume(StockRejectedEvent event) {

        // 🚀 3. FIX: Check if this rejection event has already been handled
        if (processedEventRepository.existsById(event.id())) {
            System.out.println("Duplicate cancellation event detected! Skipping event: " + event.id());
            return; // Breaks out safely before touching database states
        }

        System.out.println("Estoque rejeitado recebido para o pedido: " + event.orderId());

        // Execute compensation logic
        orderService.cancelOrder(event.orderId(), event.reason());

        // 🚀 4. FIX: Store the handled event ID into the PostgreSQL tracking table
        ProcessedEvent processedEvent = new ProcessedEvent(event.id(), Instant.now());
        processedEventRepository.save(processedEvent);
    }
}
