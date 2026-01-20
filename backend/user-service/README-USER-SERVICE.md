# User Service - Nova Commerce

Microservicio de gestión de usuarios, roles y permisos para Nova Commerce.

**Función principal:** Almacena y gestiona usuarios en MongoDB. Proporciona endpoints para CRUD de usuarios/roles y validación de credenciales vía endpoint interno que Auth-Service utiliza.

```
Cliente (credenciales)
  ↓
API Gateway (8080)
  ↓
Auth-Service (8081) ──(Feign)──→ User-Service (8082, MongoDB)
                                      ↑
                       /internal/users/validate
                        (validación interna)
  ↓ (retorna JWT)
API Gateway valida JWT localmente → Enruta a:
                                    ├─ Customer-Service (8084)
                                    ├─ Product-Service (8083)
                                    └─ Order-Service (8085)
```

## 📋 Requisitos

- Java 17+
- Maven 3.6+
- **MongoDB 7.0+** (base de datos obligatoria)
- IDE: IntelliJ IDEA o VS Code

## 🚀 Instalación y Configuración

### 1. Clonar el repositorio
```bash
git clone https://github.com/LeonardoPerezSoft/Nova-Commerce.git
cd backend/user-service
```

### 2. Configurar MongoDB

MongoDB puede ejecutarse localmente o usando Docker:

```bash
# Con Docker
docker run -d --name nova-mongodb \
  -p 27017:27017 \
  -e MONGO_INITDB_DATABASE=nova_db \
  mongo:7.0

# O instalar MongoDB localmente en tu sistema
# MongoDB creará automáticamente las colecciones al iniciar el servicio
```

### 3. Compilar y ejecutar

#### Limpiar compilaciones anteriores:
```bash
mvn clean
```

#### Compilar el proyecto:
```bash
mvn compile
```

#### Ejecutar la aplicación:
```bash
mvn spring-boot:run
```

O directamente:
```bash
java -jar target/user-service-0.0.1-SNAPSHOT.jar
```

### 4. Configurar variables de entorno (opcional)

```bash
# Base de datos MongoDB
SPRING_DATA_MONGODB_URI=mongodb://localhost:27017/nova_db

# Seed de datos iniciales
SEED_ENABLED=true
SEED_ADMIN_USERNAME=admin
SEED_ADMIN_EMAIL=admin@nova.com
SEED_ADMIN_PASSWORD=admin123

# OpenAPI
APP_NAME=User Service
APP_VERSION=1.0.0
```

**IMPORTANTE**: User-Service ahora valida tokens JWT y protege endpoints internos con API Key.

## 🛠️ Tecnologías

- **Spring Boot**: 3.4.3
- **Spring Data MongoDB**: Acceso a datos con MongoDB
- **Spring Security**: BCrypt para hash de contraseñas + JWT validation
- **MongoDB**: Base de datos NoSQL
- **Lombok**: Reducción de boilerplate
- **MapStruct**: 1.5.5.Final (mapeo de DTOs)
- **SpringDoc OpenAPI**: 2.7.0 (Swagger)
- **JJWT**: 0.12.5 (validación de tokens JWT)
- **Java**: 17
- **Arquitectura**: Clean Architecture (Hexagonal/Ports & Adapters)

## 📚 Documentación de API

Una vez que la aplicación esté corriendo:

- **Swagger UI**: http://localhost:8082/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8082/v3/api-docs

## 📁 Estructura del Proyecto (Clean Architecture)

```
user-service/
├── src/
│   ├── main/
│   │   ├── java/com/novacommerce/user_service/
│   │   │   ├── adapter/                    ✨ NUEVA - Adaptadores
│   │   │   │   ├── in/                     # Adaptadores de entrada
│   │   │   │   │   ├── filter/
│   │   │   │   │   │   ├── JwtAuthenticationFilter.java   # Filtro JWT
│   │   │   │   │   │   └── InternalApiKeyFilter.java      # Filtro API Key
│   │   │   │   │   └── web/
│   │   │   │   │       ├── UserRestController.java         ✨
│   │   │   │   │       ├── RoleRestController.java         ✨
│   │   │   │   │       └── InternalUserRestController.java ✨
│   │   │   │   └── out/                    # Adaptadores de salida
│   │   │   │       ├── jwt/
│   │   │   │       │   └── JwtTokenValidatorAdapter.java   ✨
│   │   │   │       ├── persistence/
│   │   │   │       │   ├── UserPersistenceAdapter.java     ✨
│   │   │   │       │   └── RolePersistenceAdapter.java     ✨
│   │   │   │       └── security/
│   │   │   │           └── PasswordEncoderAdapter.java     ✨
│   │   │   ├── application/                ✨ NUEVA - Lógica de negocio
│   │   │   │   ├── port/
│   │   │   │   │   ├── in/                 # Use Cases (puertos de entrada)
│   │   │   │   │   │   ├── ManageUsersUseCase.java         ✨
│   │   │   │   │   │   ├── ManageRolesUseCase.java         ✨
│   │   │   │   │   │   └── ValidateUserCredentialsUseCase.java ✨
│   │   │   │   │   └── out/                # Puertos de salida
│   │   │   │   │       ├── TokenValidatorPort.java         ✨
│   │   │   │   │       ├── UserPersistencePort.java        ✨
│   │   │   │   │       ├── RolePersistencePort.java        ✨
│   │   │   │   │       └── PasswordEncoderPort.java        ✨
│   │   │   │   └── service/                # Implementación de Use Cases
│   │   │   │       ├── UserService.java                    ✨
│   │   │   │       └── RoleService.java                    ✨
│   │   │   ├── config/                     # Configuraciones
│   │   │   │   ├── security/
│   │   │   │   │   └── SecurityConfig.java # JWT + API Key Security ✨ ACTUALIZADO
│   │   │   │   ├── DataInitializer.java
│   │   │   │   ├── SeedProperties.java
│   │   │   │   ├── OpenApiConfig.java
│   │   │   │   └── MapperConfig.java
│   │   │   ├── domain/                     # Dominio (entidades + excepciones)
│   │   │   │   ├── model/
│   │   │   │   │   ├── User.java           # Documento MongoDB
│   │   │   │   │   ├── Role.java           # Documento MongoDB
│   │   │   │   │   ├── Permission.java     # Documento MongoDB
│   │   │   │   │   └── Token.java          ✨ NUEVO - Modelo dominio JWT
│   │   │   │   ├── enums/
│   │   │   │   │   └── UserStatusEnum.java
│   │   │   │   └── exception/
│   │   │   │       └── SecurityException.java  ✨ NUEVA
│   │   │   ├── repository/                 # Spring Data MongoDB
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── RoleRepository.java
│   │   │   │   └── PermissionRepository.java
│   │   │   ├── service/                    ❌ LEGACY (mantener por compatibilidad)
│   │   │   │   ├── interfaces/
│   │   │   │   │   ├── IUserService.java   ❌
│   │   │   │   │   └── IRoleService.java   ❌
│   │   │   │   ├── impl/
│   │   │   │   │   ├── UserServiceImpl.java ❌
│   │   │   │   │   └── RoleServiceImpl.java ❌
│   │   │   │   └── mapper/                 # MapStruct mappers
│   │   │   │       ├── UserMapper.java
│   │   │   │       ├── RoleMapper.java
│   │   │   │       └── PermissionMapper.java
│   │   │   └── web/                        ❌ LEGACY
│   │   │       ├── api/dto/                # DTOs (Request/Response)
│   │   │       │   ├── request/
│   │   │       │   └── response/
│   │   │       └── rest/
│   │   │           ├── controller/         ❌
│   │   │           │   ├── UserController.java       ❌
│   │   │           │   ├── RoleController.java       ❌
│   │   │           │   └── InternalUserController.java ❌
│   │   │           └── exceptions/
│   │   │               └── GlobalExceptionHandler.java
│   │   └── resources/
│   │       ├── application.yaml            ✨ ACTUALIZADO - JWT config
│   │       ├── banner.txt
│   │       └── (sin migraciones - MongoDB usa colecciones dinámicas)
│   │           ├── changelog-master.yaml
│   │           └── changes/
│   │               ├── 001-create-permissions-table.yaml
│   │               ├── 002-create-roles-table.yaml
│   │               ├── 003-create-users-table.yaml
│   │               ├── 004-create-role-permission-table.yaml
│   │               └── 005-create-user-role-table.yaml
│   └── test/
├── pom.xml                                 ✨ ACTUALIZADO - JWT dependency
└── README-USER-SERVICE.md

✨ NUEVA = Componente nuevo con Clean Architecture
❌ LEGACY = Componente antiguo (eliminar después de migración completa)
```

### 🏗️ Arquitectura Hexagonal (Puertos y Adaptadores)

| Capa | Responsabilidad | Componentes |
|------|----------------|-------------|
| **application** | Casos de uso y reglas de negocio | `UserService`, `RoleService` implementan use cases |
| **application/port/in** | Contratos de casos de uso | `ManageUsersUseCase`, `ValidateUserCredentialsUseCase` |
| **application/port/out** | Contratos de infraestructura | `TokenValidatorPort`, `UserPersistencePort`, `PasswordEncoderPort` |
| **adapter/in** | Adaptadores de entrada (HTTP, filtros) | `UserRestController`, `JwtAuthenticationFilter`, `InternalApiKeyFilter` |
| **adapter/out** | Adaptadores de salida (BD, JWT, security) | `UserPersistenceAdapter`, `JwtTokenValidatorAdapter`, `PasswordEncoderAdapter` |
| **domain** | Entidades y lógica de dominio | `User`, `Role`, `Permission`, `Token`, `SecurityException` |

### 🔐 Seguridad Implementada

1. **Validación JWT**: Todos los endpoints `/api/**` requieren token JWT válido
2. **Protección API Key**: Endpoint `/internal/users/validate` requiere header `X-Internal-API-Key`
3. **Method Security**: `@PreAuthorize` habilitado para control granular de permisos
4. **BCrypt**: Contraseñas hasheadas con strength 12

### 📋 Configuración JWT

```yaml
app:
  jwt:
    secret: ${JWT_SECRET:NovaCommerceSecretKeyForJWTTokenValidation2024MustBeLongEnoughForHS512Algorithm}
    internal-api-key: ${INTERNAL_API_KEY:nova-internal-service-key-2024}
```

**IMPORTANTE**: En producción, configurar variables de entorno:
```bash
export JWT_SECRET="tu-clave-secreta-super-larga-y-segura"
export INTERNAL_API_KEY="clave-unica-para-comunicacion-interna"
```

## 🗄️ Base de Datos

### Colecciones MongoDB

Las colecciones se crean automáticamente al iniciar el servicio:
- **users**: Usuarios del sistema
- **roles**: Roles disponibles
- **permissions**: Permisos del sistema

El servicio incluye seed de datos iniciales si está habilitado (SEED_ENABLED=true).

### Esquema de Tablas

#### permissions
- `id` (UUID, PK)
- `name` (VARCHAR, UNIQUE) - Ej: USER_READ, ROLE_CREATE
- `description` (VARCHAR)

#### roles
- `id` (UUID, PK)
- `name` (VARCHAR, UNIQUE) - Ej: ADMIN, USER
- `description` (VARCHAR)

#### users
- `id` (UUID, PK)
- `username` (VARCHAR, UNIQUE)
- `email` (VARCHAR, UNIQUE)
- `password` (VARCHAR) - Hash BCrypt
- `status` (VARCHAR) - ACTIVE, INACTIVE, SUSPENDED, DELETED
- `enabled` (BOOLEAN)
- `locked` (BOOLEAN)
- `last_login` (TIMESTAMP)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)

#### role_permission (Many-to-Many)
- `role_id` (UUID, FK → roles)
- `permission_id` (UUID, FK → permissions)

#### user_role (Many-to-Many)
- `user_id` (UUID, FK → users)
- `role_id` (UUID, FK → roles)

## 📊 Seed de Datos Iniciales

Al iniciar con `SEED_ENABLED=true`, se crean automáticamente:

### Permisos (9 permisos)
- **Usuarios**: USER_READ, USER_CREATE, USER_UPDATE, USER_DELETE
- **Roles**: ROLE_READ, ROLE_CREATE, ROLE_UPDATE, ROLE_DELETE
- **Auth**: AUTH_VALIDATE

### Roles (3 roles predefinidos)
- **ADMIN**: Todos los permisos
- **SALES**: USER_READ
- **USER**: Sin permisos especiales

### Usuario administrador
- Username: `admin`
- Email: `admin@nova.com`
- Password: `admin123` (hash BCrypt almacenado)
- Rol: ADMIN
- Status: ACTIVE

## 🔐 Seguridad

### Arquitectura de Confianza

User-Service **NO valida JWT**. Confía completamente en API Gateway:

```java
@Configuration
@EnableWebSecurity
// @EnableMethodSecurity comentado - Gateway maneja autorización
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/internal/**").permitAll()  // Feign interno
                .requestMatchers("/api/**").permitAll()       // Confianza en gateway
                .requestMatchers("/swagger-ui/**").permitAll()
                .anyRequest().authenticated()
            )
            .build();
    }
}
```

### Password Hashing

- **BCrypt** con strength 12
- Contraseñas nunca se almacenan en texto plano
- Hash generado automáticamente al crear/actualizar usuario

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
}
```

## 📝 Endpoints

### 👥 API Pública (vía API Gateway)

#### Users - `/api/users`
| Método | Endpoint | Descripción | Auth Requerida |
|--------|----------|-------------|----------------|
| GET | `/api/users` | Listar usuarios (paginado) | Sí - Gateway valida |
| GET | `/api/users/{id}` | Obtener usuario por ID | Sí - Gateway valida |
| POST | `/api/users` | Crear nuevo usuario | Sí - Gateway valida |
| PUT | `/api/users/{id}` | Actualizar usuario | Sí - Gateway valida |
| DELETE | `/api/users/{id}` | Eliminar usuario | Sí - Gateway valida |

#### Roles - `/api/roles`
| Método | Endpoint | Descripción | Auth Requerida |
|--------|----------|-------------|----------------|
| GET | `/api/roles` | Listar roles (paginado) | Sí - Gateway valida |
| GET | `/api/roles/{id}` | Obtener rol por ID | Sí - Gateway valida |
| POST | `/api/roles` | Crear nuevo rol | Sí - Gateway valida |
| PUT | `/api/roles/{id}` | Actualizar rol | Sí - Gateway valida |
| DELETE | `/api/roles/{id}` | Eliminar rol | Sí - Gateway valida |

**IMPORTANTE:** Estos endpoints se acceden vía API Gateway (puerto 8080):
- `http://localhost:8080/api/users` ✅
- ~~`http://localhost:8082/api/users`~~ ❌ (acceso directo bloqueado en producción)

### 🔒 API Interna (solo para microservicios)

#### Internal Users - `/internal/users`
| Método | Endpoint | Descripción | Llamado por |
|--------|----------|-------------|-------------|
| POST | `/internal/users/validate` | Validar credenciales de usuario | Auth-Service (Feign) |

**Request:**
```json
{
  "userIdentifier": "admin",
  "password": "admin123"
}
```

**Response (credenciales válidas):**
```json
{
  "valid": true,
  "userId": "04938a8a-853a-4ac9-aea3-3c7af779022d",
  "username": "admin",
  "email": "admin@nexatec.com",
  "roles": ["ADMIN"],
  "permissions": ["USER_READ", "USER_CREATE", ...]
}
```

**Response (credenciales inválidas):**
```json
{
  "valid": false
}
```

**IMPORTANTE:** Este endpoint NO debe ser accesible públicamente. Solo para comunicación interna entre microservicios.

## 🧪 Pruebas

### Ejecutar tests
```bash
mvn test
```

### Probar endpoints (vía API Gateway)

#### 1. Login (genera JWT)
```bash
curl -X POST http://localhost:8080/api/auth/login \
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
  "refresh_token": "...",
  "token_type": "Bearer",
  "expires_in": 86400,
  "username": "admin",
  "roles": ["ADMIN"],
  "permissions": [...]
}
```

#### 2. Listar usuarios (con JWT)
```bash
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer <access_token>"
```

#### 3. Crear usuario
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Authorization: Bearer <access_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "nuevo_usuario",
    "email": "nuevo@nexatec.com",
    "password": "Password123!",
    "roleIds": ["<uuid-del-rol-USER>"]
  }'
```

## 🔍 Troubleshooting

### Error: "Cannot connect to database"
```bash
# Verificar que MongoDB esté corriendo
sudo systemctl status mongod   # Linux
# O si usas Docker:
docker ps | grep mongo

# Verificar URI de conexión en application.yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/nova_db
```

### Error: "Port 8082 already in use"
```bash
# Cambiar el puerto en application.yaml
server:
  port: 8083
```

### Error: "MongoDB connection failed"
```bash
# Verificar que MongoDB esté activo
mongosh --eval "db.adminCommand('ping')"

# Si usas Docker, verificar el contenedor:
docker logs nova-mongodb

# Si necesitas recrear la base de datos, simplemente elimina las colecciones:
mongosh nova_db --eval "db.dropDatabase()"
```

### Error: "release version 17 not supported"
```powershell
# Configurar JAVA_HOME en PowerShell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"

# Verificar
java -version

# Limpiar y compilar
mvn clean compile
```

### Error: "Auth-Service can't connect to User-Service"
```bash
# Verificar que user-service esté corriendo
curl http://localhost:8082/actuator/health

# Revisar configuración de Feign en auth-service
user-service:
  url: http://localhost:8082
```

## ⚙️ Configuración de Perfiles

### Desarrollo (local)
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
```

### Producción
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"
```

## 🔗 Integración con otros servicios

### Auth-Service (OpenFeign)

Auth-Service se conecta a user-service vía Feign Client:

```java
@FeignClient(name = "user-service", url = "${user-service.url}")
public interface UserServiceClient {
    @PostMapping("/internal/users/validate")
    InternalUserValidationResponse validateCredentials(
        @RequestBody InternalUserValidationRequest request
    );
}
```

**Flujo de login:**
1. Cliente → `POST /api/auth/login` (Gateway → Auth-Service)
2. Auth-Service → `POST /internal/users/validate` (Feign → User-Service)
3. User-Service valida credenciales contra MongoDB
4. User-Service retorna datos del usuario (roles, permisos)
5. Auth-Service genera JWT con esa información
6. Cliente recibe JWT

### API Gateway

- Gateway enruta `/api/users/**` y `/api/roles/**` a user-service
- Gateway valida JWT antes de enviar peticiones
- User-service confía en que gateway ya validó el token

## 📊 Monitoreo

Actuator endpoints disponibles:

- `http://localhost:8082/actuator/health` - Estado de salud
- `http://localhost:8082/actuator/info` - Información del servicio
- `http://localhost:8082/actuator/metrics` - Métricas

## 🤝 Contribuir

1. Fork el proyecto
2. Crear rama para la feature (`git checkout -b feature/AmazingFeature`)
3. Commit cambios (`git commit -m 'Add AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir Pull Request

## 📄 Licencia

Este proyecto es propiedad intelectual de Nova Commerce.

## 📞 Soporte

Para soporte y preguntas:
- Email: yesid.perez@sofka.com.co
- Web: https://www.novacommerce.com

---

Desarrollado con ❤️ por **Leonardo Pérez**
