Actua com  arquitecto experto e ingeniero de desarrollo senior especialista en Java y spring Boot y eleabora todo lo necesario para la gestion de clientes en product-service, sigue el mismo estandar que has realizado para nova-gateway, auth-service, users-service y product-service. Ten en cuenta estos microservicios para que el nuevo microservicio pueda trabajar en conjunto con ellos. 

🧱 1️⃣ Estructura propuesta — customer-service

Mantenemos Clean Architecture + Ports & Adapters:
customer-service/
├── adapter/
│   ├── in/
│   │   └── web/
│   │       ├── CustomerRestController.java
│   │       ├── AddressRestController.java
│   │       └── GlobalExceptionHandler.java
│   ├── out/
│   │   └── persistence/
│   │       ├── CustomerPersistenceAdapter.java
│   │       └── AddressPersistenceAdapter.java
│
├── application/
│   ├── port/
│   │   ├── in/
│   │   │   ├── ManageCustomersUseCase.java
│   │   │   ├── ManageCustomerStatusUseCase.java
│   │   │   └── ManageAddressesUseCase.java
│   │   └── out/
│   │       ├── CustomerPersistencePort.java
│   │       └── AddressPersistencePort.java
│   └── service/
│       ├── CustomerService.java
│       └── AddressService.java
│
├── domain/
│   ├── model/
│   │   ├── Customer.java
│   │   ├── CustomerStatus.java (enum)
│   │   ├── LoyaltyLevel.java   (enum)
│   │   ├── Address.java
│   │   └── ContactInfo.java (value object)
│   └── exception/
│       ├── CustomerException.java
│       └── LoyaltyRuleException.java
│
├── repository/
│   ├── CustomerRepository.java
│   └── AddressRepository.java
│
├── config/
│   ├── SecurityConfig.java
│   ├── MapperConfig.java
│   └── OpenApiConfig.java
│
└── resources/
    ├── application.yaml
    └── (sin migraciones - MongoDB usa colecciones dinámicas)
        ├── changelog-master.yaml
        └── changes/
            ├── 001-create-customer-table.yaml
            ├── 002-create-address-table.yaml
            └── 003-seed-initial-customers.yaml

✔ Igual a user-service y product-service
✔ Mantiene consistencia entre microservicios
✔ Deja campo listo para uso en órdenes y descuentos

🧩 2️⃣ Modelo de dominio sugerido

Nada de entidades anémicas — reglas van en el dominio.

👤 Customer

Campos recomendados:

id (uuid)
fullName
documentNumber
email
phone
loyaltyLevel (enum)
status (enum)
registeredAt
updatedAt

CustomerStatus
ACTIVE
INACTIVE
BLOCKED
DECEASED (valioso para edge cases)

🟡 LoyaltyLevel

NEW          → sin historial
REGULAR      → compras ocasionales
LOYAL        → cliente frecuente
VIP          → alto valor

⚠️ Importante:

👉 el nivel NO debe calcularse en el controller
👉 ni en la base de datos

Debe ser:

✔ Regla de Dominio
✔ Evolutiva
✔ Extensible al motor de descuentos

🏠 Address (objeto separado)

street
city
state
country
postalCode
isPrimary

Un cliente puede tener:

✔ múltiples direcciones
✔ dirección principal única

📞 ContactInfo (Value Object)

email
phone
Con validaciones en el constructor.

🟢 Regla 1 — Cambio de nivel debe ser controlado

Ejemplo:
NEW → REGULAR
REGULAR → LOYAL
LOYAL → VIP

Pero NO:
❌ NEW → VIP directo

🟢 Regla 2 — Cliente BLOCKED no puede subir nivel
if (status == BLOCKED && newLevel != REGULAR)
    throw new LoyaltyRuleException("Blocked customer cannot upgrade loyalty level");

🟢 Regla 3 — VIP requiere historial mínimo

Por ejemplo:

✔ número mínimo de órdenes
✔ monto total acumulado mínimo

(No es necesario implementarlo completo aún,
pero deja el hook documentado).

🟢 Regla 4 — Cliente INACTIVE no puede:

❌ crear órdenes
❌ acumular puntos
❌ recibir descuentos

Esto será usado en:

🔥 order-service
🔥 strategy de fidelidad

Y suma puntos por anticipación funcional.