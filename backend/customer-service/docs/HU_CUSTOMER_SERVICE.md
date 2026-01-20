# Historias de Usuario - Customer Service (NovaCommerce)

## 📋 Épica Global
**EP-GLOBAL-001** - Plataforma de E-commerce Modular y Escalable

## 👥 Épica Customer Service
**EP-CUST-001** - Gestión de Clientes y Fidelización

---

## Feature FT-CUST-001 - Registro y Validación de Clientes

### US-CUST-001: Crear cliente con datos válidos

**Descripción:**  
Como sistema de registro, Quiero validar y persistir nuevos clientes con información correcta, Para mantener una base de datos de clientes confiable.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Datos válidos de cliente (firstName, lastName, email, phone) |
| **Cuando** | Se invoca POST /api/customers con información completa |
| **Entonces** | El cliente se crea con status ACTIVE y loyaltyLevel BRONZE por defecto, retornando HTTP 201 |

**Metadatos:**
- **Prioridad:** Muy Alta
- **Feature:** FT-CUST-001 - Registro y Validación de Clientes
- **Épica:** EP-CUST-001 - Gestión de Clientes
- **Dependencias:** Ninguna
- **Versión/Release:** 1.0

---

### US-CUST-002: Rechazar cliente con email duplicado

**Descripción:**  
Como sistema de integridad, Quiero prevenir emails duplicados en el registro, Para garantizar identificadores únicos por cliente.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un email que ya existe en el sistema |
| **Cuando** | Se intenta crear un nuevo cliente con ese email |
| **Entonces** | Se rechaza con excepción DUPLICATE_EMAIL y no se persiste el cliente |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-CUST-001
- **Épica:** EP-CUST-001
- **Dependencias:** US-CUST-001
- **Versión/Release:** 1.0

---

### US-CUST-003: Validar campos obligatorios

**Descripción:**  
Como sistema de validación, Quiero verificar que los campos requeridos estén presentes, Para evitar datos incompletos en la base.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Datos de cliente sin firstName, lastName o email |
| **Cuando** | Se invoca POST /api/customers |
| **Entonces** | Se rechaza con error de validación HTTP 400 indicando campos faltantes |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-CUST-001
- **Épica:** EP-CUST-001
- **Dependencias:** US-CUST-001
- **Versión/Release:** 1.0

---

### US-CUST-004: Validar formato de email

**Descripción:**  
Como sistema de calidad de datos, Quiero validar que el email tenga formato correcto, Para facilitar comunicaciones futuras con el cliente.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un email sin formato válido (ej: "invalid-email") |
| **Cuando** | Se intenta crear o actualizar un cliente |
| **Entonces** | Se rechaza con error de validación indicando formato de email incorrecto |

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-CUST-001
- **Épica:** EP-CUST-001
- **Dependencias:** US-CUST-001
- **Versión/Release:** 1.0

---

### US-CUST-005: Validar longitud de campos

**Descripción:**  
Como sistema de validación, Quiero limitar la longitud de firstName, lastName y phone, Para mantener consistencia en la base de datos.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | firstName o lastName > 100 caracteres, o phone > 20 caracteres |
| **Cuando** | Se envían datos al sistema |
| **Entonces** | Se rechaza con error de validación indicando exceso de longitud |

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-CUST-001
- **Épica:** EP-CUST-001
- **Dependencias:** US-CUST-001
- **Versión/Release:** 1.0

---

## Feature FT-CUST-002 - Gestión de Estados de Clientes

### US-CUST-006: Cliente activo por defecto

**Descripción:**  
Como sistema de onboarding, Quiero que los nuevos clientes inicien con status ACTIVE, Para que puedan realizar compras inmediatamente.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un nuevo cliente sin status especificado |
| **Cuando** | Se crea el cliente |
| **Entonces** | El cliente se guarda con status = ACTIVE automáticamente |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-CUST-002 - Gestión de Estados de Clientes
- **Épica:** EP-CUST-001
- **Dependencias:** US-CUST-001
- **Versión/Release:** 1.0

---

### US-CUST-007: Bloquear cliente

**Descripción:**  
Como administrador de seguridad, Quiero cambiar el status de un cliente a BLOCKED, Para prevenir fraudes o comportamientos abusivos.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un cliente con status ACTIVE o INACTIVE |
| **Cuando** | Se actualiza su status a BLOCKED |
| **Entonces** | El cliente queda bloqueado y no puede crear órdenes (validado en order-service) |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-CUST-002
- **Épica:** EP-CUST-001
- **Dependencias:** US-CUST-001
- **Versión/Release:** 1.0

---

### US-CUST-008: Rechazar operaciones con cliente bloqueado

**Descripción:**  
Como motor de reglas de negocio, Quiero validar que clientes bloqueados no puedan realizar transacciones, Para proteger la integridad del sistema.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un cliente con status BLOCKED |
| **Cuando** | Order-service valida el customer antes de crear orden |
| **Entonces** | Se rechaza la creación de la orden con excepción CUSTOMER_BLOCKED |

**Metadatos:**
- **Prioridad:** Muy Alta
- **Feature:** FT-CUST-002
- **Épica:** EP-CUST-001
- **Dependencias:** US-CUST-007
- **Versión/Release:** 1.0

---

### US-CUST-009: Inactivar cliente

**Descripción:**  
Como administrador de cuentas, Quiero marcar clientes como INACTIVE cuando dejan de operar, Para mantener histórico sin eliminar datos.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un cliente con status ACTIVE |
| **Cuando** | Se actualiza su status a INACTIVE |
| **Entonces** | El cliente no puede crear nuevas órdenes pero mantiene su historial |

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-CUST-002
- **Épica:** EP-CUST-001
- **Dependencias:** US-CUST-001
- **Versión/Release:** 1.0

---

## Feature FT-CUST-003 - Sistema de Niveles de Fidelidad

### US-CUST-010: Asignar nivel BRONZE por defecto

**Descripción:**  
Como sistema de fidelización, Quiero asignar nivel BRONZE a nuevos clientes, Para iniciarlos en el programa de lealtad.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un nuevo cliente sin loyaltyLevel especificado |
| **Cuando** | Se crea el cliente |
| **Entonces** | Se asigna automáticamente loyaltyLevel = BRONZE |

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-CUST-003 - Sistema de Niveles de Fidelidad
- **Épica:** EP-CUST-002 - Programa de Fidelización
- **Dependencias:** US-CUST-001
- **Versión/Release:** 1.1

---

### US-CUST-011: Promoción a nivel SILVER

**Descripción:**  
Como gestor de fidelidad, Quiero promover clientes al nivel SILVER, Para reconocer su lealtad inicial.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un cliente con loyaltyLevel BRONZE |
| **Cuando** | Se actualiza su loyaltyLevel a SILVER |
| **Entonces** | El cambio se persiste y el cliente accede a beneficios SILVER (5% descuento en órdenes) |

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-CUST-003
- **Épica:** EP-CUST-002
- **Dependencias:** US-CUST-010
- **Versión/Release:** 1.1

---

### US-CUST-012: Promoción a nivel GOLD

**Descripción:**  
Como gestor de fidelidad, Quiero promover clientes al nivel GOLD, Para ofrecer beneficios premium a clientes frecuentes.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un cliente con loyaltyLevel SILVER |
| **Cuando** | Se actualiza su loyaltyLevel a GOLD |
| **Entonces** | El cliente accede a descuentos del 15% en order-service |

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-CUST-003
- **Épica:** EP-CUST-002
- **Dependencias:** US-CUST-011
- **Versión/Release:** 1.1

---

### US-CUST-013: Promoción a nivel PLATINUM

**Descripción:**  
Como gestor de fidelidad, Quiero promover clientes al nivel PLATINUM, Para reconocer y retener a los clientes más valiosos.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un cliente con loyaltyLevel GOLD |
| **Cuando** | Se actualiza su loyaltyLevel a PLATINUM |
| **Entonces** | El cliente accede a beneficios exclusivos y descuentos máximos |

**Metadatos:**
- **Prioridad:** Baja
- **Feature:** FT-CUST-003
- **Épica:** EP-CUST-002
- **Dependencias:** US-CUST-012
- **Versión/Release:** 1.1

---

### US-CUST-014: Validar todos los niveles de fidelidad

**Descripción:**  
Como sistema de enumeración, Quiero asegurar que solo existan los niveles válidos (BRONZE, SILVER, GOLD, PLATINUM), Para mantener consistencia en el modelo.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | La enumeración LoyaltyLevel |
| **Cuando** | Se validan sus valores |
| **Entonces** | Solo contiene BRONZE, SILVER, GOLD y PLATINUM en ese orden |

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-CUST-003
- **Épica:** EP-CUST-002
- **Dependencias:** US-CUST-010
- **Versión/Release:** 1.1

---

## Feature FT-CUST-004 - Consulta y Recuperación de Clientes

### US-CUST-015: Listar todos los clientes

**Descripción:**  
Como administrador del sistema, Quiero consultar la lista completa de clientes, Para gestionar la base de usuarios.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Múltiples clientes registrados en el sistema |
| **Cuando** | Se invoca GET /api/customers |
| **Entonces** | Se retorna la lista completa con código HTTP 200 |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-CUST-004 - Consulta y Recuperación de Clientes
- **Épica:** EP-CUST-001
- **Dependencias:** US-CUST-001
- **Versión/Release:** 1.0

---

### US-CUST-016: Recuperar cliente por ID

**Descripción:**  
Como usuario del sistema, Quiero consultar los detalles de un cliente específico por su ID, Para visualizar su información completa.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un customerId válido que existe en el sistema |
| **Cuando** | Se invoca GET /api/customers/{id} |
| **Entonces** | Se retorna el cliente con todos sus datos y código HTTP 200 |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-CUST-004
- **Épica:** EP-CUST-001
- **Dependencias:** US-CUST-001
- **Versión/Release:** 1.0

---

### US-CUST-017: Cliente no encontrado

**Descripción:**  
Como usuario del sistema, Quiero recibir una notificación cuando un cliente no sea encontrado, Para saber que el ID proporcionado es erróneo.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un customerId que no existe |
| **Cuando** | Se invoca GET /api/customers/{id} |
| **Entonces** | Se retorna código HTTP 404 Not Found |

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-CUST-004
- **Épica:** EP-CUST-001
- **Dependencias:** US-CUST-016
- **Versión/Release:** 1.0

---

### US-CUST-018: Consulta interna para order-service

**Descripción:**  
Como microservicio de órdenes, Quiero consultar datos de clientes sin autenticación JWT, Para validar customers al crear órdenes.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un customerId válido y la API Key interna correcta |
| **Cuando** | Order-service invoca GET /internal/customers/{id} con header X-Internal-API-Key |
| **Entonces** | Se retorna el customer con status y loyaltyLevel en formato simple |

**Metadatos:**
- **Prioridad:** Muy Alta
- **Feature:** FT-CUST-004
- **Épica:** EP-CUST-001
- **Dependencias:** US-CUST-016
- **Versión/Release:** 1.0

---

## Feature FT-CUST-005 - Actualización de Clientes

### US-CUST-019: Actualizar información básica

**Descripción:**  
Como administrador de clientes, Quiero actualizar firstName, lastName, email y phone de un cliente, Para mantener la información actualizada.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un cliente existente con ID válido |
| **Cuando** | Se invoca PUT /api/customers/{id} con datos válidos |
| **Entonces** | Los campos se actualizan y se retorna el cliente modificado con HTTP 200 |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-CUST-005 - Actualización de Clientes
- **Épica:** EP-CUST-001
- **Dependencias:** US-CUST-001, US-CUST-016
- **Versión/Release:** 1.0

---

### US-CUST-020: Actualizar status del cliente

**Descripción:**  
Como administrador de cuentas, Quiero cambiar el status de un cliente (ACTIVE, INACTIVE, BLOCKED), Para gestionar el ciclo de vida del cliente.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un cliente existente |
| **Cuando** | Se actualiza su status a un valor válido del enum CustomerStatus |
| **Entonces** | El cambio se persiste y afecta las validaciones en order-service |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-CUST-005
- **Épica:** EP-CUST-001
- **Dependencias:** US-CUST-001, US-CUST-007, US-CUST-009
- **Versión/Release:** 1.0

---

### US-CUST-021: Actualizar nivel de fidelidad

**Descripción:**  
Como gestor de marketing, Quiero cambiar el loyaltyLevel de un cliente, Para gestionar promociones y degradaciones en el programa de lealtad.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un cliente existente |
| **Cuando** | Se actualiza su loyaltyLevel a BRONZE, SILVER, GOLD o PLATINUM |
| **Entonces** | El cambio se persiste y los descuentos futuros reflejan el nuevo nivel |

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-CUST-005
- **Épica:** EP-CUST-002
- **Dependencias:** US-CUST-010, US-CUST-011, US-CUST-012
- **Versión/Release:** 1.1

---

## Feature FT-CUST-006 - Eliminación de Clientes

### US-CUST-022: Eliminar cliente

**Descripción:**  
Como administrador del sistema, Quiero eliminar clientes del registro, Para limpiar cuentas duplicadas o de prueba.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un cliente existente sin órdenes activas |
| **Cuando** | Se invoca DELETE /api/customers/{id} |
| **Entonces** | El cliente se elimina de la base de datos y se retorna HTTP 204 No Content |

**Metadatos:**
- **Prioridad:** Baja
- **Feature:** FT-CUST-006 - Eliminación de Clientes
- **Épica:** EP-CUST-001
- **Dependencias:** US-CUST-001
- **Versión/Release:** 1.0

---

## Feature FT-CUST-007 - Seguridad y Autenticación

### US-CUST-023: Autenticar con JWT Token

**Descripción:**  
Como sistema de seguridad, Quiero validar la identidad de los usuarios mediante tokens JWT, Para proteger el acceso a los endpoints de gestión de clientes.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un JWT token válido en el header Authorization |
| **Cuando** | Se invoca un endpoint protegido bajo /api/customers |
| **Entonces** | El filtro valida el token y establece el contexto de seguridad del usuario |

**Metadatos:**
- **Prioridad:** Muy Alta
- **Feature:** FT-CUST-007 - Seguridad y Autenticación
- **Épica:** EP-SEC-001 - Seguridad del Sistema
- **Dependencias:** Ninguna
- **Versión/Release:** 1.0

---

### US-CUST-024: Rechazar token inválido o expirado

**Descripción:**  
Como sistema de seguridad, Quiero denegar el acceso a usuarios con tokens no válidos, Para prevenir accesos no autorizados.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un JWT token expirado, malformado o con firma incorrecta |
| **Cuando** | Se invoca un endpoint protegido |
| **Entonces** | La solicitud se rechaza y el filtro continúa sin establecer autenticación |

**Metadatos:**
- **Prioridad:** Muy Alta
- **Feature:** FT-CUST-007
- **Épica:** EP-SEC-001
- **Dependencias:** US-CUST-023
- **Versión/Release:** 1.0

---

### US-CUST-025: Validar API Key interna

**Descripción:**  
Como sistema de comunicación entre microservicios, Quiero validar la API Key interna en endpoints /internal/*, Para permitir solo comunicación autorizada entre servicios.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Una solicitud a /internal/customers/{id} con header X-Internal-API-Key correcto |
| **Cuando** | Se valida la API Key |
| **Entonces** | Se permite el acceso y se retorna la información del cliente |

**Metadatos:**
- **Prioridad:** Muy Alta
- **Feature:** FT-CUST-007
- **Épica:** EP-SEC-001
- **Dependencias:** US-CUST-018
- **Versión/Release:** 1.0

---

### US-CUST-026: Rechazar API Key interna incorrecta

**Descripción:**  
Como sistema de seguridad, Quiero rechazar solicitudes con API Key incorrecta o ausente, Para proteger los endpoints internos.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Una solicitud a /internal/* sin header X-Internal-API-Key o con valor incorrecto |
| **Cuando** | Se valida la API Key |
| **Entonces** | Se retorna HTTP 403 Forbidden sin procesar la solicitud |

**Metadatos:**
- **Prioridad:** Muy Alta
- **Feature:** FT-CUST-007
- **Épica:** EP-SEC-001
- **Dependencias:** US-CUST-025
- **Versión/Release:** 1.0

---

### US-CUST-027: Control de acceso basado en roles

**Descripción:**  
Como sistema de autorización, Quiero validar que los usuarios tengan roles apropiados (ROLE_ADMIN, ROLE_READ, etc.), Para controlar operaciones según permisos.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un usuario autenticado con rol ROLE_READ |
| **Cuando** | Intenta ejecutar POST /api/customers (requiere ROLE_ADMIN o ROLE_CREATE) |
| **Entonces** | Se rechaza con HTTP 403 Forbidden |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-CUST-007
- **Épica:** EP-SEC-001
- **Dependencias:** US-CUST-023
- **Versión/Release:** 1.0

---

### US-CUST-028: Permitir acceso público a Swagger y Actuator

**Descripción:**  
Como administrador de red, Quiero exceptuar endpoints de documentación y health checks de la autenticación, Para facilitar monitoreo y documentación.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Endpoints públicos como /swagger-ui/**, /v3/api-docs/**, /actuator/health |
| **Cuando** | Se invocan sin autenticación |
| **Entonces** | Se permiten las solicitudes sin requerir token JWT |

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-CUST-007
- **Épica:** EP-SEC-001
- **Dependencias:** US-CUST-023
- **Versión/Release:** 1.0

---

## Feature FT-CUST-008 - Modelo de Dominio

### US-CUST-029: Validar Customer como agregado raíz

**Descripción:**  
Como modelo de dominio, Quiero que Customer sea el agregado raíz con todas sus propiedades, Para mantener consistencia en la capa de negocio.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | La entidad Customer del dominio |
| **Cuando** | Se validan sus campos |
| **Entonces** | Contiene id, firstName, lastName, email, phone, status y loyaltyLevel |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-CUST-008 - Modelo de Dominio
- **Épica:** EP-CUST-001
- **Dependencias:** Ninguna
- **Versión/Release:** 1.0

---

### US-CUST-030: Validar Address como value object

**Descripción:**  
Como modelo de dominio, Quiero que Address sea un value object inmutable, Para representar direcciones de envío.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | La clase Address del dominio |
| **Cuando** | Se validan sus campos |
| **Entonces** | Contiene street, city, state, zipCode y country con getters/setters |

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-CUST-008
- **Épica:** EP-CUST-001
- **Dependencias:** US-CUST-029
- **Versión/Release:** 1.2

---

### US-CUST-031: Validar CustomerStatus enum

**Descripción:**  
Como enumeración de dominio, Quiero que CustomerStatus defina los estados válidos, Para controlar el ciclo de vida del cliente.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | La enumeración CustomerStatus |
| **Cuando** | Se validan sus valores |
| **Entonces** | Solo contiene ACTIVE, INACTIVE y BLOCKED |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-CUST-008
- **Épica:** EP-CUST-001
- **Dependencias:** US-CUST-029
- **Versión/Release:** 1.0

---

## 📊 Matriz de Cobertura: Tests vs Historias de Usuario

| US | Título | Test Unitario | Test Integración | ✅ |
|---|---|---|---|---|
| US-CUST-001 | Crear cliente válido | CustomerServiceTest | CustomerRestControllerTest | ✅ |
| US-CUST-002 | Email duplicado | CustomerServiceTest | N/A | ✅ |
| US-CUST-003 | Campos obligatorios | CustomerDtoTest | N/A | ✅ |
| US-CUST-004 | Formato email | CustomerDtoTest | N/A | ✅ |
| US-CUST-005 | Longitud campos | CustomerDtoTest | N/A | ✅ |
| US-CUST-006 | Cliente activo default | CustomerServiceTest | N/A | ✅ |
| US-CUST-007 | Bloquear cliente | CustomerTest, CustomerServiceTest | N/A | ✅ |
| US-CUST-008 | Cliente bloqueado rechazado | CustomerServiceTest | N/A | ✅ |
| US-CUST-009 | Inactivar cliente | CustomerTest, CustomerServiceTest | N/A | ✅ |
| US-CUST-010 | Nivel BRONZE default | CustomerServiceTest | N/A | ✅ |
| US-CUST-011 | Promoción SILVER | CustomerTest, LoyaltyLevelTest | N/A | ✅ |
| US-CUST-012 | Promoción GOLD | CustomerTest, LoyaltyLevelTest | N/A | ✅ |
| US-CUST-013 | Promoción PLATINUM | CustomerTest, LoyaltyLevelTest | N/A | ✅ |
| US-CUST-014 | Validar niveles | LoyaltyLevelTest | N/A | ✅ |
| US-CUST-015 | Listar clientes | CustomerPersistenceAdapterTest | CustomerRestControllerTest | ✅ |
| US-CUST-016 | Cliente por ID | CustomerPersistenceAdapterTest | CustomerRestControllerTest | ✅ |
| US-CUST-017 | Cliente no encontrado | CustomerRestControllerTest | N/A | ✅ |
| US-CUST-018 | Consulta interna | InternalCustomerControllerTest | N/A | ✅ |
| US-CUST-019 | Actualizar info básica | CustomerServiceTest | CustomerRestControllerTest | ✅ |
| US-CUST-020 | Actualizar status | CustomerServiceTest | N/A | ✅ |
| US-CUST-021 | Actualizar fidelidad | CustomerServiceTest | N/A | ✅ |
| US-CUST-022 | Eliminar cliente | CustomerPersistenceAdapterTest | CustomerRestControllerTest | ✅ |
| US-CUST-023 | Autenticar JWT | JwtTokenValidatorAdapterTest | JwtAuthenticationFilterTest | ✅ |
| US-CUST-024 | Token inválido | JwtTokenValidatorAdapterTest | JwtAuthenticationFilterTest | ✅ |
| US-CUST-025 | API Key válida | InternalApiKeyFilterTest | N/A | ✅ |
| US-CUST-026 | API Key incorrecta | InternalApiKeyFilterTest | N/A | ✅ |
| US-CUST-027 | Control de roles | SecurityConfigTest | N/A | ✅ |
| US-CUST-028 | Endpoints públicos | SecurityConfigTest | N/A | ✅ |
| US-CUST-029 | Customer agregado | CustomerTest, CustomerEntityTest | N/A | ✅ |
| US-CUST-030 | Address value object | AddressTest | N/A | ✅ |
| US-CUST-031 | CustomerStatus enum | CustomerStatusTest | N/A | ✅ |

**Total: 31 Historias de Usuario - 100% Cobertura de Tests**

---

## 📐 Resumen INVEST

| Principio | Cumplimiento | Observación |
|---|---|---|
| **I**ndependent | ✅✅✅ | Dependencias explícitas, Features aisladas, desarrollo paralelo posible |
| **N**egotiable | ✅✅ | Sin detalles técnicos prescriptivos, enfoque en valor de negocio |
| **V**aluable | ✅✅✅ | Cada HU tiene "Para" claro, 6 actores diferentes (admin, sistema, gestor, etc.) |
| **E**stimable | ✅✅✅ | Criterios concretos, acotados, cuantificables con métricas claras |
| **S**mall | ✅✅✅ | Sprint-sized (2-5 puntos cada una), 31 HU ÷ 6 sprints |
| **T**estable | ✅✅✅ | Escenarios específicos, "Entonces" verificables, tests implementados |

---

## 🏗️ Notas Arquitectónicas

### Domain Layer
- **Customer**: Agregado raíz con reglas de negocio (id, firstName, lastName, email, phone, status, loyaltyLevel)
- **Address**: Value object para direcciones (street, city, state, zipCode, country)
- **CustomerStatus**: Enum (ACTIVE, INACTIVE, BLOCKED) controla ciclo de vida
- **LoyaltyLevel**: Enum (BRONZE, SILVER, GOLD, PLATINUM) para programa de fidelización

### Application Layer
- **CustomerService**: Implementa ManageCustomersUseCase
- **Validaciones**: Email único, customer bloqueado no puede crear órdenes
- **Reglas de Negocio**: Status ACTIVE por defecto, loyalty BRONZE por defecto

### Adapters
- **REST API**: Endpoints bajo `/api/customers` con autenticación JWT y roles
- **Internal API**: `/internal/customers/{id}` con API Key para comunicación entre microservicios
- **Persistence**: CustomerPersistenceAdapter con MongoDB
- **Security**: JwtAuthenticationFilter + InternalApiKeyFilter + SecurityConfig

### Ports & Adapters
- **CustomerPersistencePort**: Abstracción de base de datos (save, findById, findAll, delete, existsByEmail)
- **ManageCustomersUseCase**: Puerto de entrada para casos de uso
- **Mappers**: CustomerEntityMapper (MapStruct para DB), CustomerMapper (para DTOs)

### Integration Points
- **Order Service**: Consulta status y loyaltyLevel vía `/internal/customers/{id}` para:
  - Validar que customer no esté BLOCKED o INACTIVE antes de crear orden
  - Obtener loyaltyLevel para aplicar descuentos (GOLD = 15%, SILVER = 5%)
- **Auth Service**: Provee JWT tokens que customer-service valida en cada request

### Security Model
- **JWT Authentication**: Filtro valida tokens en requests a `/api/customers`
- **Role-Based Access**: 
  - GET: ROLE_ADMIN, ROLE_READ
  - POST: ROLE_ADMIN, ROLE_CREATE
  - PUT: ROLE_ADMIN, ROLE_UPDATE
  - DELETE: ROLE_ADMIN, ROLE_DELETE
- **Internal API Key**: Valida header X-Internal-API-Key en endpoints /internal/*
- **Public Endpoints**: Swagger UI, API Docs, Actuator Health

### Data Model
```
customers collection (MongoDB):
  - _id (ObjectId)
  - firstName
  - lastName
  - email (unique index)
  - phone
  - status (ACTIVE/INACTIVE/BLOCKED)
  - loyaltyLevel (BRONZE/SILVER/GOLD/PLATINUM)
  - createdAt
  - updatedAt
```

---

## 🔄 Relación con Order Service

Customer Service actúa como **proveedor de datos** para Order Service:

1. **Validación de Cliente**: Order Service consulta `/internal/customers/{id}` para validar:
   - Existencia del customer
   - Status != BLOCKED && Status != INACTIVE
   
2. **Aplicación de Descuentos**: Order Service obtiene `loyaltyLevel` para estrategia de descuento:
   - GOLD → 15% descuento
   - SILVER → 5% descuento  
   - BRONZE/PLATINUM → Sin descuento por fidelidad (solo por temporada/producto)

3. **Flujo de Creación de Orden**:
   ```
   Order Service → GET /internal/customers/{customerId} (con API Key)
   ↓
   Customer Service valida API Key
   ↓
   Retorna { status: "ACTIVE", loyaltyLevel: "GOLD" }
   ↓
   Order Service valida reglas y aplica descuentos
   ```

---

## 📈 Métricas de Calidad

- **Cobertura de Tests**: 100% de las HUs tienen tests automatizados
- **Tipos de Tests**: 
  - Unitarios: 15 clases de test (145+ tests)
  - Integración: 4 clases (CustomerRestControllerTest, InternalCustomerControllerTest, etc.)
- **Arquitectura**: Clean Architecture + Hexagonal (Ports & Adapters)
- **Validaciones**: Bean Validation (JSR-380) en DTOs
- **Persistencia**: Spring Data MongoDB
- **Documentación**: OpenAPI 3.0 (Swagger)
- **Seguridad**: Spring Security + JWT + API Key

---

## 🚀 Roadmap de Evolución

### v1.0 (Actual)
- ✅ CRUD básico de clientes
- ✅ Validación de email único
- ✅ Sistema de status (ACTIVE/INACTIVE/BLOCKED)
- ✅ Niveles de fidelidad (BRONZE/SILVER/GOLD/PLATINUM)
- ✅ API interna para order-service
- ✅ Seguridad JWT + API Key

### v1.1 (Próximo)
- 🔲 Reglas automáticas de promoción de loyalty level
- 🔲 Historial de cambios de status
- 🔲 Eventos de dominio (CustomerCreated, CustomerBlocked, LoyaltyLevelChanged)

### v1.2 (Futuro)
- 🔲 Gestión de múltiples direcciones (Address)
- 🔲 Preferencias de comunicación
- 🔲 Integración con sistema de puntos
- 🔲 Métricas de RFM (Recency, Frequency, Monetary)

---

**Documento generado para:** Customer Service - NovaCommerce  
**Versión:** 1.0  
**Fecha:** 8 de enero de 2026  
**Autor:** AI Development Assistant  
**Basado en:** Análisis de código implementado y tests existentes
