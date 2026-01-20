Actua com  arquitecto experto e ingeniero de desarrollo senior especialista en Java y spring Boot y eleabora todo lo necesario para la gestion de clientes en product-service, sigue el mismo estandar que has realizado para nova-gateway, auth-service, users-service, product-service y customer-service. Ten en cuenta estos microservicios para que el nuevo microservicio pueda trabajar en conjunto con ellos. 

🧱 1️⃣ Estructura propuesta — order-service

Seguimos el mismo estándar de los otros micros:

order-service/
├── adapter/
│   ├── in/
│   │   └── web/
│   │       ├── OrderRestController.java
│   │       └── GlobalExceptionHandler.java
│   ├── out/
│   │   ├── persistence/
│   │   │   ├── OrderPersistenceAdapter.java
│   │   │   └── OrderItemPersistenceAdapter.java
│   │   ├── customer/
│   │   │   └── CustomerClientAdapter.java
│   │   └── product/
│   │       └── ProductClientAdapter.java
│
├── application/
│   ├── port/
│   │   ├── in/
│   │   │   ├── CreateOrderUseCase.java
│   │   │   ├── GetOrderUseCase.java
│   │   │   └── UpdateOrderStatusUseCase.java
│   │   └── out/
│   │       ├── OrderPersistencePort.java
│   │       ├── OrderItemPersistencePort.java
│   │       ├── CustomerValidationPort.java
│   │       └── ProductValidationPort.java
│   └── service/
│       ├── OrderService.java
│       └── OrderStatusService.java
│
├── domain/
│   ├── model/
│   │   ├── Order.java
│   │   ├── OrderItem.java
│   │   ├── OrderStatus.java (enum)
│   │   ├── Money.java (Value Object)
│   │   ├── DiscountResult.java
│   │   └── DiscountContext.java
│   ├── discount/
│   │   ├── DiscountStrategy.java   ⭐
│   │   ├── LoyaltyDiscountStrategy.java
│   │   ├── SeasonDiscountStrategy.java
│   │   └── ProductTypeDiscountStrategy.java
│   └── exception/
│       ├── OrderException.java
│       └── BusinessRuleException.java
│
├── repository/
│   ├── OrderRepository.java
│   └── OrderItemRepository.java
│
└── resources/
    ├── application.yaml
    └── (sin migraciones - MongoDB usa colecciones dinámicas)
        ├── changelog-master.yaml
        └── changes/
            ├── 001-create-orders-table.yaml
            ├── 002-create-order-items-table.yaml
            └── 003-seed-sample-orders.yaml


Este diseño deja:

✔ dominio aislado
✔ pricing y descuentos en módulo propio
✔ adaptadores para comunicación con productos y clientes
✔ extensible sin romper código existente (OCP)


🧩 2️⃣ Modelo de dominio recomendado

Nada de anémicos — las reglas de negocio viven aquí.

🟢 Order

Campos:
id
customerId
status
totalBeforeDiscount
discountTotal
totalAfterDiscount
createdAt
updatedAt
items (lista de OrderItem)

🟢 OrderItem
productId
productName
quantity
unitPrice
subTotal
productType

🟢 OrderStatus
CREATED
PAID
CANCELLED
SHIPPED
COMPLETED

🟢 Money (Value Object)

Evita BigDecimal suelto por todo lado.

Valida:

✔ no negativos
✔ precisión
✔ operaciones seguras


⭐ Motor de Descuentos — Strategy Pattern

Cada estrategia independiente:
LoyaltyDiscountStrategy
SeasonDiscountStrategy
ProductTypeDiscountStrategy

DiscountStrategy {
   DiscountResult apply(DiscountContext context);
}

El contexto incluye:

✔ cliente
✔ productos
✔ totales
✔ fidelidad
✔ temporada

Luego combinamos los resultados.

🧠 3️⃣ Reglas de negocio mínimas (obligatorias)

Estas son claves para romper el “happy path”.
❌ No permitir crear orden si:

Customer está:

INACTIVE

BLOCKED

❌ No permitir:

cantidad = 0

precio negativo

producto INACTIVE

stock insuficiente (mínimo validar existencia)

🟡 Regla de totales

Siempre recalcular en dominio — nunca confiar en request.

🟡 Regla de descuentos

Si ninguna estrategia aplica:

✔ totalAfterDiscount == totalBeforeDiscount

Nada de descuentos mágicos.

🟡 Edge cases recomendados (para tests)

✔ cliente sin nivel de fidelidad → sin descuento
✔ productos mixtos → descuento por tipo parcial
✔ temporada activa → aplicar múltiple estrategia
✔ VIP pero producto no elegible → no aplica