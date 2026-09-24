package com.distributedorderplatform.order_service.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.distributedorderplatform.order_service.entity.Order;

public interface OrderRepository
        extends JpaRepository<Order, UUID> {
}
