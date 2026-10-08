Especificações do Cluster Amazon EKS:
• Versão do K8s: Kubernetes v1.31
• Tipo de Instância: EC2 t3.medium (2 vCPUs, 4GB RAM) ou superior.
• Estratégia de Escalabilidade (Auto Scaling Group): Mínimo: 3 | Desejado: 3 | Máximo: 6.
• Distribuição: Multi-AZ (Nós espalhados por diferentes zonas de disponibilidade para tolerância a falhas).

Antes de aplicar os manifests Kubernetes, provisione o Secret `app-secret` fora do
repositório, usando uma senha forte e um gerenciador de segredos. Não versione valores
de senha no Git. No Docker Compose, defina `DB_PASSWORD` no ambiente antes de iniciar
os serviços.

## Arquitetura na AWS

```text
┌─────────────────────────────────────────┐
│                  AWS                    │
│                                         │
│  EKS  ───────── Kubernetes              │
│   │                                     │
│   ├── API Gateway                       │
│   ├── Order Service                     │
│   ├── Stock Service                     │
│   ├── Client Service                    │
│   └── Notification Service              │
│                                         │
│  RDS  ───────── PostgreSQL              │
│                                         │
│  MSK  ───────── Kafka                   │
│                                         │
│  ECR  ───────── Docker Images           │
│                                         │
│  CloudWatch ─── Logs/Métricas           │
└─────────────────────────────────────────┘
```