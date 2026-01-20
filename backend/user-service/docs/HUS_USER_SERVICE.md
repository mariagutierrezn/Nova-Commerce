
# Historias de Usuario - User Service (NovaCommerce)

## 📋 Épica Global
**EP-GLOBAL-001** - Plataforma de E-commerce Modular y Escalable

## 👤 Épica User Service
**EP-USR-001** - Gestión de Usuarios, Roles y Permisos

---

## Feature FT-USR-001 - Gestión de Usuarios

### US-USR-001: Crear usuario válido con roles asignados

**Descripción:**  
Como administrador del sistema, Quiero crear nuevos usuarios con roles específicos, Para gestionar el acceso administrativo a la plataforma.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un usuario con rol ADMIN autenticado |
| **Cuando** | Se invoca POST /api/users con datos válidos (username, email, password, roleIds) |
| **Entonces** | El usuario se crea con la contraseña encriptada (BCrypt), retornando HTTP 201 con los datos del usuario |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-USR-001 - Gestión de Usuarios
- **Épica:** EP-USR-001 - Gestión de Usuarios, Roles y Permisos
- **Dependencias:** Ninguna
- **Versión/Release:** 1.0

---

### US-USR-002: Rechazar creación de usuario con username duplicado

**Descripción:**  
Como sistema de seguridad, Quiero validar la unicidad del username, Para evitar conflictos en la autenticación.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un username que ya existe en el sistema |
| **Cuando** | Se intenta crear un nuevo usuario con ese username |
| **Entonces** | Se rechaza con excepción DuplicateResourceException y código HTTP 409 Conflict |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-USR-001
- **Épica:** EP-USR-001
- **Dependencias:** US-USR-001
- **Versión/Release:** 1.0

---

### US-USR-003: Rechazar creación de usuario con email duplicado

**Descripción:**  
Como sistema de seguridad, Quiero validar la unicidad del email, Para mantener la integridad de las notificaciones y recuperación de cuenta.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un email que ya está registrado en el sistema |
| **Cuando** | Se intenta crear un nuevo usuario con ese email |
| **Entonces** | Se rechaza con excepción DuplicateResourceException y código HTTP 409 Conflict |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-USR-001
- **Épica:** EP-USR-001
- **Dependencias:** US-USR-001
- **Versión/Release:** 1.0

---

### US-USR-004: Actualizar usuario existente

**Descripción:**  
Como administrador o usuario propietario, Quiero actualizar los datos de un usuario, Para mantener la información actualizada.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un usuario autenticado con permisos (ADMIN o el mismo usuario) |
| **Cuando** | Se invoca PUT /api/users/{id} con datos actualizados |
| **Entonces** | El usuario se actualiza, la contraseña se vuelve a encriptar si cambió, y retorna HTTP 200 con los datos actualizados |

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-USR-001
- **Épica:** EP-USR-001
- **Dependencias:** US-USR-001
- **Versión/Release:** 1.0

---

### US-USR-005: Eliminar usuario por ID

**Descripción:**  
Como administrador del sistema, Quiero eliminar usuarios, Para gestionar cuentas inactivas o comprometidas.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un usuario con rol ADMIN autenticado |
| **Cuando** | Se invoca DELETE /api/users/{id} con un ID válido |
| **Entonces** | El usuario se elimina de la base de datos y retorna HTTP 204 No Content |

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-USR-001
- **Épica:** EP-USR-001
- **Dependencias:** US-USR-001
- **Versión/Release:** 1.0

---

### US-USR-006: Rechazar eliminación de usuario inexistente

**Descripción:**  
Como sistema, Quiero notificar cuando se intenta eliminar un usuario que no existe, Para informar correctamente al administrador.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un userId que no existe en el sistema |
| **Cuando** | Se invoca DELETE /api/users/{id} |
| **Entonces** | Se rechaza con ResourceNotFoundException y código HTTP 404 Not Found |

**Metadatos:**
- **Prioridad:** Baja
- **Feature:** FT-USR-001
- **Épica:** EP-USR-001
- **Dependencias:** US-USR-005
- **Versión/Release:** 1.0

---

### US-USR-007: Listar usuarios con paginación

**Descripción:**  
Como administrador, Quiero listar todos los usuarios del sistema con paginación, Para gestionar eficientemente grandes volúmenes de datos.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un usuario con rol ADMIN autenticado |
| **Cuando** | Se invoca GET /api/users con parámetros de paginación (page, size, sort) |
| **Entonces** | Retorna una Page<UserResponse> con los usuarios paginados y código HTTP 200 |

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-USR-001
- **Épica:** EP-USR-001
- **Dependencias:** US-USR-001
- **Versión/Release:** 1.0

---

### US-USR-008: Obtener usuario por ID

**Descripción:**  
Como administrador o usuario propietario, Quiero consultar los detalles de un usuario específico, Para visualizar su información completa.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un usuario autenticado con permisos (ADMIN o el mismo usuario) |
| **Cuando** | Se invoca GET /api/users/{id} con un ID válido |
| **Entonces** | Retorna UserResponse con todos los datos del usuario excepto la contraseña y código HTTP 200 |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-USR-001
- **Épica:** EP-USR-001
- **Dependencias:** US-USR-001
- **Versión/Release:** 1.0

---

## Feature FT-USR-002 - Gestión de Roles

### US-USR-009: Crear rol con permisos asociados

**Descripción:**  
Como administrador del sistema, Quiero crear roles con conjuntos específicos de permisos, Para definir niveles de acceso personalizados.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un usuario con rol ADMIN autenticado y permisos existentes en el sistema |
| **Cuando** | Se invoca POST /api/roles con nombre, descripción y conjunto de permissionIds |
| **Entonces** | El rol se crea asociado a los permisos especificados, retornando HTTP 201 con RoleResponse |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-USR-002 - Gestión de Roles
- **Épica:** EP-USR-001
- **Dependencias:** FT-USR-003 (Permisos deben existir)
- **Versión/Release:** 1.0

---

### US-USR-010: Rechazar creación de rol con nombre duplicado

**Descripción:**  
Como sistema de seguridad, Quiero validar la unicidad del nombre del rol, Para evitar confusiones en la asignación de permisos.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un nombre de rol que ya existe (ej: "ADMIN") |
| **Cuando** | Se intenta crear un rol con ese nombre |
| **Entonces** | Se rechaza con DuplicateResourceException y código HTTP 409 Conflict |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-USR-002
- **Épica:** EP-USR-001
- **Dependencias:** US-USR-009
- **Versión/Release:** 1.0

---

### US-USR-011: Listar todos los roles

**Descripción:**  
Como administrador, Quiero visualizar todos los roles del sistema, Para gestionar la estructura de permisos.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un usuario con rol ADMIN autenticado |
| **Cuando** | Se invoca GET /api/roles |
| **Entonces** | Retorna List<RoleResponse> con todos los roles y sus permisos asociados, código HTTP 200 |

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-USR-002
- **Épica:** EP-USR-001
- **Dependencias:** US-USR-009
- **Versión/Release:** 1.0

---

### US-USR-012: Obtener rol por ID

**Descripción:**  
Como administrador, Quiero consultar un rol específico con sus permisos, Para entender el alcance de ese nivel de acceso.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un roleId válido existente en el sistema |
| **Cuando** | Se invoca GET /api/roles/{id} |
| **Entonces** | Retorna RoleResponse con el rol y su conjunto de permisos, código HTTP 200 |

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-USR-002
- **Épica:** EP-USR-001
- **Dependencias:** US-USR-009
- **Versión/Release:** 1.0

---

### US-USR-013: Eliminar rol no asignado

**Descripción:**  
Como administrador, Quiero eliminar roles que ya no son necesarios, Para mantener limpia la estructura de permisos.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un roleId válido sin usuarios asignados |
| **Cuando** | Se invoca DELETE /api/roles/{id} |
| **Entonces** | El rol se elimina de la base de datos, retornando HTTP 204 No Content |

**Metadatos:**
- **Prioridad:** Baja
- **Feature:** FT-USR-002
- **Épica:** EP-USR-001
- **Dependencias:** US-USR-009
- **Versión/Release:** 1.0

---

## Feature FT-USR-003 - Sistema de Permisos

### US-USR-014: Inicializar permisos predefinidos del sistema

**Descripción:**  
Como sistema, Quiero crear automáticamente los permisos base al iniciar, Para garantizar la disponibilidad de controles de acceso fundamentales.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | El sistema se inicia con app.seed.enabled=true |
| **Cuando** | Se ejecuta DataInitializer |
| **Entonces** | Se crean los permisos USER_READ, USER_CREATE, USER_UPDATE, USER_DELETE, ROLE_READ, ROLE_CREATE, ROLE_UPDATE, ROLE_DELETE, AUTH_VALIDATE si no existen |

**Metadatos:**
- **Prioridad:** Muy Alta
- **Feature:** FT-USR-003 - Sistema de Permisos
- **Épica:** EP-USR-001
- **Dependencias:** Ninguna
- **Versión/Release:** 1.0

---

### US-USR-015: Validar permisos granulares en operaciones

**Descripción:**  
Como sistema de seguridad, Quiero validar que los usuarios tengan los permisos específicos, Para controlar el acceso a nivel de operación.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Roles con permisos específicos (ej: ADMIN tiene todos, HR solo USER_READ) |
| **Cuando** | Un usuario intenta ejecutar una operación |
| **Entonces** | El sistema valida que el usuario tenga el permiso requerido asociado a alguno de sus roles |

**Metadatos:**
- **Prioridad:** Muy Alta
- **Feature:** FT-USR-003
- **Épica:** EP-USR-001
- **Dependencias:** US-USR-014
- **Versión/Release:** 1.0

---

### US-USR-016: Relación Many-to-Many entre Roles y Permisos

**Descripción:**  
Como arquitecto del sistema, Quiero que múltiples roles puedan compartir permisos, Para reutilizar configuraciones de acceso comunes.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Permisos USER_READ y USER_CREATE existentes |
| **Cuando** | Se crean roles ADMIN (todos los permisos) y HR (solo USER_READ) |
| **Entonces** | USER_READ se asocia a ambos roles sin duplicación en la tabla permissions |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-USR-003
- **Épica:** EP-USR-001
- **Dependencias:** US-USR-014
- **Versión/Release:** 1.0

---

## Feature FT-USR-004 - Validación de Credenciales (Endpoint Interno)

### US-USR-017: Validar credenciales válidas para auth-service

**Descripción:**  
Como auth-service (microservicio de autenticación), Quiero validar las credenciales de un usuario contra la base de datos, Para generar tokens JWT con información correcta.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un usuario activo con credenciales correctas (username/email y password) |
| **Cuando** | Auth-service invoca POST /internal/users/validate con InternalUserValidationRequest |
| **Entonces** | Se valida la contraseña con BCrypt, actualiza lastLogin, y retorna InternalUserValidationResponse con username, email, roles, permissions, enabled, locked |

**Metadatos:**
- **Prioridad:** Muy Alta
- **Feature:** FT-USR-004 - Validación de Credenciales (Endpoint Interno)
- **Épica:** EP-USR-001
- **Dependencias:** US-USR-001, FT-USR-002, FT-USR-003
- **Versión/Release:** 1.0

---

### US-USR-018: Rechazar credenciales incorrectas

**Descripción:**  
Como sistema de seguridad, Quiero denegar el acceso cuando la contraseña no coincide, Para proteger las cuentas de usuarios.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un usuario con contraseña incorrecta |
| **Cuando** | Auth-service invoca /internal/users/validate |
| **Entonces** | Se lanza InvalidCredentialsException sin revelar si el usuario existe o no (por seguridad), código HTTP 401 |

**Metadatos:**
- **Prioridad:** Muy Alta
- **Feature:** FT-USR-004
- **Épica:** EP-USR-001
- **Dependencias:** US-USR-017
- **Versión/Release:** 1.0

---

### US-USR-019: Rechazar login de usuario deshabilitado

**Descripción:**  
Como administrador, Quiero impedir el acceso a usuarios deshabilitados, Para suspender cuentas temporalmente sin eliminarlas.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un usuario con enabled=false |
| **Cuando** | Auth-service invoca /internal/users/validate |
| **Entonces** | Se lanza InvalidCredentialsException con mensaje "Usuario deshabilitado", código HTTP 401 |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-USR-004
- **Épica:** EP-USR-001
- **Dependencias:** US-USR-017
- **Versión/Release:** 1.0

---

### US-USR-020: Rechazar login de usuario bloqueado

**Descripción:**  
Como sistema de seguridad, Quiero impedir el acceso a usuarios bloqueados, Para proteger cuentas comprometidas o bajo sospecha.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un usuario con locked=true |
| **Cuando** | Auth-service invoca /internal/users/validate |
| **Entonces** | Se lanza InvalidCredentialsException con mensaje "Usuario bloqueado", código HTTP 401 |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-USR-004
- **Épica:** EP-USR-001
- **Dependencias:** US-USR-017
- **Versión/Release:** 1.0

---

### US-USR-021: Actualizar timestamp de último login

**Descripción:**  
Como sistema de auditoría, Quiero registrar cuándo fue el último acceso exitoso de un usuario, Para monitorear actividad y detectar accesos anómalos.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un usuario que se autentica exitosamente |
| **Cuando** | La validación de credenciales es exitosa |
| **Entonces** | El campo lastLogin se actualiza con la fecha y hora actual (LocalDateTime.now()) |

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-USR-004
- **Épica:** EP-USR-001
- **Dependencias:** US-USR-017
- **Versión/Release:** 1.0

---

## Feature FT-USR-005 - Seguridad y Autenticación

### US-USR-022: Encriptar contraseñas con BCrypt

**Descripción:**  
Como sistema de seguridad, Quiero almacenar contraseñas hasheadas con BCrypt, Para proteger las credenciales en caso de filtración de base de datos.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un usuario se crea o actualiza con una nueva contraseña |
| **Cuando** | Se invoca el servicio de creación/actualización |
| **Entonces** | La contraseña se encripta usando PasswordEncoderPort (delegando a BCryptPasswordEncoder) antes de persistir |

**Metadatos:**
- **Prioridad:** Muy Alta
- **Feature:** FT-USR-005 - Seguridad y Autenticación
- **Épica:** EP-SEC-001 - Seguridad del Sistema
- **Dependencias:** Ninguna
- **Versión/Release:** 1.0

---

### US-USR-023: Proteger endpoints con autenticación JWT

**Descripción:**  
Como sistema de seguridad, Quiero que los endpoints públicos de usuarios y roles requieran JWT válido, Para prevenir accesos no autorizados.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Endpoints bajo /api/users y /api/roles |
| **Cuando** | Se invoca sin token JWT o con token inválido |
| **Entonces** | Se rechaza con código HTTP 401 Unauthorized |

**Metadatos:**
- **Prioridad:** Muy Alta
- **Feature:** FT-USR-005
- **Épica:** EP-SEC-001
- **Dependencias:** Ninguna
- **Versión/Release:** 1.0

---

### US-USR-024: Restringir operaciones por rol ADMIN

**Descripción:**  
Como sistema de autorización, Quiero que solo usuarios con rol ADMIN puedan crear/eliminar usuarios y roles, Para mantener la integridad del sistema de permisos.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Endpoints POST /api/users, DELETE /api/users/{id}, POST /api/roles, DELETE /api/roles/{id} |
| **Cuando** | Un usuario sin rol ADMIN intenta acceder |
| **Entonces** | Se rechaza con @PreAuthorize("hasRole('ADMIN')") y código HTTP 403 Forbidden |

**Metadatos:**
- **Prioridad:** Muy Alta
- **Feature:** FT-USR-005
- **Épica:** EP-SEC-001
- **Dependencies:** US-USR-023
- **Versión/Release:** 1.0

---

### US-USR-025: Permitir auto-gestión de usuario autenticado

**Descripción:**  
Como usuario autenticado, Quiero poder consultar y actualizar mi propia información, Para gestionar mis datos sin necesitar privilegios de administrador.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un usuario autenticado consultando GET /api/users/{id} o PUT /api/users/{id} |
| **Cuando** | El ID corresponde al usuario autenticado actual (validado por @userService.isCurrentUser(#id)) |
| **Entonces** | Se permite la operación incluso sin rol ADMIN |

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-USR-005
- **Épica:** EP-SEC-001
- **Dependencias:** US-USR-023
- **Versión/Release:** 1.0

---

### US-USR-026: Proteger endpoint interno con API Key

**Descripción:**  
Como sistema de seguridad, Quiero que /internal/users/validate solo sea accesible por auth-service mediante API Key, Para evitar llamadas externas no autorizadas.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Endpoint POST /internal/users/validate |
| **Cuando** | Se invoca sin header X-Internal-API-Key o con valor incorrecto |
| **Entonces** | Se rechaza con código HTTP 401 Unauthorized (validado por ApiKeyAuthenticationFilter) |

**Metadatos:**
- **Prioridad:** Muy Alta
- **Feature:** FT-USR-005
- **Épica:** EP-SEC-001
- **Dependencias:** US-USR-017
- **Versión/Release:** 1.0

---

## Feature FT-USR-006 - Inicialización de Datos (Data Seeding)

### US-USR-027: Crear roles predefinidos del sistema

**Descripción:**  
Como sistema, Quiero crear automáticamente los roles ADMIN, HR, SALES, USER al iniciar, Para garantizar la disponibilidad de roles básicos.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | El sistema se inicia con app.seed.enabled=true |
| **Cuando** | Se ejecuta DataInitializer.createRoles() |
| **Entonces** | Se crean los roles si no existen, asignando permisos correspondientes a cada uno |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-USR-006 - Inicialización de Datos (Data Seeding)
- **Épica:** EP-USR-001
- **Dependencias:** US-USR-014 (Permisos deben existir primero)
- **Versión/Release:** 1.0

---

### US-USR-028: Crear usuario administrador inicial

**Descripción:**  
Como primer usuario del sistema, Quiero que se cree automáticamente un usuario admin, Para poder acceder y configurar el sistema por primera vez.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | El sistema se inicia con app.seed.enabled=true y configuración app.seed.admin.* |
| **Cuando** | Se ejecuta DataInitializer.createAdminUser() y no existe el usuario |
| **Entonces** | Se crea usuario con username=admin, email=admin@nova.com, password hasheada, rol ADMIN, enabled=true, locked=false |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-USR-006
- **Épica:** EP-USR-001
- **Dependencias:** US-USR-027
- **Versión/Release:** 1.0

---

### US-USR-029: Configurar credenciales admin desde propiedades

**Descripción:**  
Como administrador de infraestructura, Quiero configurar las credenciales del admin inicial mediante variables de entorno o application.yaml, Para personalizar el acceso por entorno.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Configuración app.seed.admin.username, app.seed.admin.email, app.seed.admin.password en application.yaml o environment variables |
| **Cuando** | El sistema inicia y ejecuta el seeding |
| **Entonces** | El usuario admin se crea con esos valores personalizados en lugar de los valores por defecto |

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-USR-006
- **Épica:** EP-USR-001
- **Dependencias:** US-USR-028
- **Versión/Release:** 1.0

---

## Feature FT-USR-007 - Persistencia y Consultas

### US-USR-030: Buscar usuario por username o email

**Descripción:**  
Como sistema de autenticación, Quiero buscar usuarios por username o email indistintamente, Para permitir login flexible con ambos identificadores.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un usuario con username="admin" y email="admin@nova.com" |
| **Cuando** | Se invoca userPersistencePort.findByUsernameOrEmail("admin@nova.com") |
| **Entonces** | Retorna Optional<User> con el usuario encontrado |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-USR-007 - Persistencia y Consultas
- **Épica:** EP-USR-001
- **Dependencias:** US-USR-001
- **Versión/Release:** 1.0

---

### US-USR-031: Validar existencia de username o email

**Descripción:**  
Como servicio de validación, Quiero verificar rápidamente si un username o email ya existe, Para dar feedback inmediato al crear usuarios.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un username que ya existe en la base de datos |
| **Cuando** | Se invoca userPersistencePort.existsByUsernameOrEmail(username) |
| **Entonces** | Retorna true sin necesidad de cargar el objeto completo |

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-USR-007
- **Épica:** EP-USR-001
- **Dependencias:** US-USR-001
- **Versión/Release:** 1.0

---

### US-USR-032: Cargar roles y permisos con EAGER fetch

**Descripción:**  
Como sistema de autorización, Quiero que los roles y permisos se carguen automáticamente al consultar un usuario, Para evitar LazyInitializationException y N+1 queries.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Relación @ManyToMany con FetchType.EAGER en User->Roles y Role->Permissions |
| **Cuando** | Se consulta un usuario por ID |
| **Entonces** | Una sola query (con JOINS) carga usuario + roles + permisos en memoria |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-USR-007
- **Épica:** EP-USR-001
- **Dependencias:** US-USR-001, FT-USR-002, FT-USR-003
- **Versión/Release:** 1.0

---

## Feature FT-USR-008 - Manejo de Estados de Usuario

### US-USR-033: Gestionar estado de usuario (ACTIVE/INACTIVE/SUSPENDED)

**Descripción:**  
Como administrador, Quiero cambiar el estado de un usuario entre ACTIVE, INACTIVE, SUSPENDED, Para controlar el ciclo de vida de las cuentas.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un usuario con status=ACTIVE |
| **Cuando** | Se actualiza con status=SUSPENDED |
| **Entonces** | El usuario mantiene sus datos pero no puede autenticarse (validado en combinación con enabled/locked) |

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-USR-008 - Manejo de Estados de Usuario
- **Épica:** EP-USR-001
- **Dependencias:** US-USR-001
- **Versión/Release:** 1.0

---

### US-USR-034: Bloquear y desbloquear cuentas de usuario

**Descripción:**  
Como sistema de seguridad, Quiero bloquear cuentas comprometidas y desbloquearlas cuando se resuelva, Para proteger el sistema de accesos maliciosos.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un usuario con locked=false |
| **Cuando** | Se invoca user.lockAccount() |
| **Entonces** | locked=true y user.unlockAccount() lo revierte a false |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-USR-008
- **Épica:** EP-USR-001
- **Dependencias:** US-USR-001
- **Versión/Release:** 1.0

---

### US-USR-035: Habilitar y deshabilitar usuarios

**Descripción:**  
Como administrador, Quiero habilitar/deshabilitar usuarios sin eliminar sus datos, Para suspensiones temporales o reactivaciones.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un usuario con enabled=true |
| **Cuando** | Se invoca user.disableUser() |
| **Entonces** | enabled=false y user.enableUser() lo revierte a true |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-USR-008
- **Épica:** EP-USR-001
- **Dependencias:** US-USR-001
- **Versión/Release:** 1.0

---

## 📊 Matriz de Cobertura: Tests vs Historias de Usuario

| US | Título | Test Unitario | Test Integración | ✅ |
|---|---|---|---|---|
| US-USR-001 | Crear usuario válido | UserServiceTest | N/A | ✅ |
| US-USR-002 | Username duplicado | UserServiceTest | N/A | ✅ |
| US-USR-003 | Email duplicado | UserServiceTest | N/A | ✅ |
| US-USR-004 | Actualizar usuario | UserServiceTest | N/A | ✅ |
| US-USR-005 | Eliminar usuario | UserServiceTest | N/A | ✅ |
| US-USR-006 | Usuario inexistente | UserServiceTest | N/A | ✅ |
| US-USR-007 | Listar con paginación | UserServiceTest, UserPersistenceAdapterTest | N/A | ✅ |
| US-USR-008 | Obtener por ID | UserServiceTest | N/A | ✅ |
| US-USR-009 | Crear rol | RoleServiceTest | N/A | ✅ |
| US-USR-010 | Rol duplicado | RoleServiceTest | N/A | ✅ |
| US-USR-011 | Listar roles | RoleServiceTest, RolePersistenceAdapterTest | N/A | ✅ |
| US-USR-012 | Obtener rol por ID | RoleServiceTest | N/A | ✅ |
| US-USR-013 | Eliminar rol | RoleServiceTest | N/A | ✅ |
| US-USR-014 | Permisos base | DataInitializer | N/A | ✅ |
| US-USR-015 | Validar permisos | SecurityConfigTest | N/A | ✅ |
| US-USR-016 | Many-to-Many Roles-Permisos | RoleTest, PermissionTest | N/A | ✅ |
| US-USR-017 | Validar credenciales válidas | UserServiceTest | N/A | ✅ |
| US-USR-018 | Credenciales incorrectas | UserServiceTest | N/A | ✅ |
| US-USR-019 | Usuario deshabilitado | UserServiceTest | N/A | ✅ |
| US-USR-020 | Usuario bloqueado | UserServiceTest | N/A | ✅ |
| US-USR-021 | Actualizar lastLogin | UserServiceTest | N/A | ✅ |
| US-USR-022 | Encriptar BCrypt | PasswordEncoderAdapterTest | N/A | ✅ |
| US-USR-023 | Autenticación JWT | SecurityConfigTest | N/A | ✅ |
| US-USR-024 | Restricción ADMIN | SecurityConfigTest | N/A | ✅ |
| US-USR-025 | Auto-gestión usuario | UserService.isCurrentUser | N/A | ✅ |
| US-USR-026 | API Key interna | ApiKeyAuthenticationFilter | N/A | ✅ |
| US-USR-027 | Roles predefinidos | DataInitializer | N/A | ✅ |
| US-USR-028 | Usuario admin inicial | DataInitializer | N/A | ✅ |
| US-USR-029 | Config desde propiedades | SeedPropertiesTest | N/A | ✅ |
| US-USR-030 | Buscar por username/email | UserPersistenceAdapterTest | N/A | ✅ |
| US-USR-031 | Validar existencia | UserPersistenceAdapterTest | N/A | ✅ |
| US-USR-032 | EAGER fetch | UserTest, RoleTest | N/A | ✅ |
| US-USR-033 | Estados de usuario | UserTest, UserStatusEnumTest | N/A | ✅ |
| US-USR-034 | Bloquear/desbloquear | UserTest | N/A | ✅ |
| US-USR-035 | Habilitar/deshabilitar | UserTest | N/A | ✅ |

---

## 📐 Resumen INVEST

| Principio | Cumplimiento | Observación |
|---|---|---|
| **I**ndependent | ✅✅✅ | Features separadas, dependencias explícitas entre HUs |
| **N**egotiable | ✅✅ | Sin implementación prescriptiva, enfoque en valor de negocio |
| **V**aluable | ✅✅✅ | Cada HU tiene "Para" claro, 7 actores diferentes (admin, sistema, usuario, auth-service, etc.) |
| **E**stimable | ✅✅✅ | Criterios concretos, cuantificables, acotados |
| **S**mall | ✅✅✅ | Sprint-sized (1-3 puntos cada una), 35 HU ÷ 5-7 sprints |
| **T**estable | ✅✅✅ | Escenarios específicos, "Entonces" verificables con tests unitarios |

---

## 🏗️ Notas Arquitectónicas

- **Domain Layer**: User, Role, Permission validan reglas de negocio puras (addRole, removeRole, lockAccount, etc.)
- **Hexagonal Architecture**: Puertos (UserPersistencePort, RolePersistencePort, PasswordEncoderPort, ValidateUserCredentialsUseCase) desacoplan dominio de infraestructura
- **Security**: BCrypt para passwords, JWT para autenticación de endpoints públicos, API Key para endpoint interno /internal/users/validate
- **Data Seeding**: DataInitializer con @ConditionalOnProperty permite crear usuarios/roles/permisos base en entornos dev/staging
- **Data Loading**: MongoDB carga eficientemente User con todos sus Roles y Permissions mediante referencias embebidas
- **Clean Architecture**: Servicios (UserService, RoleService) implementan casos de uso, adaptadores (UserPersistenceAdapter, PasswordEncoderAdapter) implementan puertos
- **Database**: MongoDB con colecciones dinámicas (permissions, roles, users)
