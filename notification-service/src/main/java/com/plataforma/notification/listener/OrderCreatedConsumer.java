package com.plataforma.notification.listener;

// SE O SEU ARQUIVO OrderCreatedEvent ESTIVER EM OUTRA PASTA (EX: MODEL), O IMPORT VAI APARECER AQUI AUTOMATICAMENTE
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedConsumer {

    @KafkaListener(topics = "order.created", groupId = "notification-service", containerFactory = "kafkaListenerContainerFactory" // Adicionado
                                                                                                                                  // conforme
                                                                                                                                  // pedido
                                                                                                                                  // pelo
                                                                                                                                  // projeto
    )
    public void consume(OrderCreatedEvent event) {

        System.out.println(
                "Pedido recebido: " + event.orderId());

        System.out.println(
                "Enviando notificação para clientId: "
                        + event.clientId());
    }
}
