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
```
---
## 🔁 Diagrama de Saga de Reserva (Mermaid)

```mermaid
sequenceDiagram
  actor U as Usuario
  participant FE as Frontend / Cliente
  participant GW as API Gateway
  participant TRIP as trip-service
  participant PAY as payment-service
  participant NOTI as notification-service
  participant KAFKA as Kafka

  U->>FE: Crear reserva (tripId, passengerId)
  FE->>GW: POST /trips/{tripId}/reservations
  GW->>TRIP: POST /trips/{tripId}/reservations

  TRIP->>TRIP: Validar asientos disponibles
  TRIP->>TRIP: Crear Reservation (PENDING)<br/>Descontar asiento
  TRIP->>KAFKA: publish ReservationRequested

  KAFKA-->>PAY: ReservationRequested
  PAY->>PAY: Crear PaymentIntent
  alt Pago autorizado
    PAY->>PAY: Crear Charge
    PAY->>KAFKA: publish PaymentAuthorized
    KAFKA-->>TRIP: PaymentAuthorized
    TRIP->>TRIP: Cambiar Reservation a CONFIRMED
    TRIP->>KAFKA: publish ReservationConfirmed
    KAFKA-->>NOTI: ReservationConfirmed
    NOTI->>U: Enviar notificación de confirmación
  else Pago rechazado / error
    PAY->>KAFKA: publish PaymentFailed
    KAFKA-->>TRIP: PaymentFailed
    TRIP->>TRIP: Cambiar Reservation a CANCELLED<br/>Devolver asiento al Trip
    TRIP->>KAFKA: publish ReservationCancelled
    KAFKA-->>NOTI: ReservationCancelled
    NOTI->>U: Enviar notificación de fallo
  end
```
---
🚀 Resumen de microservicios (vía Gateway)
| Microservicio            | Rutas principales                              |Puertos |
| ------------------------ | ---------------------------------------------- |--------|
| **trip-service**         | `/trips/**`, `/reservations/**`                |8082    |
| **passenger-service**    | `/passengers/**`, `/drivers/**`, `/ratings/**` |8083    |
| **payment-service**      | `/payments/**`, `/charges/**`, `/refunds/**`   |8084    |
| **notification-service** | `/notifications/**`                            |8085    |
| **Gateway actuator**     | `/actuator/**`                                 |8080    |
---
