package DistributedOrderPlatform.stock_service.service; // Corrigido para o seu pacote real

import DistributedOrderPlatform.stock_service.event.OrderCreatedEvent; // Import real
import DistributedOrderPlatform.stock_service.event.OrderItemEvent; // Import real
import DistributedOrderPlatform.stock_service.entity.Product; // Import real
import DistributedOrderPlatform.stock_service.repository.StockRepository; // Import real
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockService {

    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Transactional
    public void processOrder(OrderCreatedEvent event) {
        for (OrderItemEvent item : event.items()) {
            // Busca o produto pelo Id (UUID) correto
            Product product = stockRepository.findById(item.productId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Estoque não encontrado para produto: " + item.productId()));

            // Seus records usam apenas 'quantity' (não existe getreservedquantity)
            int available = product.getQuantity() - item.quantity();

            if (available < 0) {
                throw new IllegalStateException(
                        "Estoque Insuficiente para produto: " + item.productId());
            }

            product.setQuantity(available);
            stockRepository.save(product);
        }
    }
}
