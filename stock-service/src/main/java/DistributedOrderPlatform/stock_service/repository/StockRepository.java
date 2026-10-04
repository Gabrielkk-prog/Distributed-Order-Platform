package DistributedOrderPlatform.stock_service.repository;

import DistributedOrderPlatform.stock_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import java.math.BigDecimal;

public interface StockRepository extends JpaRepository<Product, UUID> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO products (id, name, price, quantity, reserved_quantity, created_at) " +
            "VALUES (:id, :name, :price, :quantity, :reservedQuantity, NOW()) " +
            "ON CONFLICT (id) DO UPDATE SET " +
            "quantity = :quantity, reserved_quantity = :reservedQuantity, name = :name, price = :price", nativeQuery = true)
    void forceUpsert(
            @Param("id") UUID id,
            @Param("name") String name,
            @Param("price") BigDecimal price,
            @Param("quantity") Integer quantity,
            @Param("reservedQuantity") Integer reservedQuantity);
}
