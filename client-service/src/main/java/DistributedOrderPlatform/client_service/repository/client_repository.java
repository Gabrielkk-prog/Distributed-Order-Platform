package DistributedOrderPlatform.client_service.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import DistributedOrderPlatform.client_service.entity.Client;

// O primeiro parâmetro DEVE ser Client (sua entidade) e o segundo o tipo do ID (UUID)
public interface client_repository extends JpaRepository<Client, UUID> {

  // Seus métodos de busca

  Optional<Client> findByEmail(String email);

  boolean existsByEmail(String email);
}
