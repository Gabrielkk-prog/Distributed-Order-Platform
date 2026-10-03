package DistributedOrderPlatform.stock_service.repository;

import DistributedOrderPlatform.stock_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockRepository extends JpaRepository<Product, UUID> {
    // Caso o item.productId() no seu evento venha como String, mude aqui para
    // String id
    // Se o seu evento já expõe como UUID, mantenha UUID
    Optional<Product> findById(UUID id);
}
