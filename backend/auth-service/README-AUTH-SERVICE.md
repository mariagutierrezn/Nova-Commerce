# Auth Service - Nova Commerce

Microservicio de autenticación y autorización centralizada para Nova Commerce.

**Arquitectura:** Auth-Service NO maneja base de datos. Utiliza Feign Client para validar credenciales con User-Service y genera tokens JWT que protegen toda la plataforma.

Implementa **Clean Architecture** con separación clara entre dominio, aplicación y adaptadores.

```
Cliente → API Gateway (8080) → Auth-Service (8081) → (Feign) → User-Service (8082, MongoDB)
                                   ↓ Genera JWT
                           Retorna al API Gateway
                                   ↓
                    JWT protege acceso a:
                    ├─ Customer-Service (8084)
                    ├─ Product-Service (8083)
                    └─ Order-Service (8085)
```

## 📋 Requisitos

- Java 17+
- Maven 3.6+
- **User-Service** corriendo en puerto 8082 (dependencia obligatoria)
- IDE: IntelliJ IDEA o VS Code
- **NO requiere** base de datos propia

## 🚀 Instalación y Configuración

### 1. Clonar el repositorio
```bash
git clone https://github.com/LeonardoPerezSoft/Nova-Commerce.git
cd backend/auth-service
```

### 2. Verificar que User-Service esté corriendo
```bash
# User-Service debe estar activo en http://localhost:8082
# Auth-Service usa Feign Client para comunicarse con él
```

### 3. Compilar y ejecutar

#### Compilar el proyecto:
```bash
mvnw clean compile
```

#### Ejecutar la aplicación:
```bash
mvnw spring-boot:run
```

### 4. Configurar variables de entorno (opcional)

```bash
# Comunicación con User-Service
USER_SERVICE_URL=http://localhost:8082

# JWT (debe coincidir con API Gateway)
JWT_SECRET=E8F7D6C4B5A3F2E1D9C8B7A6F5E4D3C2B1A0F9E8D7C6B5A4F3E2D1C0B9A8F7E6D5C4B3A2F1E0D9C8B7A6F5E4D3C2B1A0F9E8D7C6B5A4
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000
```

**IMPORTANTE:** El `JWT_SECRET` debe ser idéntico en auth-service y api-gateway.

## 🛠️ Tecnologías

- **Spring Boot**: 3.4.3
- **Spring Cloud**: 2024.0.0
- **Spring Cloud OpenFeign**: 4.3.0 (comunicación con user-service)
- **Spring Security**: Para generación de JWT
- **JWT (JJWT)**: 0.11.5
- **Lombok**: Reducción de boilerplate
- **MapStruct**: 1.5.5.Final (mapeo de DTOs)
- **SpringDoc OpenAPI**: 2.7.0 (Swagger)
- **Java**: 17

## 📚 Documentación de API

Una vez que la aplicación esté corriendo:

- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8081/v3/api-docs

## 📁 Estructura del Proyecto - Clean Architecture

```
com.novacommerce.auth_service/
│
├── domain/                          ← CAPA DE DOMINIO
│   ├── model/                      # Modelos de dominio (sin cambios)
│   └── enums/                      # Enumeraciones de negocio
│
├── application/                     ← CAPA DE APLICACIÓN ✨ NUEVA
│   ├── port/
│   │   ├── in/                     # Puertos de entrada (use cases)
│   │   │   ├── AuthenticateUserUseCase.java
│   │   │   ├── RefreshTokenUseCase.java
│   │   │   └── ValidateTokenUseCase.java
│   │   └── out/                    # Puertos de salida (interfaces)
│   │       ├── TokenGeneratorPort.java
│   │       └── UserValidationPort.java
│   └── service/
│       └── AuthService.java        # Implementa los use cases
│
├── adapter/                         ← CAPA DE ADAPTADORES ✨ NUEVA
│   ├── in/
│   │   └── web/
│   │       └── AuthRestController.java  # Controller REST (HTTP adapter)
│   └── out/
│       ├── jwt/
│       │   └── JwtTokenAdapter.java     # Implementa TokenGeneratorPort
│       └── feign/
│           └── UserServiceAdapter.java  # Implementa UserValidationPort
│
├── client/                          ← FEIGN CLIENTS
│   ├── UserServiceClient.java      # Feign interface
│   ├── dto/                        # DTOs de comunicación
│   ├── feign/                      # Configuración Feign
│   └── model/                      # Modelos cliente
│
├── config/                          ← CONFIGURACIÓN
│   ├── security/                   # Security Config, JWT Filter, JwtConstants
│   │   ├── SecurityConfig.java
│   │   └── jwt/
│   │       ├── JwtAuthenticationFilter.java
│   │       ├── JwtTokenProvider.java    # LEGACY - usar JwtTokenAdapter
│   │       └── JwtConstants.java
│   ├── OpenApiConfig.java
│   └── MapperConfig.java
│
├── service/                         ← LEGACY (mantener temporalmente)
│   ├── interfaces/
│   │   └── IAuthService.java       # ANTIGUO - migrar a use cases
│   └── impl/
│       └── AuthServiceImpl.java    # ANTIGUO - ya refactorizado a AuthService
│
├── web/
│   ├── api/dto/                    # DTOs de API REST
│   │   ├── request/                # LoginRequest, RefreshTokenRequest
│   │   └── response/               # LoginResponse, TokenValidationResponse
│   └── rest/
│       ├── controller/
│       │   └── AuthController.java # ANTIGUO - migrar a AuthRestController
│       └── exceptions/             # GlobalExceptionHandler, excepciones custom
│
└── AuthServiceApplication.java     # Main @EnableFeignClients ✅
```

### 📋 Componentes Clave por Capa

#### **Capa de Aplicación (Nueva)**
| Componente | Responsabilidad |
|------------|-----------------|
| `AuthenticateUserUseCase` | Define contrato de login |
| `RefreshTokenUseCase` | Define contrato de refresh token |
| `ValidateTokenUseCase` | Define contrato de validación |
| `TokenGeneratorPort` | Interface para generar/validar JWT |
| `UserValidationPort` | Interface para validar usuarios |
| `AuthService` | Orquesta use cases usando puertos |

#### **Capa de Adaptadores (Nueva)**
| Componente | Responsabilidad |
|------------|-----------------|
| `AuthRestController` | Expone endpoints HTTP REST |
| `JwtTokenAdapter` | Implementa generación JWT usando JJWT |
| `UserServiceAdapter` | Implementa validación vía Feign |

#### **Archivos Legacy (Eliminar después de migración)**
- ❌ `service/impl/AuthServiceImpl.java` → Reemplazado por `application/service/AuthService.java`
- ❌ `service/interfaces/IAuthService.java` → Reemplazado por use cases
- ❌ `web/rest/controller/AuthController.java` → Reemplazado por `adapter/in/web/AuthRestController.java`
- ⚠️ `config/security/jwt/JwtTokenProvider.java` → Funcionalidad en `JwtTokenAdapter`

### Explicación de Clean Architecture

#### **Domain Layer (Dominio)**
- Contiene modelos de negocio y enums
- Independiente de frameworks
- Sin dependencias externas

#### **Application Layer (Aplicación)**
- Define los puertos (interfaces):
  - **IN** (Use Cases): `AuthenticateUserUseCase`, `RefreshTokenUseCase`, `ValidateTokenUseCase`
  - **OUT**: `TokenGeneratorPort`, `UserValidationPort`
- `AuthService`: implementa los use cases orquestando los puertos

#### **Adapter Layer (Adaptadores)**
- **IN (Entrada)**: `AuthRestController` - expone endpoints HTTP
- **OUT (Salida)**:
  - `JwtTokenAdapter`: genera y valida tokens JWT
  - `UserServiceAdapter`: se comunica con user-service vía Feign

#### **Ventajas de esta Arquitectura**

✅ **Independencia de Framework**: Lógica de negocio sin Spring  
✅ **Testabilidad**: Fácil crear mocks de puertos  
✅ **Mantenibilidad**: Responsabilidades claramente separadas  
✅ **Extensibilidad**: Cambiar Feign por REST Template es trivial  

## 🔐 Autenticación

### Flujo de autenticación (Clean Architecture):

```
HTTP Request (POST /api/auth/login)
    ↓
AuthRestController (adapter/in/web)
    ↓ (implementa)
AuthenticateUserUseCase (application/port/in)
    ↓ (implementado por)
AuthService (application/service)
    ├─→ UserValidationPort (application/port/out)
    │       ↓ (implementado por)
    │   UserServiceAdapter (adapter/out/feign)
    │       ↓ (usa)
    │   UserServiceClient (Feign)
    │       ↓
    │   User-Service (http://localhost:8082)
    │       ↓ Valida credenciales contra MongoDB
    │       ↓ Retorna usuario, roles, permisos
    │
    └─→ TokenGeneratorPort (application/port/out)
            ↓ (implementado por)
        JwtTokenAdapter (adapter/out/jwt)
            ↓
        Genera JWT
            ↓
    Retorna a Cliente
        ↓
    Cliente → API Gateway (8080)
            ↓ Valida JWT localmente
            ↓ Inyecta headers (X-Username, X-Authorities)
            ↓
        Enruta a servicios backend:
        ├─ Customer-Service (8084)
        ├─ Product-Service (8083)
        └─ Order-Service (8085)
```

### JWT en la Plataforma

El JWT generado por **Auth-Service** protege acceso a TODOS los microservicios:

1. **Auth-Service** (8081) - **Genera JWT**
2. **API Gateway** (8080) - **Valida JWT localmente** sin latencia
3. **Servicios Backend**:
   - Customer-Service (8084) - Confía en el gateway
   - Product-Service (8083) - Confía en el gateway
   - Order-Service (8085) - Confía en el gateway
   - User-Service (8082) - Confía en el gateway

**Ventaja:** JWT se valida una sola vez en el gateway, no en cada servicio.

### Endpoints

1. **Login**: `POST /api/auth/login`
   ```json
   {
     "userIdentifier": "admin",
     "password": "admin123"
   }
   ```
   
   **Nota:** Auth-service envía estas credenciales a user-service vía Feign.

2. **Respuesta**:
   ```json
   {
     "access_token": "eyJhbGciOiJIUzUxMiJ9...",
     "refresh_token": "eyJhbGciOiJIUzUxMiJ9...",
     "token_type": "Bearer",
     "expires_in": 86400
   }
   ```

3. **Usar token en requests**:
   ```
   Authorization: Bearer <access_token>
   ```

## 📁 Estructura del Proyecto

```
auth-service/
├── src/
│   ├── main/
│   │   ├── java/com/novacommerce/auth_serviceo/
│   │   │   ├── client/              # Feign Clients
│   │   │   │   └── UserServiceClient.java    # Comunicación con user-service
│   │   │   ├── config/              # Configuraciones
│   │   │   │   ├── security/        # Security, JWT
│   │   │   │   ├── OpenApiConfig.java
│   │   │   │   └── MapperConfig.java
│   │   │   ├── service/             # Servicios de negocio
│   │   │   │   ├── interfaces/      # IAuthService
│   │   │   │   └── impl/            # AuthServiceImpl
│   │   │   └── web/                 # Controllers REST
│   │   │       ├── api/dto/         # DTOs (Request/Response)
│   │   │       │   ├── request/     # LoginRequest, RefreshTokenRequest
│   │   │       │   └── response/    # LoginResponse, TokenValidationResponse
│   │   │       └── rest/
│   │   │           ├── controller/  # AuthController
│   │   │           └── exceptions/  # GlobalExceptionHandler
│   │   └── resources/
│   │       ├── application.yml      # Configuración (puerto 8081, Feign, JWT)
│   │       └── banner.txt
│   └── test/                        # Tests unitarios
├── pom.xml                          # Spring Boot 3.4.3, Spring Cloud 2024.0.0, Feign
└── README.md
```

### Componentes Clave

- **UserServiceClient**: Feign Client para comunicación con user-service (`/internal/users/validate`)
- **AuthServiceImpl**: Lógica de autenticación (valida con Feign, genera JWT)
- **AuthController**: Endpoints REST (`/api/auth/login`, `/refresh`, `/validate`)
- **JwtTokenProvider**: Generación y validación de tokens JWT
- **NO tiene**: Documentos MongoDB, Repositorios (user-service maneja la BD)

## � Integración con User-Service

Auth-Service se comunica con User-Service vía **OpenFeign**:

### Endpoint Interno de Validación

```java
@FeignClient(name = "user-service", url = "${user-service.url}")
public interface UserServiceClient {
    @PostMapping("/internal/users/validate")
    InternalUserValidationResponse validateCredentials(
        @RequestBody InternalUserValidationRequest request
    );
}
```

**Flujo de autenticación:**

1. Cliente envía credenciales a `POST /api/auth/login`
2. Auth-Service llama a `user-service` vía Feign: `POST /internal/users/validate`
3. User-Service valida credenciales contra MongoDB
4. User-Service retorna información del usuario (id, username, roles, permissions)
5. Auth-Service genera JWT con esa información
6. Auth-Service retorna JWT al cliente

### Configuración Feign

```yaml
user-service:
  url: http://localhost:8082

feign:
  client:
    config:
      default:
        connectTimeout: 5000
        readTimeout: 5000
        loggerLevel: basic
```

## 🧪 Pruebas

Ejecutar tests:
```bash
mvn test
```

## � Usuarios de Prueba

Auth-Service **NO crea usuarios**. Los usuarios están en **user-service**.

### Usuario administrador (creado por user-service)

Para probar auth-service, usa las credenciales que user-service creó:

- Username: `admin`
- Email: `admin@nova.com`
- Password: `admin123` (verifica en user-service)

**Nota:** Si cambias el seed de user-service, actualiza las credenciales aquí.

## ⚙️ Configuración de Perfiles

### Desarrollo
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### Producción
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"
```

## 🔍 Troubleshooting

### Error: "release version 17 not supported"

Este error ocurre cuando Maven no puede encontrar Java 17 en el PATH.

**Solución para Windows:**

1. Configurar JAVA_HOME en PowerShell:
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
```

2. Verificar la versión de Java:
```powershell
java -version
```

3. Limpiar y compilar:
```powershell
mvn clean compile
mvn package -DskipTests
```

**Solución permanente (Windows):**

1. Ir a: Control Panel → System → Advanced System Settings
2. Click en "Environment Variables"
3. Crear nueva variable:
   - Variable name: `JAVA_HOME`
   - Variable value: `C:\Program Files\Java\jdk-17`
4. Hacer click OK y reiniciar PowerShell o IDE

### Error: "No se ha encontrado la clase principal"
```bash
# Limpiar y recompilar
mvn clean compile

# O reconstruir todo
mvn clean install
```

### Error: "Connection refused: user-service"
```bash
# Auth-service requiere que user-service esté corriendo
# Verificar que user-service esté activo en http://localhost:8082

# Probar manualmente:
curl http://localhost:8082/actuator/health
```

### Error: "Port 8081 already in use"
```bash
# Cambiar el puerto en application.yml
server:
  port: 8082
```

## 📝 Endpoints

### 🔐 Authentication
Auth-Service solo expone endpoints de autenticación JWT

| Método | Endpoint | Descripción | Autenticación |
|--------|----------|-------------|---------------|
| POST | `/api/auth/login` | Login de usuario (valida con user-service vía Feign) | No |
| POST | `/api/auth/refresh` | Refrescar access token usando refresh token | No |
| GET | `/api/auth/validate` | Validar si un JWT es válido | Sí (Bearer token) |

### 📋 Ejemplos de uso

#### Login
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "userIdentifier": "admin",
    "password": "admin123"
  }'
```

**Respuesta:**
```json
{
  "access_token": "eyJhbGciOiJIUzUxMiJ9...",
  "refresh_token": "eyJhbGciOiJIUzUxMiJ9...",
  "token_type": "Bearer",
  "expires_in": 86400,
  "username": "admin",
  "roles": ["ADMIN"],
  "permissions": ["USER_READ", "USER_CREATE", ...]
}
```

#### Refresh Token
```bash
curl -X POST http://localhost:8081/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "eyJhbGciOiJIUzUxMiJ9..."
  }'
```

#### Validar Token
```bash
curl -X GET http://localhost:8081/api/auth/validate \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

**Nota:** Los endpoints de gestión de usuarios y roles (`/api/users`, `/api/roles`) están en **user-service** (puerto 8082), no en auth-service.

## 🤝 Contribuir

1. Fork el proyecto
2. Crear rama para la feature (`git checkout -b feature/AmazingFeature`)
3. Commit cambios (`git commit -m 'Add AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir Pull Request

## 📄 Licencia

Este proyecto es propiedad intelectual de NovaCommerce.

## 📞 Soporte

Para soporte y preguntas:
- Email: yesid.perez@sofka.com.co
- Web: https://www.novacommerce.com

---

Desarrollado con ❤️ por **Leonardo Pérez**
