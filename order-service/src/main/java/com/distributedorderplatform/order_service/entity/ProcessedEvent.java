package com.distributedorderplatform.order_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "processed_events") // Nome da tabela que guardará o histórico de IDs processados
public class ProcessedEvent {

    @Id
    private UUID eventId; // ID do evento que vem do Kafka (Chave Primária)

    @Column(nullable = false)
    private Instant processedAt; // Momento em que o evento foi gravado

    // Construtor Padrão exigido pelo Hibernate
    public ProcessedEvent() {
    }

    // Construtor Customizado para facilitar a criação no seu Consumer
    public ProcessedEvent(UUID eventId, Instant processedAt) {
        this.eventId = eventId;
        this.processedAt = processedAt;
    }

    // Getters e Setters
    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Instant processedAt) {
        this.processedAt = processedAt;
    }
}
