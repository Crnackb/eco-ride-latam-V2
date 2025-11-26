# 📡 Referencia de API - Eco Ride Latam V2

Este documento describe los principales endpoints expuestos por los microservicios a través del **API Gateway**.

> **Nota**: Todas las peticiones deben pasar por el API Gateway (Puerto `8080`).

## 🚗 Trip Service
Gestión de viajes y reservas.

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `POST` | `/trips` | Crear un nuevo viaje (Conductor) |
| `GET` | `/trips/{id}` | Obtener detalles de un viaje |
| `GET` | `/trips` | Buscar viajes disponibles (filtros: origen, destino) |
| `POST` | `/trips/{id}/reservations` | Reservar un asiento en un viaje |
| `GET` | `/trips/{id}/reservations` | Ver reservas de un viaje |

## 👤 Passenger Service
Gestión de pasajeros y conductores.

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `POST` | `/passengers` | Registrar un nuevo pasajero |
| `GET` | `/passengers/{id}` | Obtener perfil de pasajero |
| `POST` | `/drivers` | Registrar un conductor |
| `GET` | `/drivers/{id}` | Obtener perfil de conductor |
| `POST` | `/ratings` | Calificar un viaje/conductor |

## 💳 Payment Service
Procesamiento de pagos y gestión de transacciones.

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `POST` | `/payments` | Iniciar un proceso de pago |
| `GET` | `/payments/{id}` | Consultar estado de un pago |
| `POST` | `/charges` | Ejecutar cargo (interno/saga) |
| `POST` | `/refunds` | Procesar reembolso |

## 🔔 Notification Service
Envío de notificaciones (Email, SMS, Push).

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `POST` | `/notifications/send` | Enviar notificación manual |
| `GET` | `/notifications/history` | Ver historial de notificaciones |

---

## 🔐 Autenticación
La mayoría de los endpoints protegidos requieren un token **Bearer JWT** en el header `Authorization`.
El token se obtiene autenticándose contra **Keycloak**.
