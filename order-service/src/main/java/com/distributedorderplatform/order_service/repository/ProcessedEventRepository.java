package com.distributedorderplatform.order_service.repository;

import com.distributedorderplatform.order_service.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository // 🚀 Tells Spring Boot to manage this interface as a data access bean
public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, UUID> {
    // Inherits standard CRUD methods like .save(), .existsById(), and .findById()
}
