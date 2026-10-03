package DistributedOrderPlatform.stock_service.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import DistributedOrderPlatform.stock_service.entity.Product;

public interface ProductRepository
                extends JpaRepository<Product, UUID> {
}