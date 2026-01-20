# Product Service - Nova Commerce

## 📦 Descripción

Microservicio de gestión de productos y categorías para la plataforma Nova Commerce. Implementado siguiendo Clean Architecture (Arquitectura Hexagonal) con patrón Ports & Adapters.

## 🏗️ Arquitectura

### Clean Architecture / Hexagonal

```
product-service/
├── adapter/
│   ├── in/
│   │   ├── web/            # REST Controllers
│   │   │   ├── ProductRestController       # Public /api/products/**
│   │   │   ├── CategoryRestController      # Public /api/categories/**
│   │   │   ├── InternalProductController   # Internal /internal/products/{id}
│   │   │   └── GlobalExceptionHandler
│   │   └── filter/         # Security Filters
│   │       ├── JwtAuthenticationFilter
│   │       └── InternalApiKeyFilter
│   └── out/
│       ├── persistence/    # Database Adapters
│       │   ├── ProductPersistenceAdapter
│       │   └── CategoryPersistenceAdapter
│       └── jwt/            # JWT Validation
│           └── JwtTokenValidatorAdapter
├── application/
│   ├── port/
│   │   ├── in/            # Use Cases
│   │   │   ├── ManageProductsUseCase
│   │   │   └── ManageCategoriesUseCase
│   │   └── out/           # Persistence Ports
│   │       ├── ProductPersistencePort
│   │       └── CategoryPersistencePort
│   └── service/           # Business Logic
│       ├── ProductService
│       └── CategoryService
├── domain/
│   ├── model/             # Domain Entities
│   │   ├── Product
│   │   ├── Category
│   │   └── ProductType (enum)
│   └── exception/         # Domain Exceptions
│       ├── ProductException
│       ├── CategoryException
│       └── ResourceNotFoundException
├── repository/
│   ├── entity/            # Documentos MongoDB
│   │   ├── ProductEntity
│   │   └── CategoryEntity
│   ├── mapper/            # Entity Mappers
│   │   ├── ProductEntityMapper
│   │   └── CategoryEntityMapper
│   ├── ProductRepository
│   └── CategoryRepository
└── config/                # Configuration
    ├── security/
    │   └── SecurityConfig
    ├── OpenApiConfig
    └── MapperConfig
```

## 🚀 Características

- ✅ **Clean Architecture**: Separación clara de responsabilidades
- ✅ **JWT Authentication**: Validación de tokens JWT desde gateway
- ✅ **Role-Based Access**: Control de acceso basado en roles (ADMIN, USER)
- ✅ **API Key Protection**: Protección de endpoints internos
- ✅ **Base de Datos**: MongoDB con colecciones dinámicas
- ✅ **MapStruct**: Mapeo eficiente entre capas
- ✅ **OpenAPI/Swagger**: Documentación automática de API
- ✅ **Exception Handling**: Manejo global de excepciones
- ✅ **Validation**: Validación de entrada con Bean Validation

## 📋 Modelo de Datos

### Category

| Campo       | Tipo          | Descripción              |
|-------------|---------------|--------------------------|
| id          | Long          | ID único                 |
| name        | String(100)   | Nombre (único)           |
| description | String(500)   | Descripción              |
| status      | String(20)    | ACTIVE / INACTIVE        |

### Product

| Campo         | Tipo           | Descripción                       |
|---------------|----------------|-----------------------------------|
| id            | Long           | ID único                          |
| name          | String(200)    | Nombre del producto               |
| description   | String(1000)   | Descripción                       |
| price         | BigDecimal     | Precio (>= 0)                     |
| productType   | ProductType    | PHYSICAL/DIGITAL/SERVICE/SUBSCRIPTION |
| categoryId    | Long           | FK a categories                   |
| stockQuantity | Integer        | Cantidad en stock (>= 0)          |
| status        | String(20)     | ACTIVE / INACTIVE                 |

## 🔌 API Endpoints

### Public Products

| Método | Endpoint                        | Roles        | Descripción                |
|--------|---------------------------------|--------------|----------------------------|
| GET    | /api/products                   | ADMIN, USER  | Listar productos (paginado)|
| GET    | /api/products/{id}              | ADMIN, USER  | Obtener producto por ID    |
| GET    | /api/products/category/{id}     | ADMIN, USER  | Productos por categoría    |
| POST   | /api/products                   | ADMIN        | Crear producto             |
| PUT    | /api/products/{id}              | ADMIN        | Actualizar producto        |
| DELETE | /api/products/{id}              | ADMIN        | Eliminar producto          |

### Internal Products (Comunicación Inter-Servicios)

| Método | Endpoint                | Header (Requerido)           | Descripción               |
|--------|-------------------------|------------------------------|---------------------------|
| GET    | /internal/products/{id} | X-Internal-API-Key: *key*    | Obtener producto (Order-Service) |

### Categories

| Método | Endpoint                 | Roles        | Descripción                  |
|--------|--------------------------|--------------|------------------------------|
| GET    | /api/categories          | ADMIN, USER  | Listar categorías (paginado) |
| GET    | /api/categories/{id}     | ADMIN, USER  | Obtener categoría por ID     |
| POST   | /api/categories          | ADMIN        | Crear categoría              |
| PUT    | /api/categories/{id}     | ADMIN        | Actualizar categoría         |
| DELETE | /api/categories/{id}     | ADMIN        | Eliminar categoría           |

## 🛠️ Tecnologías

- **Java 17**
- **Spring Boot 3.4.3**
- **Spring Security 6.4.3** - JWT validation
- **Spring Data MongoDB** - Persistencia
- **MongoDB** - Base de datos NoSQL
- **MapStruct 1.5.5** - Mapeo de objetos
- **Lombok** - Reducción de boilerplate
- **SpringDoc OpenAPI 2.7.0** - Documentación
- **JJWT 0.12.5** - JWT processing
- **Maven** - Build tool

## ⚙️ Configuración

### Variables de Entorno

```bash
# Database MongoDB
SPRING_DATA_MONGODB_URI=mongodb://localhost:27017/nova_db

# Server
SERVER_PORT=8083
SPRING_PROFILE=local

# JWT
JWT_SECRET=bm92YS1jb21tZXJjZS1zdXBlci1zZWNyZXQta2V5LTIwMjQtc2VjdXJlLXNpZ24ta2V5LWZvci1qd3QtdG9rZW5z
INTERNAL_API_KEY=nova-internal-service-key-2024

# Logging
LOG_LEVEL=INFO
```

### Perfiles de Spring

- **local**: Desarrollo local (puerto 8083, logs DEBUG)
- **dev**: Desarrollo en servidor
- **prod**: Producción (logs WARN, pool aumentado)

## 🗄️ Base de Datos

### Configurar MongoDB

MongoDB puede ejecutarse localmente o usando Docker:

```bash
# Con Docker
docker run -d --name nova-mongodb \
  -p 27017:27017 \
  -e MONGO_INITDB_DATABASE=nova_db \
  mongo:7.0
```

### Colecciones MongoDB

Las colecciones se crean automáticamente al iniciar:
- **categories**: Categorías de productos
- **products**: Productos del catálogo

El servicio incluye seed de datos iniciales si está habilitado (SEED_ENABLED=true).

## 🚀 Ejecución

### Compilar

```bash
./mvnw clean package -DskipTests
```

### Ejecutar

```bash
# Perfil local
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# Con variables de entorno
SPRING_PROFILE=dev ./mvnw spring-boot:run
```

### Ejecutar JAR

```bash
java -jar target/product-service-1.0.0-SNAPSHOT.jar --spring.profiles.active=local
```

## 🔐 Seguridad

### JWT Authentication

El servicio valida tokens JWT enviados por el gateway:

```
Authorization: Bearer <JWT_TOKEN>
```

El token debe contener:
- `sub`: username
- `roles`: array de roles (ROLE_ADMIN, ROLE_USER)

### Internal API Key

Endpoints bajo `/internal/**` requieren header de protección:

```
X-Internal-API-Key: nova-internal-service-key-2024
```

Este endpoint es utilizado por order-service para validar disponibilidad de productos via Feign client.

## 📝 Ejemplos de Uso

### Crear Categoría (ADMIN)

```bash
curl -X POST http://localhost:8083/api/categories \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Sports",
    "description": "Sports equipment and apparel",
    "status": "ACTIVE"
  }'
```

### Crear Producto (ADMIN)

```bash
curl -X POST http://localhost:8083/api/products \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Running Shoes",
    "description": "High-performance running shoes",
    "price": 89.99,
    "productType": "PHYSICAL",
    "categoryId": 1,
    "stockQuantity": 100,
    "status": "ACTIVE"
  }'
```

### Listar Productos (USER/ADMIN)

```bash
curl -X GET "http://localhost:8083/api/products?page=0&size=10" \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

### Productos por Categoría

```bash
curl -X GET "http://localhost:8083/api/products/category/1?page=0&size=10" \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

### Obtener Producto (Internal - Order-Service)

```bash
curl -X GET "http://localhost:8083/internal/products/1" \
  -H "X-Internal-API-Key: nova-internal-service-key-2024"

# Response:
{
  "id": 1,
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 999.99,
  "categoryName": "Electronics",
  "stock": 50,
  "status": "ACTIVE"
}
```

## 📚 Documentación API

Swagger UI disponible en:

```
http://localhost:8083/swagger-ui.html
```

OpenAPI JSON:

```
http://localhost:8083/v3/api-docs
```

## 🔗 Integración con Gateway y Otros Servicios

### Ruta en Gateway

El gateway (puerto 8080) debe configurar la ruta:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: product-service
          uri: http://localhost:8083
          predicates:
            - Path=/api/products/**, /api/categories/**
```

### Comunicación Inter-Servicios (Feign)

Order-Service utiliza ProductServiceClient (Feign) para validar productos:

```java
@FeignClient(name = "product-service", url = "http://localhost:8083")
public interface ProductServiceClient {
    @GetMapping("/internal/products/{id}")
    ProductResponse getProduct(
        @PathVariable Long id,
        @RequestHeader("X-Internal-API-Key") String apiKey
    );
}
```

Endpoint `/internal/products/{id}` está protegido con InternalApiKeyFilter que valida el header X-Internal-API-Key.

## 📊 Monitoreo

### Actuator Endpoints

- Health: `http://localhost:8083/actuator/health`
- Info: `http://localhost:8083/actuator/info`
- Metrics: `http://localhost:8083/actuator/metrics`
- Prometheus: `http://localhost:8083/actuator/prometheus`

## 🧪 Testing

```bash
# Ejecutar tests
./mvnw test

# Con coverage
./mvnw verify
```

## 📦 Extensibilidad

El diseño está preparado para futuras extensiones:

- **Pricing Service**: Gestión avanzada de precios
- **Discount Service**: Promociones y descuentos
- **Inventory Service**: Control de stock avanzado
- **Review Service**: Reseñas de productos

## 👥 Roles y Permisos

| Operación                | ADMIN | USER |
|--------------------------|-------|------|
| Listar productos         | ✅     | ✅    |
| Ver producto             | ✅     | ✅    |
| Crear producto           | ✅     | ❌    |
| Actualizar producto      | ✅     | ❌    |
| Eliminar producto        | ✅     | ❌    |
| Listar categorías        | ✅     | ✅    |
| Ver categoría            | ✅     | ✅    |
| Crear categoría          | ✅     | ❌    |
| Actualizar categoría     | ✅     | ❌    |
| Eliminar categoría       | ✅     | ❌    |

## 📄 Licencia

Nova Commerce - Product Service © 2024

---

**Desarrollado con Clean Architecture para escalabilidad y mantenibilidad**
