package DistributedOrderPlatform.stock_service.event.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import DistributedOrderPlatform.stock_service.event.StockRejectedEvent;
import DistributedOrderPlatform.stock_service.event.StockReservedEvent;

@Component
public class StockEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public StockEventProducer(
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishReserved(StockReservedEvent event) {

        kafkaTemplate.send(
                "stock.reserved",
                event.orderId().toString(),
                event);
    }

    public void publishRejected(StockRejectedEvent event) {

        kafkaTemplate.send(
                "stock.rejected",
                event.orderId().toString(),
                event);
    }
}