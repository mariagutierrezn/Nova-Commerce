# API Gateway 

## Descripción

API Gateway central para la plataforma ecommerce Nova Commerce. Proporciona un punto único de entrada para todos los microservicios, validación de JWT local y enrutamiento inteligente.

### Arquitectura de Microservicios

```
Cliente
  ↓
API Gateway (8080) [Valida JWT, enruta, inyecta headers]
  ├─→ Auth-Service (8081)           [Autenticación, genera JWT]
  ├─→ User-Service (8082)           [Gestión de usuarios, roles, permisos]
  ├─→ Customer-Service (8084)       [Gestión de clientes, niveles de lealtad]
  ├─→ Product-Service (8083)        [Catálogo de productos, inventario]
  └─→ Order-Service (8085)          [Gestión de órdenes, descuentos, validaciones]
       ├─→ (Feign) Customer-Service [Validar cliente, obtener nivel de lealtad]
       └─→ (Feign) Product-Service  [Validar producto, verificar stock]
```

**Roles de Servicios:**
- **Auth-Service**: Genera JWT mediante validación con User-Service vía Feign Client
- **User-Service**: Gestiona usuarios, roles y permisos (base de datos MongoDB)
- **Customer-Service**: Gestiona clientes, niveles de fidelidad, estado de cuenta
- **Product-Service**: Catálogo de productos, gestión de inventario, categorías
- **Order-Service**: Crea y gestiona órdenes, valida clientes/productos via Feign, aplica descuentos mediante Strategy Pattern
- **API Gateway**: Valida JWT localmente, enruta a servicios backend, inyecta headers personalizados

## Características

- ✅ Punto único de entrada (Single Entry Point)
- ✅ Validación LOCAL de JWT (sin llamadas remotas)
- ✅ Enrutamiento hacia microservicios
- ✅ Inyección de headers personalizados (X-Username, X-Authorities)
- ✅ Configuración CORS
- ✅ Rutas públicas y protegidas
- ✅ Programación reactiva con WebFlux
- ✅ **Clean Architecture** con separación de capas

## Tecnologías

- Spring Boot 3.5.9
- Spring Cloud Gateway
- Java 17
- Maven
- JWT (JJWT 0.12.5)
- Project Reactor (WebFlux)
- Lombok

## Estructura del Proyecto - Clean Architecture

```
com.novacommerce.gateway/
│
├── domain/                          ← CAPA DE DOMINIO
│   ├── model/
│   │   └── Token.java              # Entidad de dominio para Token JWT
│   └── exception/
│       └── AuthenticationException  # Excepciones de dominio
│
├── application/                     ← CAPA DE APLICACIÓN
│   └── port/
│       └── TokenValidatorPort.java # Puerto (interfaz) de validación
│
├── adapter/                         ← CAPA DE ADAPTADORES
│   ├── in/                          # Adaptadores de entrada (HTTP)
│   │   └── filter/
│   │       └── JwtAuthenticationFilter  # Filtro global de autenticación
│   │
│   └── out/                         # Adaptadores de salida (dependencias)
│       ├── jwt/
│       │   └── JwtTokenValidatorAdapter  # Implementación de validación JWT
│       └── header/
│           └── HeaderUtils          # Utilidades para gestión de headers
│
├── config/                          ← CAPA DE CONFIGURACIÓN
│   ├── GatewayConfig.java          # Configuración de rutas
│   ├── SecurityConfig.java         # Configuración de seguridad
│   ├── CorsConfig.java             # Configuración CORS
│   └── SecurityProperties.java     # Properties de seguridad
│
├── routing/
│   └── RouteConstants.java         # Constantes de enrutamiento
│
└── NovaGatewayApplication.java     # Punto de entrada
```

### Explicación de Clean Architecture

#### **Domain Layer (Dominio)**
- Define las entidades y reglas de negocio puras
- Independiente de frameworks y bases de datos
- `Token`: modelo que representa un token validado
- `AuthenticationException`: excepciones específicas del dominio

#### **Application Layer (Aplicación)**
- Define los puertos (interfaces)
- `TokenValidatorPort`: contrato para validar tokens
- Independiente de la implementación

#### **Adapter Layer (Adaptadores)**
- Implementa los puertos de la capa de aplicación
- `JwtTokenValidatorAdapter`: implementación JJWT del puerto
- `JwtAuthenticationFilter`: adaptador de entrada (filtro HTTP)
- `HeaderUtils`: adaptador para gestión de headers

#### **Framework Layer (Configuración)**
- Configuración de Spring, rutas, CORS
- Wiring de beans
- Propiedades de la aplicación

### Ventajas de esta Arquitectura

✅ **Independencia de Framework**: El dominio no depende de Spring  
✅ **Testabilidad**: Fácil crear mocks de puertos  
✅ **Mantenibilidad**: Responsabilidades claramente definidas  
✅ **Extensibilidad**: Fácil agregar nuevas implementaciones

## Configuración

### Variables de Entorno

```bash
JWT_SECRET=E8F7D6C4B5A3F2E1D9C8B7A6F5E4D3C2B1A0F9E8D7C6B5A4F3E2D1C0B9A8F7E6D5C4B3A2F1E0D9C8B7A6F5E4D3C2B1A0F9E8D7C6B5A4
```

**IMPORTANTE**: Este SECRET_KEY debe ser el mismo en auth-service y api-gateway.

### application.yaml

El archivo `application.yaml` contiene:
- Configuración del servidor (puerto 8080)
- Configuración JWT (secret, expiración)
- Rutas de microservicios
- Rutas públicas (sin autenticación)
- Configuración CORS
- Configuración de Actuator
- Niveles de logging

## Rutas

### Rutas Públicas (sin JWT)

- `/api/auth/login` - Login de usuarios
- `/api/auth/refresh` - Renovación de tokens
- `/swagger-ui/**` - Documentación Swagger
- `/v3/api-docs/**` - OpenAPI docs
- `/actuator/**` - Health checks y métricas

### Rutas Protegidas (con JWT)

Todas requieren token válido en header `Authorization: Bearer <token>`

#### Auth Service (8081)
- `POST /api/auth/login` - Genera JWT
- `POST /api/auth/refresh` - Renueva token

#### User Service (8082)
- `/api/users/**` - Gestión de usuarios (CRUD, búsqueda)
- `/api/roles/**` - Gestión de roles y permisos

#### Customer Service (8084)
- `GET /api/customers` - Listar clientes (paginado)
- `GET /api/customers/{id}` - Obtener cliente por ID
- `POST /api/customers` - Crear cliente (ADMIN)
- `PATCH /api/customers/{id}` - Actualizar cliente

#### Product Service (8083)
- `GET /api/products` - Listar productos (paginado)
- `GET /api/products/{id}` - Obtener producto por ID
- `GET /api/products/category/{categoryId}` - Productos por categoría
- `POST /api/products` - Crear producto (ADMIN)
- `PATCH /api/products/{id}` - Actualizar producto (ADMIN)
- `DELETE /api/products/{id}` - Eliminar producto (ADMIN)

#### Order Service (8085)
- `GET /api/orders` - Listar órdenes del usuario autenticado
- `GET /api/orders/{id}` - Obtener orden por ID
- `GET /api/orders/customer/{customerId}` - Órdenes de un cliente (paginado)
- `POST /api/orders` - Crear nueva orden (valida cliente, productos, stock; aplica descuentos)
- `PATCH /api/orders/{id}/status` - Actualizar estado de orden

## Flujo de Autenticación

### 1. Validación de JWT (Adapter In - Filtro)

El `JwtAuthenticationFilter` (adapter de entrada) intercepta todas las requests:

```
Request → JwtAuthenticationFilter (adapter/in/filter)
          ├─ Verifica si es ruta pública
          │  └─ Sí: permite paso directo
          └─ No: valida token
             ├─ Extrae token del header Authorization: Bearer <token>
             ├─ Delega validación al puerto TokenValidatorPort
             ├─ El adapter JWT implementa el puerto
             └─ Si es válido:
                 ├─ Extrae username y authorities
                 ├─ Inyecta headers X-Username y X-Authorities
                 └─ Continúa hacia microservicio backend
                 Si es inválido: retorna 401 Unauthorized (JSON)
```

**Flujo de capas:**
```
Adapter In (Filtro HTTP)
    ↓ (delega a)
Application Port (TokenValidatorPort)
    ↓ (implementado por)
Adapter Out (JwtTokenValidatorAdapter)
    ↓ (usa)
Domain (Token model, AuthenticationException)
```

### 2. Enrutamiento (Adapter Gateway)

El `GatewayConfig` define las rutas hacia los microservicios:

```yaml
/api/auth/**  → http://localhost:8081 (auth-service)
/api/users/** → http://localhost:8082 (user-service)
/api/roles/** → http://localhost:8082 (user-service)
```

### 3. Respuestas de Error

En caso de error de autenticación, se retorna JSON estructurado:

```json
{
  "error": "Unauthorized",
  "message": "Invalid or expired token",
  "status": 401,
  "timestamp": "2026-01-06T10:30:45",
  "path": "/api/users/123"
}
```

## Ejecución

### Todos los Servicios (orden de inicio)

**Importante**: Iniciar en este orden para evitar errores de conexión:

```bash
# Terminal 1: Auth Service (8081) - Base para generar JWT
cd backend/auth-service
./mvnw spring-boot:run

# Terminal 2: User Service (8082) - Datos de usuarios
cd backend/user-service
./mvnw spring-boot:run

# Terminal 3: Customer Service (8084)
cd backend/customer-service
./mvnw spring-boot:run

# Terminal 4: Product Service (8083)
cd backend/product-service
./mvnw spring-boot:run

# Terminal 5: Order Service (8085) - Depende de Customer y Product
cd backend/order-service
./mvnw spring-boot:run

# Terminal 6: API Gateway (8080) - Punto de entrada
cd backend/nova-gateway
./mvnw spring-boot:run
```

### Compilar Individualmente

```bash
cd backend/<service-name>
./mvnw clean package
```

### Ejecutar Específico

```bash
cd backend/<service-name>
./mvnw spring-boot:run
```

O con variable de entorno personalizada:

```bash
JWT_SECRET=tu-secreto-personalizado ./mvnw spring-boot:run
```

### Ejecutar JAR

```bash
java -jar target/<service-name>-0.0.1-SNAPSHOT.jar
```

## Pruebas

### 1. Obtener JWT (sin autenticación)

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**Respuesta:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600
}
```

### 2. Crear Orden (protegido, requiere JWT)

```bash
JWT_TOKEN="tu-token-aqui"

curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 3,
    "items": [
      {
        "productId": 1,
        "quantity": 1,
        "unitPrice": 1299.99
      }
    ]
  }'
```

### 3. Listar Clientes (protegido)

```bash
JWT_TOKEN="tu-token-aqui"

curl -X GET "http://localhost:8080/api/customers?page=0&size=10" \
  -H "Authorization: Bearer $JWT_TOKEN"
```

### 4. Listar Productos (protegido)

```bash
JWT_TOKEN="tu-token-aqui"

curl -X GET "http://localhost:8080/api/products?page=0&size=10" \
  -H "Authorization: Bearer $JWT_TOKEN"
```

### 5. Obtener Orden por ID

```bash
JWT_TOKEN="tu-token-aqui"

curl -X GET http://localhost:8080/api/orders/1 \
  -H "Authorization: Bearer $JWT_TOKEN"
```

### Respuesta esperada (sin token)

```json
{
  "error": "Unauthorized",
  "message": "Invalid or expired token",
  "status": 401,
  "timestamp": "2026-01-07T18:30:45",
  "path": "/api/orders"
}
```

## Integración con Microservicios

### Arquitectura de Confianza

**Cliente → API Gateway (valida JWT) → Microservicios (confían en el Gateway)**

```
┌─────────────┐
│   Cliente   │
└──────┬──────┘
       │ REST con JWT
       ↓
┌───────────────────────────────────────────┐
│        API Gateway (8080)                 │
│  ├─ Valida JWT localmente (sin latencia) │
│  ├─ Enruta a servicios backend            │
│  └─ Inyecta headers (X-Username, etc)    │
└────┬──────────────────┬────────────────────┘
     │                  │
     ├─ /api/auth/**    ├─ /api/users/**
     ├─ /api/customers/**  ├─ /api/products/**
     └─ /api/orders/**
```

**Principios clave:**
- ✅ **Un solo punto de validación**: Gateway valida JWT
- ✅ **Servicios no validan JWT**: Confían en el Gateway
- ✅ **Headers personalizados**: Gateway inyecta X-Username, X-Authorities, etc.
- ✅ **Comunicación inter-servicio**: Via Feign + X-Internal-API-Key (sin JWT)
- ✅ **Endpoints públicos en servicios**: `/api/**` permitido (confianza en Gateway)
- ✅ **Endpoints privados**: `/internal/**` protegidos por API Key

### Diagrama de Flujo Completo

```
1. Cliente llama: POST /api/orders (con JWT)
   ↓
2. Gateway recibe, valida JWT localmente
   ├─ Si inválido: 401 Unauthorized
   └─ Si válido: inyecta X-Username y continúa
   ↓
3. Order-Service recibe request (confía en Gateway)
   ├─ Extrae customerId del body
   ├─ Llama (vía Feign) a: GET /internal/customers/{customerId}
   │  ├─ Incluye header: X-Internal-API-Key
   │  └─ Customer-Service valida API Key y responde
   │
   ├─ Valida productos (también vía Feign)
   │  └─ GET /internal/products/{productId}
   │
   ├─ Aplica descuentos (Strategy Pattern):
   │  ├─ ProductType discount
   │  ├─ Loyalty discount (según nivel de cliente)
   │  └─ Season discount
   │
   └─ Retorna orden creada (201)
   ↓
4. Gateway envía respuesta al cliente
```

### Seguridad Inter-Servicio

**X-Internal-API-Key** protege endpoints internos:

```java
// En InternalApiKeyFilter de cada servicio:
if (request.getURI().startsWith("/internal/")) {
    String key = request.getHeader("X-Internal-API-Key");
    if (!key.equals("nova-internal-service-key-2024")) {
        response.setStatus(401);
        return; // No continúa
    }
}
```

**Configuración compartida:**
```yaml
app:
  jwt:
    internal-api-key: nova-internal-service-key-2024  # Mismo en todos los servicios
```

## Agregar Nuevos Microservicios

1. Actualizar `application.yaml`:

```yaml
gateway:
  routes:
    nuevo-service:
      uri: http://localhost:8090
      path: /api/nuevo/**
```

2. Actualizar `GatewayConfig.java`:

```java
@Value("${gateway.routes.nuevo-service.uri}")
private String nuevoServiceUri;

@Value("${gateway.routes.nuevo-service.path}")
private String nuevoServicePath;

// En customRouteLocator():
.route("nuevo-service", r -> r
    .path(nuevoServicePath)
    .filters(f -> f
        .stripPrefix(0)
        .removeRequestHeader("Cookie"))
    .uri(nuevoServiceUri))
```

3. **Importante - Configuración del Servicio Backend:**

El nuevo servicio **NO debe validar JWT**. Debe confiar en el API Gateway:

- Comentar o deshabilitar `@EnableMethodSecurity` en SecurityConfig
- Permitir `/api/**` sin autenticación en SecurityFilterChain
- Implementar filtro `InternalApiKeyFilter` para endpoints `/internal/**` (inter-service communication)
- Configurar endpoints `/internal/**` con `@PreAuthorize` deshabilitado (protegidos solo por API Key)

Ejemplo SecurityConfig:
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/actuator/**").permitAll()
                .requestMatchers("/internal/**").permitAll()  // Protegido por InternalApiKeyFilter
                .requestMatchers("/api/**").permitAll()      // Confianza en API Gateway
                .anyRequest().authenticated()
            )
            .addFilterBefore(internalApiKeyFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

## Comunicación Inter-Servicio (Feign Clients)

Los microservicios pueden comunicarse entre sí sin pasar por el API Gateway usando **Feign Clients** y endpoints protegidos por **X-Internal-API-Key**.

**Ejemplo: Order-Service valida cliente y producto**

```java
@FeignClient(name = "customer-service", url = "${app.services.customer-service.url}")
public interface CustomerServiceClient {
    @GetMapping("/internal/customers/{id}")
    CustomerResponse getCustomerById(
        @PathVariable Long id,
        @RequestHeader("X-Internal-API-Key") String apiKey
    );
}

@FeignClient(name = "product-service", url = "${app.services.product-service.url}")
public interface ProductServiceClient {
    @GetMapping("/internal/products/{id}")
    ProductResponse getProductById(
        @PathVariable Long id,
        @RequestHeader("X-Internal-API-Key") String apiKey
    );
}
```

**Configuración necesaria:**

```yaml
app:
  jwt:
    internal-api-key: nova-internal-service-key-2024
  services:
    customer-service:
      url: http://localhost:8084
    product-service:
      url: http://localhost:8083
```

**Endpoints internos requieren:**
- Header: `X-Internal-API-Key: nova-internal-service-key-2024`
- Validación en filtro `InternalApiKeyFilter` antes de llegar al endpoint
- Sin necesidad de JWT

## Monitoreo

Actuator endpoints disponibles:

- `http://localhost:8080/actuator/health` - Estado de salud
- `http://localhost:8080/actuator/info` - Información del servicio
- `http://localhost:8080/actuator/metrics` - Métricas

## Información de Servicios

### Auth Service (8081)
- **Rol**: Autenticación, generación de JWT
- **DB**: No tiene base de datos propia (usa User-Service vía Feign)
- **Dependencias**: Feign a User-Service para validar credenciales
- **Endpoints públicos**: `/api/auth/login`, `/api/auth/refresh`
- **Documentación**: [README-AUTH-SERVICE.md](../auth-service/README-AUTH-SERVICE.md)

### User Service (8082)
- **Rol**: Gestión de usuarios, roles, permisos
- **DB**: MongoDB (nova_db)
- **Endpoints públicos**: `/api/users/**`, `/api/roles/**`
- **Documentación**: [README-USER-SERVICE.md](../user-service/README-USER-SERVICE.md)

### Customer Service (8084)
- **Rol**: Gestión de clientes, niveles de fidelidad
- **DB**: MongoDB (nova_db)
- **Endpoints públicos**: `/api/customers/**`
- **Endpoints internos**: `/internal/customers/{id}` (Feign, X-Internal-API-Key)
- **Documentación**: [README-customer-service.md](../customer-service/README-customer-service.md)

### Product Service (8083)
- **Rol**: Catálogo de productos, inventario
- **DB**: MongoDB (nova_db)
- **Endpoints públicos**: `/api/products/**`
- **Endpoints internos**: `/internal/products/{id}` (Feign, X-Internal-API-Key)
- **Documentación**: [README-product-service.md](../product-service/README-product-service.md)

### Order Service (8085)
- **Rol**: Gestión de órdenes, validaciones, descuentos
- **DB**: MongoDB (nova_db)
- **Endpoints públicos**: `/api/orders/**`
- **Validaciones**: Cliente (vía Feign), Productos (vía Feign), Stock
- **Descuentos**: Strategy Pattern (ProductType, Loyalty, Season)
- **Documentación**: [README-order-service.md](../order-service/README-order-service.md)
