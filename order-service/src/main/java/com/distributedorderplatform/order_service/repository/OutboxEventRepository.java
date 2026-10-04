package com.distributedorderplatform.order_service.repository;

import com.distributedorderplatform.order_service.entity.OutboxEvent;
import com.distributedorderplatform.order_service.entity.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    // 📍 ADICIONE ESTA LINHA:
    List<OutboxEvent> findByStatus(OutboxStatus status);
}
