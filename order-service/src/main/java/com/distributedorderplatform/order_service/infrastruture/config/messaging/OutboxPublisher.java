package com.distributedorderplatform.order_service.infrastruture.config.messaging;

import com.distributedorderplatform.order_service.entity.OutboxEvent;
import com.distributedorderplatform.order_service.entity.OutboxStatus;
import com.distributedorderplatform.order_service.repository.OutboxEventRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OutboxPublisher(OutboxEventRepository outboxEventRepository,
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 5000)
    @Transactional // Garante que a operação de salvar o status no banco seja segura
    public void publishPendingEvents() {
        // 1. Busca todos os eventos que ainda estão PENDING no banco PostgreSQL
        List<OutboxEvent> pendingEvents = outboxEventRepository.findByStatus(OutboxStatus.PENDING);

        for (OutboxEvent event : pendingEvents) {
            if ("OrderCreated".equals(event.getEventType())) {

                // 2. Envia para o Kafka de forma assíncrona usando a chave do agregado e o
                // payload JSON
                kafkaTemplate
                        .send(
                                "order.created",
                                event.getAggregateId().toString(),
                                event.getPayload())
                        .whenComplete((result, exception) -> {
                            // 3. Callback executado quando o Kafka responde
                            if (exception == null) {
                                // Sucesso: atualiza o status e a data de publicação
                                event.setStatus(OutboxStatus.PUBLISHED);
                                event.setPublishedAt(Instant.now());

                                outboxEventRepository.save(event);
                            } else {
                                // Falha: printa o erro e deixa o evento como PENDING para tentar no próximo
                                // ciclo
                                System.err.println("Erro ao enviar evento outbox [" + event.getId() + "] para o Kafka: "
                                        + exception.getMessage());
                            }
                        });
            }
        }
    }
}
