# Distributed Order Platform

Plataforma de pedidos distribuída desenvolvida com **Java e Spring Boot**, com foco em arquitetura de microsserviços, comunicação assíncrona, mensageria, consistência distribuída e containerização.

O projeto simula o processamento de pedidos em uma arquitetura próxima de um cenário real de sistemas distribuídos.

## Arquitetura

```text
                         CLIENT
                           |
                           | HTTP
                           v
                    +-------------+
                    | API Gateway |
                    +-------------+
                           |
          +----------------+----------------+
          |                |                |
          v                v                v
   +-------------+  +-------------+  +-------------+
   |   Client    |  |    Order    |  |    Stock    |
   |   Service   |  |   Service   |  |   Service   |
   +-------------+  +-------------+  +-------------+
                           |
                           | Kafka
                           v
                    +-------------+
                    |    Kafka    |
                    +-------------+
                       |       |
                       v       v
                Stock Service   Notification
                                Service
```

Cada microsserviço possui sua própria responsabilidade e seu próprio banco de dados.

```text
client-service  → client_db
order-service   → order_db
stock-service   → stock_db
```

Nenhum serviço acessa diretamente o banco de dados de outro serviço.

---

## Fluxo de um pedido

O fluxo principal da aplicação funciona da seguinte forma:

```text
1. Cliente envia um pedido
        ↓
2. API Gateway recebe a requisição
        ↓
3. Order Service cria o pedido
        ↓
4. Pedido recebe status STOCK_PENDING
        ↓
5. Evento OrderCreated é armazenado no Outbox
        ↓
6. Outbox Publisher publica o evento no Kafka
        ↓
7. Stock Service recebe o evento
        ↓
8. Estoque é validado e reservado
        ↓
9. Stock Service publica:
        ├── StockReserved
        └── StockRejected
        ↓
10. Order Service recebe o resultado
        ↓
11. Pedido é:
        ├── CONFIRMED
        └── CANCELLED
        ↓
12. Notification Service recebe o evento
```

O objetivo é demonstrar como um fluxo de negócio pode ser dividido entre diferentes serviços sem depender de uma única aplicação monolítica.

---

## Principais conceitos implementados

### Microsserviços

O sistema é dividido em serviços independentes:

- `api-gateway`
- `client-service`
- `order-service`
- `stock-service`
- `notification-service`

Cada serviço possui uma responsabilidade específica.

### Comunicação síncrona

REST é utilizado quando uma resposta imediata é necessária.

Exemplo:

```text
Order Service
      |
      | REST
      v
Client Service
```

Antes de criar um pedido, o `Order Service` pode validar a existência do cliente através de uma chamada HTTP.

### Comunicação assíncrona

Kafka é utilizado para comunicação orientada a eventos.

Exemplo:

```text
Order Service
      |
      | OrderCreated
      v
    Kafka
      |
      +-------------> Stock Service
      |
      +-------------> Notification Service
```

Isso reduz o acoplamento entre os serviços.

---

## Event-Driven Architecture

Os principais eventos utilizados são:

```text
OrderCreated
StockReserved
StockRejected
OrderConfirmed
OrderCancelled
```

Os eventos representam mudanças importantes no fluxo de negócio e permitem que outros serviços reajam sem depender diretamente da implementação interna de outro serviço.

---

## Outbox Pattern

O projeto utiliza o **Outbox Pattern** para evitar o problema de inconsistência entre banco de dados e Kafka.

Em vez de fazer:

```text
Salvar pedido
      ↓
Publicar Kafka
```

o `Order Service` realiza:

```text
┌─────────────────────────────┐
│ Transação do banco          │
│                             │
│  Order                      │
│  OutboxEvent                │
└─────────────────────────────┘
             ↓
        commit
             ↓
     Outbox Publisher
             ↓
           Kafka
```

Dessa forma, o evento não é perdido caso o serviço falhe depois de salvar o pedido.

---

## Idempotência

Como sistemas distribuídos podem receber uma mesma mensagem mais de uma vez, os consumidores possuem controle de eventos processados.

```text
Kafka
  ↓
Evento recebido
  ↓
eventId já processado?
  ├── SIM → ignorar
  └── NÃO
        ↓
   processar evento
        ↓
   registrar eventId
```

Isso evita operações duplicadas, como reservar o mesmo estoque duas vezes.

---

## Bancos de dados

Cada serviço possui seu próprio banco lógico:

```text
Client Service
      ↓
  client_db

Order Service
      ↓
  order_db

Stock Service
      ↓
  stock_db
```

Não existem relacionamentos JPA entre entidades pertencentes a serviços diferentes.

As referências entre serviços utilizam identificadores, principalmente UUIDs.

---

## Docker

Todos os serviços podem ser executados em containers.

```text
Docker
│
├── api-gateway
├── client-service
├── order-service
├── stock-service
├── notification-service
├── PostgreSQL
└── Kafka
```

A comunicação interna utiliza os nomes dos serviços/container em vez de `localhost`.

Exemplo:

```text
jdbc:postgresql://postgres:5432/order_db
```

e:

```text
kafka:9092
```

---

## Kubernetes

O projeto também possui preparação para execução em Kubernetes.

A estrutura planejada é:

```text
k8s/
├── namespace.yaml
├── api-gateway.yaml
├── client-service.yaml
├── order-service.yaml
├── stock-service.yaml
├── notification-service.yaml
├── postgres.yaml
└── kafka.yaml
```

São utilizados conceitos como:

- Deployments
- Services
- ConfigMaps
- Secrets
- Health Checks
- Liveness Probes
- Readiness Probes
- Replicas

---

## AWS

A arquitetura também foi projetada pensando em uma possível execução na AWS.

Mapeamento conceitual:

```text
Docker
   ↓
ECR
   ↓
EKS
   ↓
Kubernetes

PostgreSQL → RDS

Kafka → MSK

Logs/Métricas → CloudWatch
```

A implantação em AWS não é necessária para executar o projeto localmente.

---

## Tecnologias

### Backend

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- Spring Kafka
- Spring Cloud Gateway
- Bean Validation
- Spring Actuator

### Banco de dados

- PostgreSQL
- Flyway

### Mensageria

- Apache Kafka

### Infraestrutura

- Docker
- Docker Compose
- Kubernetes
- AWS

### Testes

- JUnit
- Spring Boot Test
- Testcontainers

---

## Estrutura do projeto

```text
distributed-order-platform/
│
├── api-gateway/
├── client-service/
├── order-service/
├── stock-service/
├── notification-service/
│
├── infrastructure/
│   ├── docker/
│   └── kafka/
│
├── k8s/
│
├── docs/
│   ├── architecture.md
│   ├── domain.md
│   └── events.md
│
├── docker-compose.yml
├── .gitignore
└── README.md
```

---

## Objetivo do projeto

Este projeto foi desenvolvido como estudo prático de **arquitetura de sistemas distribuídos e microsserviços**.

O objetivo principal não é apenas criar uma API de pedidos, mas demonstrar conceitos utilizados em sistemas distribuídos reais:

- Separação de responsabilidades
- Microsserviços
- Comunicação REST
- Comunicação assíncrona
- Event-Driven Architecture
- Apache Kafka
- Idempotência
- Outbox Pattern
- Bancos de dados por serviço
- Docker
- Kubernetes
- Observabilidade
- Preparação para AWS

O projeto representa uma evolução arquitetural em relação a uma aplicação monolítica, explorando os problemas e soluções envolvidos na construção de sistemas distribuídos.

---

## Status

🚧 **Projeto em desenvolvimento**

Principais etapas implementadas:

- [x] Arquitetura dos microsserviços
- [x] Client Service
- [x] Order Service
- [x] Stock Service
- [x] Notification Service
- [x] API Gateway
- [x] PostgreSQL
- [x] Kafka
- [x] Event-Driven Architecture
- [x] Idempotência
- [x] Outbox Pattern
- [x] Observabilidade
- [x] Docker
- [x] Preparação para Kubernetes
- [x] Preparação para AWS

---

## Autor

**Gabriel**

GitHub:  
https://github.com/Gabrielkk-prog
