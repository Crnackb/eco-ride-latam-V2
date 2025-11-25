# 📊 Diagramas de Arquitectura & Saga – ECO RIDE LATAM

> ⚠️ Nota: Todos los microservicios cargan su configuración desde **Config Server**, usando:
>
> ```
> spring.config.import=optional:configserver:http://localhost:8888
> ```
>
> Cada microservicio tiene su propio archivo en:
> `/config/<service-name>.yml` dentro del config-server.

---

## 🏗️ Arquitectura General (Microservicios + Infraestructura)

```mermaid
graph LR
  subgraph Cliente
    FE[Frontend / Postman]
  end

  FE --> GW[API Gateway<br/>Spring Cloud Gateway]

  GW --> KC[Keycloak<br/>Auth Server]
  KC --> GW

  GW --> TRIP[trip-service]
  GW --> PASS[passenger-service]
  GW --> PAY[payment-service]
  GW --> NOTI[notification-service]

  subgraph Infraestructura
    EUR[Eureka<br/>Service Discovery]
    KAFKA[(Kafka)]
    ZIP[Zipkin]
    PROM[Prometheus]
    LOKI[Loki]
    GRAF[Grafana]
    CONF[Config Server]
  end

  GW --> CONF
  TRIP --> CONF
  PASS --> CONF
  PAY --> CONF
  NOTI --> CONF

  TRIP --> EUR
  PASS --> EUR
  PAY --> EUR
  NOTI --> EUR
  GW --> EUR

  TRIP <--> KAFKA
  PAY <--> KAFKA
  NOTI <--> KAFKA

  TRIP --> PROM
  PASS --> PROM
  PAY --> PROM
  NOTI --> PROM
  GW --> PROM

  TRIP --> LOKI
  PASS --> LOKI
  PAY --> LOKI
  NOTI --> LOKI
  GW --> LOKI

  PROM --> GRAF
  LOKI --> GRAF
  ZIP --> GRAF
