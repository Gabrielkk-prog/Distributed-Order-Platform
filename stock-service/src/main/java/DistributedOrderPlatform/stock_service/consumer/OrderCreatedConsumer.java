package DistributedOrderPlatform.stock_service.consumer;

import DistributedOrderPlatform.stock_service.entity.ProcessedEvent;
import DistributedOrderPlatform.stock_service.event.OrderCreatedEvent;
import DistributedOrderPlatform.stock_service.repository.ProcessedEventRepository;
import DistributedOrderPlatform.stock_service.service.StockService; // Importado o serviço

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
public class OrderCreatedConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderCreatedConsumer.class);

    private final ProcessedEventRepository processedEventRepository;
    private final StockService stockService; // Injetado o seu StockService real

    public OrderCreatedConsumer(ProcessedEventRepository processedEventRepository, StockService stockService) {
        this.processedEventRepository = processedEventRepository;
        this.stockService = stockService;
    }

    @KafkaListener(topics = "order.created", groupId = "stock-service")
    @Transactional
    public void consume(OrderCreatedEvent event) {
        log.info("OrderCreated recebido. eventId={}, orderId={}", event.eventId(), event.orderId());

        if (processedEventRepository.existsById(event.eventId())) {
            log.warn("Evento já processado: {}", event.eventId());
            return;
        }

        stockService.processOrder(event);
        processedEventRepository.save(new ProcessedEvent(event.eventId(), Instant.now()));

        log.info("Estoque reservado com sucesso no banco para o orderId={}", event.orderId());
    }
}
