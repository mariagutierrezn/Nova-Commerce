# Customer Service - Nova Commerce

Microservicio de gestión de clientes para Nova Commerce.

Implementa Clean Architecture con separación entre dominio, aplicación y adaptadores. Persiste en MongoDB mediante Spring Data MongoDB. Expone API REST protegida por JWT y validaciones de rol.

```
Cliente → API Gateway → Customer-Service → MongoDB
                    ↓
                Swagger / OpenAPI
```

## 📋 Requisitos

- Java 17+
- Maven 3.6+
- MongoDB 7.0+ (local o Docker)
- IDE: IntelliJ IDEA o VS Code

## 🚀 Instalación y Configuración

### 1. Clonar el repositorio
```bash
git clone https://github.com/LeonardoPerezSoft/Nova-Commerce.git
cd backend/customer-service
```

### 2. Base de datos MongoDB (Docker opcional)
```powershell
docker run -d --name nova-mongodb `
  -p 27017:27017 `
  -e MONGO_INITDB_DATABASE=nova_db `
  mongo:7.0
```

### 3. Variables de entorno (opcional)
Puedes sobrescribir las propiedades de `application.yaml`:
```bash
SPRING_PROFILES_ACTIVE=local
SPRING_DATA_MONGODB_URI=mongodb://localhost:27017/nova_db

# Semilla de datos
SEED_ENABLED=true

# Seguridad JWT
JWT_SECRET=E8F7D6C4B5A3F2E1D9C8B7A6F5E4D3C2B1A0F9E8D7C6B5A4F3E2D1C0B9A8F7E6D5C4B3A2F1E0D9C8B7A6F5E4D3C2B1A0F9E8D7C6B5A4
INTERNAL_API_KEY=nova-internal-service-key-2024
```

### 4. Compilar y ejecutar
```powershell
mvnw.cmd clean compile -DskipTests
mvnw.cmd spring-boot:run -DskipTests
```
Por defecto, el servicio corre en el puerto 8084 (ver configuración en [backend/customer-service/src/main/resources/application.yaml](backend/customer-service/src/main/resources/application.yaml)).

## 🛠️ Tecnologías

- Spring Boot 3.x
- Spring Data MongoDB
- Spring Security (JWT)
- SpringDoc OpenAPI (Swagger UI)
- Lombok
- MapStruct
- Java 17

## 📚 Documentación de API

- Swagger UI: http://localhost:8084/swagger-ui.html
- OpenAPI JSON: http://localhost:8084/v3/api-docs

## 📁 Estructura del Proyecto - Clean Architecture

```
com.novacommerce.customer_service/
│
├── domain/                          ← Dominio
│   └── model/                       # Customer, CustomerStatus
│
├── application/                     ← Aplicación
│   ├── port/
│   │   ├── in/                      # ManageCustomersUseCase, ManageCustomerStatusUseCase
│   │   └── out/                     # CustomerPersistencePort
│   └── service/                     # CustomerService
│
├── adapter/                         ← Adaptadores
│   ├── in/
│   │   └── web/
│   │       ├── CustomerRestController.java       # Public /api/customers/**
│   │       ├── InternalCustomerController.java   # Internal /internal/customers/{id}
│   │       ├── dto/                 # CustomerDto, InternalCustomerResponse
│   │       └── mapper/              # CustomerMapper
│   └── out/                         # Persistencia/Integraciones (si aplica)
│
├── config/                          ← Configuración
│   └── SecurityConfig.java          # Filtro JWT + llave interna
│
└── resources/
    ├── application.yaml             # Puerto 8084, MongoDB, springdoc
    └── (sin migraciones - MongoDB usa colecciones dinámicas)
```

Archivos relevantes:
- Configuración general: [backend/customer-service/src/main/resources/application.yaml](backend/customer-service/src/main/resources/application.yaml)
- Controlador REST: [backend/customer-service/src/main/java/com/novacommerce/customer_service/adapter/in/web/CustomerRestController.java](backend/customer-service/src/main/java/com/novacommerce/customer_service/adapter/in/web/CustomerRestController.java)
- **Colecciones MongoDB**: Se crean automáticamente al iniciar el servicio
- **Seed de datos**: El servicio incluye seed de clientes iniciales si está habilitado (SEED_ENABLED=true)
- Filtro API interna: [backend/customer-service/src/main/java/com/novacommerce/customer_service/adapter/in/security/InternalApiKeyFilter.java](backend/customer-service/src/main/java/com/novacommerce/customer_service/adapter/in/security/InternalApiKeyFilter.java)

## 🔐 Seguridad

### JWT Authentication
- Autenticación mediante JWT (Bearer token) y autorización por roles.
- Roles usados: `ROLE_ADMIN`, `ROLE_READ`, `ROLE_CREATE`, `ROLE_UPDATE`, `ROLE_DELETE`.
- Rutas públicas: `/v3/api-docs/**`, `/swagger-ui.html`, `/swagger-ui/**`, `/actuator/health`, `/actuator/info`.

### Internal API Key Protection
- Rutas internas: cualquier ruta que empiece por `/internal/**` requiere el header `X-Internal-API-Key`:
  ```
  X-Internal-API-Key: nova-internal-service-key-2024
  ```
- Validado por `InternalApiKeyFilter` para comunicación entre servicios.
- Order-Service utiliza este endpoint para validar clientes antes de crear órdenes.

## 🧭 Endpoints

### Public Customers
- GET `/api/customers` — Listar clientes
- GET `/api/customers/{id}` — Obtener cliente por ID
- POST `/api/customers` — Crear cliente
- PUT `/api/customers/{id}` — Actualizar cliente
- DELETE `/api/customers/{id}` — Eliminar cliente

### Internal Customers (Comunicación Inter-Servicios)
- GET `/internal/customers/{id}` — Obtener cliente (Order-Service) - Requiere `X-Internal-API-Key`

## 📋 Ejemplos de uso

### Listar clientes
```bash
curl -X GET http://localhost:8084/api/customers \
  -H "Authorization: Bearer <your_access_token>"
```

### Crear cliente
```bash
curl -X POST http://localhost:8084/api/customers \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your_access_token>" \
  -d '{
    "firstName": "Alice",
    "lastName": "García",
    "email": "alice@nova.com",
    "phone": "+57 3001112233",
    "status": "ACTIVE",
    "loyaltyLevel": "BRONZE"
  }'
```

### Ruta interna (Order-Service)
```bash
curl -X GET http://localhost:8084/internal/customers/3 \
  -H "X-Internal-API-Key: nova-internal-service-key-2024"

# Response:
{
  "id": 3,
  "names": "Juan Pérez",
  "email": "juan@nova.com",
  "phone": "+57 3001112233",
  "status": "ACTIVE",
  "loyaltyLevel": "GOLD"
}
```

## 🧪 Pruebas

```powershell
mvnw.cmd test
```

## ⚙️ Perfiles

```powershell
mvnw.cmd spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
```

## 🔗 Integración con Gateway y Otros Servicios

### Ruta en Gateway
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: customer-service
          uri: http://localhost:8084
          predicates:
            - Path=/api/customers/**
```

### Comunicación Inter-Servicios (Feign)

Order-Service utiliza CustomerServiceClient (Feign) para validar clientes:

```java
@FeignClient(name = "customer-service", url = "http://localhost:8084")
public interface CustomerServiceClient {
    @GetMapping("/internal/customers/{id}")
    InternalCustomerResponse getCustomer(
        @PathVariable Long id,
        @RequestHeader("X-Internal-API-Key") String apiKey
    );
}
```

Endpoint `/internal/customers/{id}` retorna datos básicos del cliente (id, nombres, email, phone, status, loyaltyLevel) protegido con InternalApiKeyFilter.

## 🔍 Troubleshooting

### "release version 17 not supported"
Configura Java 17 en Windows:
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
java -version
mvnw.cmd clean compile
mvnw.cmd package -DskipTests
```

### "Connection refused: MongoDB"
- Verifica que el contenedor/servicio MongoDB esté activo en `localhost:27017`.
- Revisa la URI de conexión en `SPRING_DATA_MONGODB_URI`.

### "Port 8084 already in use"
Cambia el puerto en `application.yaml`:
```yaml
server:
  port: 9094
```

## 📞 Soporte

Para soporte y preguntas:
- Email: yesid.perez@sofka.com.co
- Web: https://www.novacommerce.com

---

Desarrollado con ❤️ por **Leonardo Pérez**