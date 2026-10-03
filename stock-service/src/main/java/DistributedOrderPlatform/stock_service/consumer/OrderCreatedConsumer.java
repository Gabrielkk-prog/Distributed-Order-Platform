package DistributedOrderPlatform.stock_service.consumer;

import DistributedOrderPlatform.stock_service.event.OrderCreatedEvent;
import DistributedOrderPlatform.stock_service.service.StockService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedConsumer {

    private final StockService stockService;

    public OrderCreatedConsumer(StockService stockService) {
        this.stockService = stockService;
    }

    @KafkaListener(topics = "order-created", groupId = "stock-service")
    public void consume(OrderCreatedEvent event) {
        // Envia o evento com sucesso para a camada de negócios dar baixa nas tabelas
        stockService.processOrder(event);
    }

}
