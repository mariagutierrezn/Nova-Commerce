# Nova Commerce Backend - Docker Compose

Este documento explica cómo ejecutar los servicios backend de Nova Commerce usando Docker Compose.

## 📋 Requisitos Previos

- Docker Desktop instalado y corriendo
- Docker Compose v3.8 o superior
- Al menos 4GB de RAM disponible
- Puertos libres: 27017, 5672, 15672, 8080-8086

## 🚀 Inicio Rápido

### 1. Iniciar todos los servicios backend

```bash
cd Nova-Commerce/backend
docker-compose up -d
```

Este comando iniciará:
- MongoDB (puerto 27017)
- RabbitMQ (puertos 5672 AMQP, 15672 Management UI)
- Auth Service (puerto 8081)
- User Service (puerto 8082)
- Product Service (puerto 8083)
- Customer Service (puerto 8084)
- Order Service (puerto 8085)
- Notification Service (puerto 8086) - Worker
- API Gateway (puerto 8080)

### 2. Ver logs

```bash
# Ver todos los logs
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f order-service
docker-compose logs -f notification-service
```

### 3. Detener todos los servicios

```bash
docker-compose down
```

### 4. Detener y eliminar volúmenes (limpiar datos)

```bash
docker-compose down -v
```

## 🔍 Verificar Estado

### Ver servicios corriendo

```bash
docker-compose ps
```

### Verificar salud de los servicios

```bash
# MongoDB
docker-compose exec mongodb mongosh --eval "db.adminCommand('ping')"

# RabbitMQ
docker-compose exec rabbitmq rabbitmq-diagnostics ping

# API Gateway
curl http://localhost:8080/actuator/health

# Notification Service
curl http://localhost:8086/actuator/health
```

## 🌐 Acceso a los Servicios

- **API Gateway**: http://localhost:8080
- **RabbitMQ Management UI**: http://localhost:15672
  - Usuario: `nova`
  - Password: `nova123`
- **Swagger UI**:
  - Auth Service: http://localhost:8081/swagger-ui.html
  - User Service: http://localhost:8082/swagger-ui.html
  - Product Service: http://localhost:8083/swagger-ui.html
  - Customer Service: http://localhost:8084/swagger-ui.html
  - Order Service: http://localhost:8085/swagger-ui.html
  - Notification Service: http://localhost:8086/swagger-ui.html

## 📨 RabbitMQ - Verificar Mensajería

### Acceder a Management UI

1. Abre http://localhost:15672
2. Login con `nova` / `nova123`
3. Ve a la pestaña "Queues"
4. Deberías ver:
   - `order.created.queue`
   - `order.paid.queue`

### Probar Flujo Completo

1. Crea una orden via API:
   ```bash
   curl -X POST http://localhost:8080/api/orders \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer <JWT>" \
     -d '{"customerId": "...", "items": [...]}'
   ```

2. Verifica en RabbitMQ Management UI:
   - Queue `order.created.queue` debería recibir el mensaje
   - Notification Service debería procesarlo

3. Verifica logs del Notification Service:
   ```bash
   docker-compose logs -f notification-service
   ```

## 🛠️ Comandos Útiles

### Reconstruir imágenes

```bash
docker-compose build --no-cache
```

### Reiniciar un servicio específico

```bash
docker-compose restart order-service
docker-compose restart notification-service
```

### Ver mensajes en RabbitMQ

```bash
# Ver mensajes en cola (requiere rabbitmqadmin o Management UI)
docker-compose exec rabbitmq rabbitmqadmin list queues
```

### Limpiar mensajes de RabbitMQ

```bash
# Eliminar todos los mensajes de una cola
docker-compose exec rabbitmq rabbitmqadmin purge queue name=order.created.queue
```

## 🔧 Variables de Entorno

Puedes modificar las variables en `docker-compose.yml` o crear un archivo `.env`:

```bash
SPRING_RABBITMQ_USERNAME=nova
SPRING_RABBITMQ_PASSWORD=nova123
JWT_SECRET=tu-secret-key
```

## 🐛 Troubleshooting

### RabbitMQ no conecta

1. Verifica que RabbitMQ esté saludable:
   ```bash
   docker-compose exec rabbitmq rabbitmq-diagnostics ping
   ```

2. Verifica las credenciales en `application.yaml`

3. Verifica que el virtual host `/nova` exista

### Notification Service no procesa mensajes

1. Verifica que Notification Service esté corriendo:
   ```bash
   docker-compose ps notification-service
   ```

2. Verifica logs:
   ```bash
   docker-compose logs notification-service
   ```

3. Verifica conexión a RabbitMQ en logs

### Mensajes no llegan a las colas

1. Verifica que Order Service esté publicando eventos
2. Verifica configuración de exchange y routing keys
3. Verifica bindings en RabbitMQ Management UI

---

**Desarrollado con ❤️ por Leonardo Pérez**
