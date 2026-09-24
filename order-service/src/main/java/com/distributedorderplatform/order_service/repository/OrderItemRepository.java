package com.distributedorderplatform.order_service.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.distributedorderplatform.order_service.entity.OrderItem.OrderItem;

public interface OrderItemRepository
        extends JpaRepository<OrderItem, UUID> {
}
