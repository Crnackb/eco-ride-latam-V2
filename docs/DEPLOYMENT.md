# 🚢 Guía de Despliegue - Eco Ride Latam V2

Esta guía explica cómo desplegar la arquitectura de microservicios utilizando Docker y Docker Compose.

## 📦 Estructura de Despliegue

La configuración principal de despliegue se encuentra en la carpeta `deploy/`.

```text
deploy/
├── docker-compose.yml   # Orquestación de todos los servicios
├── infra/               # Configuraciones de infraestructura (Kafka, Postgres, etc.)
└── keycloak/            # Configuración de realms y usuarios
```

## 🐳 Docker Compose

El archivo `docker-compose.yml` define los siguientes servicios:

### Servicios de Infraestructura
*   **postgres**: Base de datos principal.
*   **kafka** & **zookeeper**: Bus de mensajería para eventos asíncronos.
*   **zipkin**: Trazabilidad distribuida.
*   **keycloak**: Servidor de identidad y acceso.

### Microservicios
*   **config-server**: Servidor de configuración centralizada.
*   **discovery-server**: Eureka Service Registry.
*   **api-gateway**: Puerta de enlace.
*   **trip-service**, **passenger-service**, **payment-service**, **notification-service**.

## 🚀 Despliegue Local

Para iniciar todo el entorno:

1.  Navega a la carpeta de despliegue:
    ```bash
    cd deploy
    ```

2.  Construye las imágenes (si es necesario) y levanta los contenedores:
    ```bash
    docker-compose up -d --build
    ```

3.  Verifica el estado de los contenedores:
    ```bash
    docker-compose ps
    ```

## 🛑 Detener el Entorno

Para detener y eliminar los contenedores:

```bash
docker-compose down
```

Para detener y eliminar también los volúmenes (datos persistentes):

```bash
docker-compose down -v
```

## 🔧 Variables de Entorno

Los microservicios están configurados para leer variables de entorno que pueden sobreescribir la configuración predeterminada. Las más importantes son:

| Variable | Descripción | Valor por defecto (Docker) |
| :--- | :--- | :--- |
| `SPRING_PROFILES_ACTIVE` | Perfil de Spring (dev, prod, docker) | `docker` |
| `CONFIG_SERVER_URI` | URL del Config Server | `http://config-server:8888` |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | URL de Eureka | `http://discovery-server:8761/eureka/` |
| `DATASOURCE_URL` | URL de conexión a BD | `jdbc:postgresql://postgres:5432/...` |
| `KAFKA_BOOTSTRAP_SERVERS` | Servidores Kafka | `kafka:9092` |

## ☁️ Consideraciones para Producción

*   **Base de Datos**: En producción, utiliza una instancia gestionada de PostgreSQL (ej. AWS RDS, Google Cloud SQL) en lugar del contenedor.
*   **Kafka**: Utiliza un clúster de Kafka gestionado (ej. Confluent Cloud, MSK).
*   **Secretos**: No guardes contraseñas en el repositorio. Usa Docker Secrets o un gestor de secretos (Vault, AWS Secrets Manager) e inyéctalos como variables de entorno.
*   **Escalabilidad**: Docker Compose es ideal para desarrollo y pruebas. Para producción, considera usar **Kubernetes (K8s)**.
