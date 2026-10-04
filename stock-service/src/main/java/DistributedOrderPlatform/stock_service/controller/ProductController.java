package DistributedOrderPlatform.stock_service.controller;

import DistributedOrderPlatform.stock_service.entity.Product;
import DistributedOrderPlatform.stock_service.repository.StockRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final StockRepository stockRepository;

    public ProductController(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @PostMapping
    public ResponseEntity<String> createProduct(@RequestBody Product product) {
        // 🚀 Bypasses Hibernate session state checks atomically!
        stockRepository.forceUpsert(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getQuantity(),
                product.getReservedQuantity());

        return ResponseEntity.ok("Massa de teste carregada com sucesso via Native Upsert!");
    }
}
