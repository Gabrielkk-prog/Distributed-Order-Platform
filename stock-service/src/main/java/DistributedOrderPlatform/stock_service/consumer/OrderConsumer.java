package DistributedOrderPlatform.stock_service.consumer;

import DistributedOrderPlatform.stock_service.event.OrderCreatedEvent;
import DistributedOrderPlatform.stock_service.event.OrderItemEvent;
import DistributedOrderPlatform.stock_service.event.StockRejectedEvent;
import DistributedOrderPlatform.stock_service.event.StockReservedEvent;
import DistributedOrderPlatform.stock_service.entity.Product;
import DistributedOrderPlatform.stock_service.entity.ProcessedEvent;
import DistributedOrderPlatform.stock_service.repository.StockRepository;
import DistributedOrderPlatform.stock_service.repository.ProcessedEventRepository;
import DistributedOrderPlatform.stock_service.event.producer.StockEventProducer;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

@Component
public class OrderConsumer {

    private final StockRepository stockRepository;
    private final StockEventProducer stockEventProducer;
    private final ProcessedEventRepository processedEventRepository;

    public OrderConsumer(StockRepository stockRepository,
            StockEventProducer stockEventProducer,
            ProcessedEventRepository processedEventRepository) {
        this.stockRepository = stockRepository;
        this.stockEventProducer = stockEventProducer;
        this.processedEventRepository = processedEventRepository;
    }

    @Transactional
    @KafkaListener(topics = "order-created", groupId = "stock-group")
    public void processOrder(OrderCreatedEvent event) {

        // Idempotency Guard Check
        if (processedEventRepository.existsById(event.eventId())) {
            System.out.println("Duplicate OrderCreatedEvent ignored in stock-service: " + event.eventId());
            return;
        }

        // Temporary list to hold validated products in memory
        List<Product> validatedProducts = new ArrayList<>();

        // 🚀 Phase 1: Validate ALL items first (No database writes yet)
        for (OrderItemEvent item : event.items()) {
            Product product = stockRepository.findById(item.productId()).orElse(null);

            if (product == null) {
                this.publishRejected(event, "Estoque não encontrado para produto: " + item.productId());
                return; // Safe abort
            }

            int available = product.getQuantity() - product.getReservedQuantity();

            if (available < item.quantity()) {
                this.publishRejected(event, "Estoque insuficiente para produto: " + item.productId());
                return; // Safe abort
            }

            // Add to temporary memory list if validation passes
            validatedProducts.add(product);
        }

        // 🚀 Phase 2: Update database ONLY if all items passed validation above
        for (int i = 0; i < event.items().size(); i++) {
            OrderItemEvent item = event.items().get(i);
            Product product = validatedProducts.get(i);

            product.setReservedQuantity(product.getReservedQuantity() + item.quantity());
            stockRepository.save(product);
        }

        // Save the event marker to prevent duplicates
        ProcessedEvent processedEvent = new ProcessedEvent(event.eventId(), Instant.now());
        processedEventRepository.save(processedEvent);

        this.publishReserved(event);
    } // 👈 Closes the processOrder method

    private void publishReserved(OrderCreatedEvent event) {
        StockReservedEvent reserved = new StockReservedEvent(
                UUID.randomUUID(),
                event.orderId(),
                Instant.now());

        System.out.println("Estoque reservado com sucesso para o pedido: " + event.orderId());
        stockEventProducer.publishReserved(reserved);
    }

    private void publishRejected(OrderCreatedEvent event, String reason) {
        StockRejectedEvent rejected = new StockRejectedEvent(
                UUID.randomUUID(),
                event.orderId(),
                reason,
                Instant.now());

        stockEventProducer.publishRejected(rejected);
    }
} // 👈 Closes the OrderConsumer class
