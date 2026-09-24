package DistributedOrderPlatform.client_service.gateway; // Ajuste para o pacote do seu serviço atual

import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
public class ClientServiceClient {

    private final RestClient restClient;

    // O Spring injeta o Builder automaticamente
    public ClientServiceClient(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://localhost:8081") // URL base de onde o seu client_service está rodando
                .build();
    }

    public boolean exists(UUID clientId) {
        try {
            restClient.get()
                    .uri("/clients/{id}", clientId) // Certifique-se de que a rota mapeada no Controller é exatamente
                                                    // essa
                    .retrieve()
                    .toBodilessEntity(); // Executa a requisição ignorando o corpo da resposta (só queremos o status
                                         // HTTP)

            return true; // Se retornar 200 OK, o cliente existe
        } catch (HttpClientErrorException.NotFound e) {
            return false; // Se retornar 404 (ResourceNotFoundException), o cliente não existe
        } catch (Exception e) {
            // Opcional: tratar outros erros (ex: 500 ou queda de conexão)
            return false;
        }
    }
}
