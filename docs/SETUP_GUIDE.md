# 🛠️ Guía de Configuración e Instalación - Eco Ride Latam V2

Esta guía detalla los pasos necesarios para configurar, construir y ejecutar el proyecto **Eco Ride Latam V2** en tu entorno local.

## 📋 Prerrequisitos

Asegúrate de tener instaladas las siguientes herramientas:

*   **Java 17** (JDK)
*   **Maven 3.8+**
*   **Docker** y **Docker Compose**
*   **Git**

## ⚙️ Configuración del Entorno

### 1. Clonar el repositorio

```bash
git clone <URL_DEL_REPOSITORIO>
cd eco-ride-latam-V2
```

### 2. Configuración de Base de Datos y Mensajería

El proyecto utiliza **PostgreSQL** y **Kafka**. La forma más sencilla de iniciarlos es usando Docker Compose.

Navega a la carpeta `deploy` (o donde se encuentre tu `docker-compose.yml` principal) y ejecuta:

```bash
cd deploy
docker-compose up -d
```

Esto levantará:
*   PostgreSQL (Puerto 5432)
*   Kafka (Puerto 9092)
*   Zookeeper (Puerto 2181)
*   Zipkin (Puerto 9411)
*   Prometheus/Grafana/Loki (si están configurados)

### 3. Configuración de Keycloak (Seguridad)

El servicio de autenticación requiere Keycloak.
1.  Asegúrate de que el contenedor de Keycloak esté corriendo (vía docker-compose).
2.  Accede a la consola de administración (usualmente `http://localhost:8080` o el puerto configurado).
3.  Importa el realm de configuración si existe en la carpeta `keycloak/`, o configura manualmente los clientes para los microservicios.

## 🚀 Ejecución de Microservicios

El orden de inicio es **CRÍTICO**. Sigue esta secuencia:

1.  **Discovery Server (Eureka)**
    ```bash
    cd discovery-server
    mvn spring-boot:run
    ```
    *Esperar a que inicie en el puerto 8761.*

2.  **Config Server**
    ```bash
    cd config-server
    mvn spring-boot:run
    ```
    *Esperar a que inicie en el puerto 8888.*

3.  **API Gateway**
    ```bash
    cd api-gateway
    mvn spring-boot:run
    ```
    *Puerto: 8080*

4.  **Servicios de Negocio** (En cualquier orden, pero idealmente después del Gateway)
    *   **Notification Service**: `cd notification-service && mvn spring-boot:run`
    *   **Passenger Service**: `cd passenger-service && mvn spring-boot:run`
    *   **Payment Service**: `cd payment-service && mvn spring-boot:run`
    *   **Trip Service**: `cd trip-service && mvn spring-boot:run`

## 🧪 Verificación

Para verificar que todo está funcionando:
1.  Ve a Eureka Dashboard: `http://localhost:8761`
2.  Deberías ver todos los servicios registrados: `GATEWAY`, `TRIP-SERVICE`, `PASSENGER-SERVICE`, etc.

## 🐛 Solución de Problemas Comunes

*   **Error de conexión a Config Server**: Asegúrate de que `config-server` esté totalmente arriba antes de iniciar los otros servicios.
*   **Error de Kafka**: Verifica que los contenedores de Docker tengan suficiente memoria y que los puertos no estén ocupados.
