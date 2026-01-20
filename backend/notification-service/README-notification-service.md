# Notification Service - Nova Commerce

Microservicio worker para procesamiento asíncrono de notificaciones en Nova Commerce.

**Función principal:** Escucha eventos de RabbitMQ y procesa notificaciones (emails, SMS, push, etc.) de forma asíncrona.

```
Order-Service → RabbitMQ → Notification-Service (Worker)
                          ↓
                    Procesa eventos:
                    ├─ OrderCreated → Email de confirmación
                    └─ OrderPaid → Email de pago
```

## 📋 Requisitos

- Java 17+
- Maven 3.6+
- **RabbitMQ 3.12+** (obligatorio)
- IDE: IntelliJ IDEA o VS Code

## 🚀 Instalación y Configuración

### 1. Asegurar que RabbitMQ esté corriendo

```bash
# Con Docker
docker run -d --name nova-rabbitmq \
  -p 5672:5672 \
  -p 15672:15672 \
  -e RABBITMQ_DEFAULT_USER=nova \
  -e RABBITMQ_DEFAULT_PASS=nova123 \
  -e RABBITMQ_DEFAULT_VHOST=/nova \
  rabbitmq:3-management-alpine
```

### 2. Configurar variables de entorno (opcional)

```bash
# RabbitMQ
SPRING_RABBITMQ_HOST=localhost
SPRING_RABBITMQ_PORT=5672
SPRING_RABBITMQ_USERNAME=nova
SPRING_RABBITMQ_PASSWORD=nova123
SPRING_RABBITMQ_VIRTUAL_HOST=/nova

# Service
SPRING_PROFILES_ACTIVE=local
SERVER_PORT=8086
```

### 3. Compilar y ejecutar

```bash
mvn clean compile
mvn spring-boot:run
```

El servicio corre en el puerto **8086**.

## 🛠️ Tecnologías

- Spring Boot 3.4.3
- Spring AMQP (RabbitMQ)
- Spring Boot Actuator (health checks)
- SpringDoc OpenAPI (Swagger UI)
- Lombok
- Java 17

## 📨 Eventos Procesados

### OrderCreatedEvent

Se procesa cuando se crea una nueva orden:
- Envía email de confirmación al cliente
- Registra en logs
- (Futuro) Notificación SMS, Push, etc.

### OrderPaidEvent

Se procesa cuando una orden es pagada:
- Envía email de confirmación de pago
- (Futuro) Genera factura
- (Futuro) Actualiza inventario
- (Futuro) Notifica a shipping service

## 🔍 Verificar Funcionamiento

### RabbitMQ Management UI

Accede a: http://localhost:15672
- Usuario: `nova`
- Password: `nova123`

Verifica:
- Queues: `order.created.queue`, `order.paid.queue`
- Exchange: `nova.exchange`
- Mensajes en cola

### Logs del Servicio

```bash
# Ver logs en tiempo real
docker-compose logs -f notification-service

# O si está ejecutándose localmente
tail -f logs/notification-service.log
```

Deberías ver mensajes como:
```
📨 Received OrderCreatedEvent: orderId=xxx, customerId=yyy
📧 Sending order confirmation email...
✅ OrderCreatedEvent processed successfully
```

## 🧪 Testing

```bash
# Ejecutar tests
mvn test

# Ejecutar con cobertura
mvn verify
```

## ⚙️ Configuración

### application.yaml

```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: nova
    password: nova123
    virtual-host: /nova
    listener:
      simple:
        acknowledge-mode: auto
        prefetch: 10
        concurrency: 1
        max-concurrency: 5
```

## 🔄 Arquitectura

```
┌─────────────────┐
│  Order Service  │
│                 │
│  Creates Order  │
└────────┬────────┘
         │
         │ publish OrderCreatedEvent
         ↓
┌─────────────────┐
│  RabbitMQ       │
│  Exchange: nova │
│  Queue: created │
└────────┬────────┘
         │
         │ consume
         ↓
┌─────────────────┐
│ Notification    │
│ Service         │
│ (Worker)        │
│                 │
│ Process Event   │
│ Send Email      │
└─────────────────┘
```

## 🚧 Próximos Pasos

- [ ] Integración con servicio de email real (SendGrid, SES, etc.)
- [ ] Envío de SMS
- [ ] Push notifications
- [ ] Dead Letter Queue para manejo de errores
- [ ] Retry mechanism con backoff exponencial
- [ ] Métricas y monitoreo
- [ ] Templates de email
- [ ] Internacionalización

---

**Estado**: 🟢 Funcional - Worker básico implementado
**Última actualización**: 10 de enero de 2026
