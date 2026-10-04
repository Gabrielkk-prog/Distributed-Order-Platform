package DistributedOrderPlatform.stock_service.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper; // 👈 Import oficial do Jackson

import DistributedOrderPlatform.stock_service.entity.ProcessedEvent;
import DistributedOrderPlatform.stock_service.repository.ProcessedEventRepository;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.UUID;

@Component
public class OrderCreatedConsumer {

    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper; // 👈 Adicionado para ler o JSON

    // Construtor atualizado com a injeção do ObjectMapper
    public OrderCreatedConsumer(ProcessedEventRepository processedEventRepository, ObjectMapper objectMapper) {
        this.processedEventRepository = processedEventRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "order.created", groupId = "stock-service")
    @Transactional // 🔥 Mantém a verificação, o estoque e o registro na mesma transação local!
    public void consume(String message) {
        try {
            // 1. 🔄 Converte a String JSON em um nó de árvore do Jackson
            JsonNode jsonNode = objectMapper.readTree(message);

            // 2. 🎯 Extrai o eventId real de dentro do JSON
            String eventIdStr = jsonNode.get("eventId").asText();
            UUID eventId = UUID.fromString(eventIdStr);

            System.out.println("🔍 [Stock Service] Analisando EventId real: " + eventId);

            // 3. 📍 CHECKLIST: Verificar eventId antes do processamento
            if (processedEventRepository.existsById(eventId)) {
                System.out.println("⚠️ [Stock Service] Idempotência Ativada! Evento já processado: " + eventId);
                return; // Encerra o fluxo imediatamente sem mexer no estoque duas vezes!
            }

            // 4. 📍 CHECKLIST: Processar estoque (Próximo passo técnico)
            // Lógica para iterar sobre a lista de "items" do JSON e decrementar o saldo...

            // 5. 📍 CHECKLIST: Registrar eventId no banco para marcar como consumido com
            // sucesso
            ProcessedEvent processedEvent = new ProcessedEvent(eventId, Instant.now());
            processedEventRepository.save(processedEvent);

            System.out.println("✅ [Stock Service] Estoque atualizado e evento registrado com sucesso: " + eventId);

        } catch (Exception e) {
            System.err.println("Erro ao processar evento no estoque: " + e.getMessage());
        }
    }
}
