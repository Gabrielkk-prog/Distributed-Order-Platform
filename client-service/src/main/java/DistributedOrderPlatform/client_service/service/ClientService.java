package DistributedOrderPlatform.client_service.service;

import java.time.Instant;
import java.util.UUID;

import DistributedOrderPlatform.client_service.entity.Client;
import DistributedOrderPlatform.client_service.exception.ResourceNotFoundException;
import DistributedOrderPlatform.client_service.repository.client_repository;
import DistributedOrderPlatform.client_service.dto.ClientRequest;
import DistributedOrderPlatform.client_service.dto.ClientResponse;

import org.springframework.stereotype.Service;

@Service
public class ClientService {

    private final client_repository repository;

    // Construtor corrigido para injeção de dependência
    public ClientService(client_repository repository) {
        this.repository = repository;
    }

    public ClientResponse findById(UUID id) {

        Client client = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Client not found"));

        return new ClientResponse(
                client.getId(),
                client.getName(),
                client.getEmail(),
                client.getCreatedAt());
    }

    public ClientResponse create(ClientRequest request) {
        // Exemplo de verificação se o cliente já existe (caso tenha implementado no
        // repositório)
        // if (repository.existsByEmail(request.email())) { ... }

        Client client = new Client();
        client.setName(request.name());
        client.setEmail(request.email());

        // A LINHA DE DEFINIR DATA FOI REMOVIDA DAQUI
        // O @PrePersist que você colocou na entidade cuida disso automaticamente ao
        // salvar.

        Client saved = repository.save(client);

        return new ClientResponse(
                saved.getId(),
                saved.getName(),
                saved.getEmail(),
                saved.getCreatedAt());
    }
}
