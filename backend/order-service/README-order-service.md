# Order Service - Nova Commerce

Microservicio de gestión de órdenes para Nova Commerce.

Implementa **Clean Architecture** con **Ports & Adapters** y **Strategy Pattern** para descuentos. Valida clientes y productos vía Feign, aplica reglas de negocio complejas en el dominio y calcula descuentos automáticamente.

```
Cliente → API Gateway → Order-Service ↔ Customer-Service (Feign)
                            ↓          ↔ Product-Service (Feign)
                        MongoDB
```

## 📋 Requisitos

- Java 17+
- Maven 3.6+
- MongoDB 7.0+ (local o Docker)
- **Customer-Service** corriendo en puerto 8084
- **Product-Service** corriendo en puerto 8083
- IDE: IntelliJ IDEA o VS Code

## 🚀 Instalación y Configuración

### 1. Clonar el repositorio
```bash
git clone https://github.com/LeonardoPerezSoft/Nova-Commerce.git
cd backend/order-service
```

### 2. Base de datos MongoDB (Docker opcional)
```powershell
docker run -d --name nova-mongodb `
  -p 27017:27017 `
  -e MONGO_INITDB_DATABASE=nova_db `
  mongo:7.0
```

### 3. Asegurar que los servicios dependientes estén activos
```powershell
# Customer-Service en http://localhost:8084
# Product-Service en http://localhost:8083
```

### 4. Variables de entorno (opcional)
```bash
SPRING_PROFILES_ACTIVE=local
CUSTOMER_SERVICE_URL=http://localhost:8084
PRODUCT_SERVICE_URL=http://localhost:8083

# JWT
JWT_SECRET=E8F7D6C4B5A3F2E1D9C8B7A6F5E4D3C2B1A0F9E8D7C6B5A4F3E2D1C0B9A8F7E6D5C4B3A2F1E0D9C8B7A6F5E4D3C2B1A0F9E8D7C6B5A4
INTERNAL_API_KEY=nova-internal-service-key-2024
```

### 5. Compilar y ejecutar
```powershell
mvnw.cmd clean compile -DskipTests
mvnw.cmd spring-boot:run -DskipTests
```
El servicio corre en el puerto **8085**.

## 🛠️ Tecnologías

- Spring Boot 3.4.1
- Spring Data MongoDB
- Spring Cloud OpenFeign (integración con microservicios)
- Spring Security (JWT)
- SpringDoc OpenAPI (Swagger UI)
- Lombok
- MapStruct
- Java 17

## 📚 Documentación de API

- Swagger UI: http://localhost:8085/swagger-ui.html
- OpenAPI JSON: http://localhost:8085/v3/api-docs

## 📁 Arquitectura - Clean Architecture

```
com.novacommerce.order_service/
│
├── domain/                          ← DOMINIO (Lógica de negocio)
│   ├── model/                       
│   │   ├── Order.java              # Agregado raíz con reglas de negocio
│   │   ├── OrderItem.java          # Entidad con validaciones
│   │   ├── OrderStatus.java        # Enum con transiciones de estado
│   │   ├── Money.java              # Value Object para dinero
│   │   ├── DiscountResult.java     # Resultado de descuentos
│   │   └── DiscountContext.java    # Contexto para estrategias
│   ├── discount/                    # Strategy Pattern para descuentos
│   │   ├── DiscountStrategy.java   # Interface
│   │   ├── LoyaltyDiscountStrategy.java
│   │   ├── SeasonDiscountStrategy.java
│   │   └── ProductTypeDiscountStrategy.java
│   └── exception/                   # Excepciones de dominio
│       ├── OrderException.java
│       └── BusinessRuleException.java
│
├── application/                     ← APLICACIÓN (Casos de uso)
│   ├── port/
│   │   ├── in/                      # Use Cases
│   │   │   ├── CreateOrderUseCase.java
│   │   │   ├── GetOrderUseCase.java
│   │   │   └── UpdateOrderStatusUseCase.java
│   │   └── out/                     # Interfaces para adaptadores
│   │       ├── OrderPersistencePort.java
│   │       ├── CustomerValidationPort.java
│   │       └── ProductValidationPort.java
│   └── service/
│       └── OrderService.java        # Implementa use cases
│
├── adapter/                         ← ADAPTADORES
│   ├── in/
│   │   └── web/
│   │       ├── OrderRestController.java  # Sin lógica - delega a use cases
│   │       ├── GlobalExceptionHandler.java
│   │       ├── dto/                 # DTOs de request/response
│   │       └── mapper/              # Mappers DTO ↔ Domain
│   └── out/
│       ├── persistence/             # Adaptador MongoDB
│       │   ├── OrderPersistenceAdapter.java
│       │   └── OrderMapper.java
│       ├── customer/                # Feign Client - Customer Service
│       │   ├── CustomerServiceClient.java
│       │   └── CustomerClientAdapter.java
│       └── product/                 # Feign Client - Product Service
│           ├── ProductServiceClient.java
│           └── ProductClientAdapter.java
│
├── repository/                      # MongoDB Repositories
│   ├── OrderRepository.java
│   ├── OrderItemRepository.java
│   └── entity/                      # Documentos MongoDB
│       ├── OrderEntity.java
│       └── OrderItemEntity.java
│
├── config/                          # Configuración
│   └── SecurityConfig.java
│
└── resources/
    ├── application.yaml
    └── (sin migraciones - MongoDB usa colecciones dinámicas)
        ├── changelog-master.yaml
        └── changes/
            ├── 001-create-orders-table.yaml
            ├── 002-create-order-items-table.yaml
            └── 003-seed-sample-orders.yaml
```

## 💼 Reglas de Negocio Implementadas

### Validaciones al crear orden

❌ **No se permite crear orden si:**
- Cliente está `INACTIVE` o `BLOCKED`
- Producto está inactivo
- Stock insuficiente
- Cantidad = 0 o precio negativo
- Orden sin items

### Transiciones de estado

- `CREATED` → `PAID` o `CANCELLED`
- `PAID` → `SHIPPED` o `CANCELLED`
- `SHIPPED` → `COMPLETED`
- `CANCELLED` y `COMPLETED` son estados finales

### Motor de Descuentos (Strategy Pattern)

Se aplican **automáticamente** múltiples estrategias:

1. **LoyaltyDiscountStrategy** (según nivel de fidelidad)
   - BRONZE: 5%
   - SILVER: 10%
   - GOLD: 15%
   - VIP: 20%

2. **SeasonDiscountStrategy** (según temporada)
   - WINTER: 15%
   - SUMMER: 10%
   - SPRING/FALL: 5%

3. **ProductTypeDiscountStrategy** (por tipo de producto)
   - ELECTRONICS: 8%
   - CLOTHING: 12%
   - FOOD: 3%

**Los descuentos se acumulan** y el cálculo es transparente en el dominio.

## 🔐 Seguridad

- Autenticación mediante JWT (Bearer token)
- Autorización por roles: `ROLE_ADMIN`, `ROLE_READ`, `ROLE_CREATE`, `ROLE_UPDATE`
- Rutas públicas: `/v3/api-docs/**`, `/swagger-ui/**`, `/actuator/health`
- Rutas internas: `/internal/**` requieren header `X-Internal-API-Key`

## 🧭 Endpoints

### Orders

| Método | Endpoint | Status | Descripción | Headers |
|--------|----------|--------|-------------|---------|
| POST | `/api/orders` | 201 | Crear orden (valida cliente, productos, stock y aplica descuentos) | `Authorization: Bearer <JWT>` |
| GET | `/api/orders` | 200 | Listar todas las órdenes (paginado) | `Authorization: Bearer <JWT>` |
| GET | `/api/orders/{id}` | 200 | Obtener orden por ID | `Authorization: Bearer <JWT>` |
| GET | `/api/orders/customer/{customerId}` | 200 | Órdenes por cliente | `Authorization: Bearer <JWT>` |
| PATCH | `/api/orders/{id}/status` | 200 | Actualizar estado de orden | `Authorization: Bearer <JWT>` |

## 📋 Ejemplos de uso

### Crear orden
```bash
curl -X POST http://localhost:8085/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your_token>" \
  -d '{
    "customerId": 1,
    "items": [
      {
        "productId": 1,
        "quantity": 2,
        "unitPrice": 50.00
      },
      {
        "productId": 2,
        "quantity": 1,
        "unitPrice": 30.00
      }
    ]
  }'
```

**Respuesta:**
```json
{
  "id": 1,
  "customerId": 1,
  "status": "CREATED",
  "totalBeforeDiscount": 130.00,
  "discountTotal": 19.50,
  "totalAfterDiscount": 110.50,
  "createdAt": "2026-01-07T10:30:00",
  "items": [...]
}
```

### Actualizar estado
```bash
curl -X PATCH http://localhost:8085/api/orders/1/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your_token>" \
  -d '{"status": "PAID"}'
```

### Consultar órdenes por cliente
```bash
curl -X GET http://localhost:8085/api/orders/customer/1 \
  -H "Authorization: Bearer <your_token>"
```

## 🧪 Pruebas

```powershell
mvnw.cmd test
```

## ⚙️ Perfiles

```powershell
mvnw.cmd spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
```

## 🔍 Troubleshooting

### "Customer-Service/Product-Service not found"
- Verifica que ambos servicios estén activos
- Revisa las URLs en `application.yaml`
- Confirma que la `INTERNAL_API_KEY` coincide

### "release version 17 not supported"
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
java -version
mvnw.cmd clean compile
```

### "Connection refused: MongoDB"
- Verifica MongoDB activo en `localhost:27017`
- Revisa URI de conexión en `application.yaml`

## 🏗️ Arquitectura Destacada

### ✅ Clean Architecture
- Dominio independiente de frameworks
- Use cases definen contratos
- Adaptadores implementan detalles técnicos

### ✅ Strategy Pattern
- Descuentos extensibles sin modificar código existente (OCP)
- Cada estrategia es independiente y testeable
- Fácil agregar nuevas reglas de descuento

### ✅ Value Objects
- `Money` con validaciones y precisión
- Inmutabilidad garantizada
- Operaciones seguras

### ✅ Integración con Feign
- Validación de clientes y productos en tiempo real
- Resiliente a errores de servicios externos
- Logging completo de comunicación

## 📞 Soporte

Para soporte y preguntas:
- Email: yesid.perez@sofka.com.co
- Web: https://www.novacommerce.com

---

Desarrollado con ❤️ por **Leonardo Pérez**
