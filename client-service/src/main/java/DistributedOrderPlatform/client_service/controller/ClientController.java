package DistributedOrderPlatform.client_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import DistributedOrderPlatform.client_service.dto.ClientRequest;
import DistributedOrderPlatform.client_service.dto.ClientResponse;
import DistributedOrderPlatform.client_service.service.ClientService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/clients")
public class ClientController {

    private final ClientService service;

    public ClientController(ClientService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ClientResponse> create(
            @Valid @RequestBody ClientRequest request) {

        ClientResponse response = service.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
