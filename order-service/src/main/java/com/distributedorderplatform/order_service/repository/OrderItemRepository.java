package com.distributedorderplatform.order_service.repository;

import com.distributedorderplatform.order_service.entity.OrderItem.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
