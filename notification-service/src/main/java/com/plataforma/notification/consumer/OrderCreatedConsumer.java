package com.plataforma.notification.consumer;

import com.plataforma.notification.dto.OrderCreatedEvent; // Import correto da sua pasta dto
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedConsumer {

        @KafkaListener(topics = "order.created", groupId = "notification-service")
        public void consume(OrderCreatedEvent event) {

                System.out.println(
                                "Pedido recebido: " + event.orderId());

                System.out.println(
                                "Enviando notificação para clientId: "
                                                + event.clientId());
        }
}