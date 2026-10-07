package DistributedOrderPlatform.api_gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Component
public class CustomGlobalFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 🟢 A LINHA QUE VOCÊ MENCIONOU É EXECUTADA AQUI PARA CADA REQUISIÇÃO HTTP:
        UUID correlationId = UUID.randomUUID();

        // Injeta o ID criado no cabeçalho da requisição que vai para os microsserviços
        ServerWebExchange updatedExchange = exchange.mutate()
                .request(r -> r.header("X-Correlation-ID", correlationId.toString()))
                .build();

        return chain.filter(updatedExchange);
    }
}
