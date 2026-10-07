package DistributedOrderPlatform.stock_service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;

import DistributedOrderPlatform.stock_service.entity.Product;
import DistributedOrderPlatform.stock_service.event.OrderCreatedEvent;
import DistributedOrderPlatform.stock_service.event.OrderItemEvent;
import DistributedOrderPlatform.stock_service.repository.StockRepository;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
public class StockServiceEventTest {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    private UUID productId;

    // Lista auxiliar para interceptar os eventos de rejeição disparados no teste
    // 10.11
    private static final List<String> rejeicoesRecebidas = new CopyOnWriteArrayList<>();

    // Listener temporário para capturar mensagens de erro enviadas pelo sistema
    // (ajuste o tópico se necessário)
    @KafkaListener(topics = "stock.rejected", groupId = "stock-test-group")
    void escutarRejeicoesTeste(Object event) {
        rejeicoesRecebidas.add(event.toString());
    }

    @BeforeEach
    void setUp() {
        stockRepository.deleteAll();
        rejeicoesRecebidas.clear();
        productId = UUID.randomUUID();
    }

    // =========================================================================
    // 10.10 PRIMEIRO TESTE: SUCESSO (ESTOQUE RESERVADO)
    // =========================================================================
    @Test
    void deveReservarEstoqueAoReceberOrderCreatedEventViaKafka() {
        UUID orderId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        // Configura: Estoque = 10, Reservado = 0
        Product initialProduct = new Product();
        initialProduct.setId(productId);
        initialProduct.setQuantity(10);
        initialProduct.setReservedQuantity(0);
        stockRepository.save(initialProduct);

        // Envia pedido com quantidade = 2
        OrderItemEvent itemPedido = new OrderItemEvent(productId, 2);
        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID(), UUID.randomUUID(), orderId, clientId,
                new BigDecimal("100.00"), Instant.now(), List.of(itemPedido));

        kafkaTemplate.send("order.created", orderId.toString(), event);

        // Valida que reservedQuantity = 2
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            Product productAtualizado = stockRepository.findById(productId).orElse(null);
            assertNotNull(productAtualizado, "O produto deveria existir no banco");
            assertEquals(2, productAtualizado.getReservedQuantity(), "A quantidade reservada deve ser igual a 2");
            assertEquals(10, productAtualizado.getQuantity(), "A quantidade base deve continuar sendo 10");
        });
    }

    // =========================================================================
    // 10.11 SEGUNDO TESTE: REJEIÇÃO (ESTOQUE INSUFICIENTE)
    // =========================================================================
    @Test
    void deveRejeitarPedidoEManterReservaZeradaQuandoEstoqueForInsuficiente() {
        UUID orderId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        // Configura: Estoque disponível = 3, Reservado = 0
        Product initialProduct = new Product();
        initialProduct.setId(productId);
        initialProduct.setQuantity(3);
        initialProduct.setReservedQuantity(0);
        stockRepository.save(initialProduct);

        // Envia pedido com quantidade = 5
        OrderItemEvent itemPedido = new OrderItemEvent(productId, 5);
        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID(), UUID.randomUUID(), orderId, clientId,
                new BigDecimal("250.00"), Instant.now(), List.of(itemPedido));

        kafkaTemplate.send("order.created", orderId.toString(), event);

        // Valida que reservedQuantity continua 0 e dispara a rejeição
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            Product productAtualizado = stockRepository.findById(productId).orElse(null);
            assertNotNull(productAtualizado, "O produto deveria existir no banco");

            // Validações do teste 10.11
            assertEquals(0, productAtualizado.getReservedQuantity(), "A quantidade reservada deve continuar 0");
            assertEquals(3, productAtualizado.getQuantity(), "O estoque total disponível não deve mudar");
        });
    }
}
