package DistributedOrderPlatform.stock_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "processed_events")
public class ProcessedEvent {

    @Id // 🚀 A CORREÇÃO ESTÁ AQUI: Define o identificador exigido pelo Hibernate!
    private UUID eventId;

    @Column(nullable = false)
    private Instant processedAt;

    // Construtor padrão exigido pelo Hibernate
    public ProcessedEvent() {
    }

    // Construtor completo para facilitar o uso na sua classe OrderConsumer
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
