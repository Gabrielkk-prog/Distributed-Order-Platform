package DistributedOrderPlatform.stock_service.consumer;

import DistributedOrderPlatform.stock_service.event.OrderCreatedEvent;
import DistributedOrderPlatform.stock_service.event.OrderItemEvent;
import DistributedOrderPlatform.stock_service.event.PublishReserved;
import DistributedOrderPlatform.stock_service.event.StockRejectedEvent; // Adicionado import
import DistributedOrderPlatform.stock_service.entity.Product;
import DistributedOrderPlatform.stock_service.repository.StockRepository;
import DistributedOrderPlatform.stock_service.event.producer.StockEventProducer; // Ajuste se o pacote do seu producer for diferente
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.UUID;

@Component
public class OrderConsumer {

    private final StockRepository stockRepository;
    private final StockEventProducer stockEventProducer; // 👈 1. DECLARADO O PRODUCER AQUI

    // 👈 2. CONSTRUTOR ATUALIZADO PARA INJETAR AMBOS
    public OrderConsumer(StockRepository stockRepository, StockEventProducer stockEventProducer) {
        this.stockRepository = stockRepository;
        this.stockEventProducer = stockEventProducer;
    }

    @Transactional
    @KafkaListener(topics = "order-created", groupId = "stock-group")
    public void processOrder(OrderCreatedEvent event) {

        for (OrderItemEvent item : event.items()) {
            Product product = stockRepository.findById(item.productId()).orElse(null);

            if (product == null) {
                this.publishRejected(event, "Estoque não encontrado para produto: " + item.productId());
                return;
            }

            int available = product.getQuantity() - product.getReservedQuantity();

            if (available < item.quantity()) {
                this.publishRejected(event, "Estoque insuficiente para produto: " + item.productId());
                return;
            }

            product.setReservedQuantity(product.getReservedQuantity() + item.quantity());
            stockRepository.save(product);
        }

        this.publishReserved(event);
    }

    private void publishReserved(OrderCreatedEvent event) {
        // 👈 CORRIGIDO: Passando os valores diretamente dentro do construtor
        PublishReserved reserved = new PublishReserved(
                UUID.randomUUID(),
                event.orderId(),
                Instant.now());

        System.out.println("Estoque reservado com sucesso para o pedido: " + event.orderId());
        // Se você quiser que o seu producer também envie o sucesso, descomente a linha
        // abaixo:
        // stockEventProducer.publishReserved(reserved);
    }

    private void publishRejected(OrderCreatedEvent event, String reason) {
        StockRejectedEvent rejected = new StockRejectedEvent(
                UUID.randomUUID(),
                event.orderId(),
                reason,
                Instant.now());

        // 👈 AGORA VAI FUNCIONAR: O stockEventProducer já foi reconhecido pelo Java!
        stockEventProducer.publishRejected(rejected);
    }
}
