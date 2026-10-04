package com.distributedorderplatform.order_service.consumer;

import com.distributedorderplatform.order_service.event.StockReservedEvent;
import com.distributedorderplatform.order_service.service.OrderService;
import com.distributedorderplatform.order_service.repository.ProcessedEventRepository; // 👈 1. IMPORT THE REPOSITORY
import com.distributedorderplatform.order_service.entity.ProcessedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.time.Instant;

@Component
public class StockReservedConsumer {

    private final OrderService orderService;
    private final ProcessedEventRepository processedEventRepository; // 👈 2. DECLARE THE REPOSITORY

    // 3. INJECT IT IN THE CONSTRUCTOR
    public StockReservedConsumer(OrderService orderService, ProcessedEventRepository processedEventRepository) {
        this.orderService = orderService;
        this.processedEventRepository = processedEventRepository;
    }

    @KafkaListener(topics = "stock.reserved", groupId = "order-service")
    public void consume(StockReservedEvent event) {

        // 🚀 4. THE CRITICAL CHECK GOES HERE (Right at the start!)
        if (processedEventRepository.existsById(event.id())) {
            System.out.println("Duplicate message detected! Skipping event: " + event.id());
            return; // Stops execution immediately if the event ID has already been processed
        }

        System.out.println("Processing stock confirmation for order: " + event.orderId());

        // Execute business logic
        orderService.confirmOrder(event.orderId());

        // 5. SAVE THE EVENT ID AFTER PROCESSING SO IT IS MARKED AS HANDLED
        ProcessedEvent processedEvent = new ProcessedEvent(event.id(), Instant.now());
        processedEventRepository.save(processedEvent);
    }
}
