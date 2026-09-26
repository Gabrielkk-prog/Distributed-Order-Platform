package DistributedOrderPlatform.client_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@SpringBootApplication
public class ClientServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClientServiceApplication.class, args);
	}

	// --- ADICIONE ESTE BLOCO EXATAMENTE AQUI ---
	@Bean
	public RestClient.Builder restClientBuilder() {
		return RestClient.builder();
	}
}
