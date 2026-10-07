package DistributedOrderPlatform.stock_service.service;

import DistributedOrderPlatform.stock_service.event.OrderCreatedEvent;
import DistributedOrderPlatform.stock_service.event.OrderItemEvent;
import DistributedOrderPlatform.stock_service.entity.Product;
import DistributedOrderPlatform.stock_service.repository.StockRepository;
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
            Product product = stockRepository.findById(item.productId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Estoque não encontrado para produto: " + item.productId()));

            int quantidadeReservada = product.getReservedQuantity() != null ? product.getReservedQuantity() : 0;
            int quantidadeDisponivel = product.getQuantity() - quantidadeReservada;
            if (quantidadeDisponivel < item.quantity()) {
                throw new IllegalStateException(
                        "Estoque Insuficiente para produto: " + item.productId());
            }

            product.setReservedQuantity(quantidadeReservada + item.quantity());

            stockRepository.save(product);
        }
    }
}
