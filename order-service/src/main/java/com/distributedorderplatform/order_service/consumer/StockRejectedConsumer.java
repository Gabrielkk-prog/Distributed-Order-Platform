package com.distributedorderplatform.order_service.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.distributedorderplatform.order_service.service.OrderService;

@Component
public class StockRejectedConsumer {

    private final OrderService orderService;

    public StockRejectedConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(topics = "stock.rejected", groupId = "order-service")
    public void consume(StockRejectedEvent event) {

        orderService.cancelOrder(
                event.orderId(),
                event.reason());
    }
}
