# Docker Setup - Nova Commerce Backend

## 📋 Resumen

Este proyecto incluye un enfoque **híbrido** de Docker Compose para facilitar desarrollo, pruebas E2E y refactorización.

## 📁 Estructura de Docker Compose

```
Nova-Commerce/
├── backend/
│   ├── docker-compose.yml          ← Backend completo (MongoDB + RabbitMQ + servicios)
│   └── README-DOCKER.md            ← Documentación específica backend
│
└── docker-compose.yml              ← TODO (Frontend + Backend + MongoDB + RabbitMQ)
                                    ← Para pruebas E2E completas
```

## 🚀 Casos de Uso

### 1. Desarrollo Backend Independiente

```bash
cd Nova-Commerce/backend
docker-compose up -d
```

**Incluye:**
- MongoDB
- RabbitMQ
- Todos los servicios backend
- API Gateway
- Notification Service (Worker)

**Útil para:**
- Desarrollo de nuevas funcionalidades
- Refactorización de servicios
- Testing de integración backend
- Debugging aislado

### 2. Pruebas E2E Completas

```bash
# En la raíz del proyecto
docker-compose up -d
```

**Incluye:**
- Todo lo del backend
- Frontend Angular

**Útil para:**
- Pruebas end-to-end completas
- Validación de integración completa
- Demos y presentaciones
- CI/CD pipelines

### 3. Desarrollo Frontend Independiente

```bash
cd Nova-Commerce-Front
docker-compose up -d
```

**Incluye:**
- Solo Frontend
- Se conecta a backend externo

**Útil para:**
- Desarrollo UI/UX
- Testing de frontend
- Desarrollo paralelo

## 📨 Mensajería Asíncrona

### RabbitMQ Configuration

- **Exchange**: `nova.exchange` (Direct)
- **Queues**:
  - `order.created.queue` → OrderCreatedEvent
  - `order.paid.queue` → OrderPaidEvent

### Management UI

- **URL**: http://localhost:15672
- **Usuario**: `nova`
- **Password**: `nova123`

### Flujo de Eventos

```
1. Order Service crea orden
   ↓
2. Publica OrderCreatedEvent → RabbitMQ
   ↓
3. Notification Service consume evento
   ↓
4. Procesa y envía notificaciones
```

## 🔧 Variables de Entorno

### RabbitMQ

```bash
SPRING_RABBITMQ_HOST=rabbitmq
SPRING_RABBITMQ_PORT=5672
SPRING_RABBITMQ_USERNAME=nova
SPRING_RABBITMQ_PASSWORD=nova123
SPRING_RABBITMQ_VIRTUAL_HOST=/nova
```

### MongoDB

```bash
SPRING_DATA_MONGODB_URI=mongodb://mongodb:27017/nova_db
```

## 🧪 Testing

### Probar Flujo Completo E2E

1. Levantar todo:
   ```bash
   docker-compose up -d
   ```

2. Crear una orden:
   ```bash
   curl -X POST http://localhost:8080/api/orders \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer <JWT>" \
     -d '{"customerId": "...", "items": [...]}'
   ```

3. Verificar en RabbitMQ Management UI:
   - Queue `order.created.queue` debe tener mensajes
   - Notification Service debe procesarlos

4. Ver logs de Notification Service:
   ```bash
   docker-compose logs -f notification-service
   ```

## 📊 Arquitectura de Mensajería

```
┌─────────────────┐
│  Order Service  │ (Publisher)
│                 │
│  createOrder()  │
└────────┬────────┘
         │
         │ publish OrderCreatedEvent
         ↓
┌─────────────────┐
│  RabbitMQ       │
│  Exchange: nova │
│  ┌───────────┐  │
│  │Queue:     │  │
│  │created    │  │
│  │paid       │  │
│  └───────────┘  │
└────────┬────────┘
         │
         │ consume (@RabbitListener)
         ↓
┌─────────────────┐
│ Notification    │ (Worker/Consumer)
│ Service         │
│                 │
│ Process Event   │
│ Send Email      │
└─────────────────┘
```

## 🐛 Troubleshooting

### RabbitMQ no inicia

```bash
# Ver logs
docker-compose logs rabbitmq

# Verificar puertos
netstat -ano | findstr :5672
netstat -ano | findstr :15672
```

### Servicios no conectan a RabbitMQ

1. Verifica que RabbitMQ esté saludable:
   ```bash
   docker-compose exec rabbitmq rabbitmq-diagnostics ping
   ```

2. Verifica credenciales en `application.yaml`

3. Verifica que el virtual host `/nova` exista

### Mensajes no se procesan

1. Verifica que Notification Service esté corriendo
2. Verifica logs: `docker-compose logs notification-service`
3. Verifica que las colas estén creadas en RabbitMQ Management UI

---

**Última actualización**: 10 de enero de 2026
