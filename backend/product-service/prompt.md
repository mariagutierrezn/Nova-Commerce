Actua com  arquitecto experto e ingeniero de desarrollo senior especialista en Java y spring Boot y eleabora todo lo necesario para la gestion de productos y categorias en product-service, sigue el mismo estandar que has realizado para nova-gateway, auth-service y users-service y ten en cuenta estos microservicios para que el nuevo microservicio pueda trabajar en conjunto con ellos. 

🧱 📦 Estructura propuesta — product-service

Misma base que user-service:
product-service/
├── adapter/
│   ├── in/
│   │   └── web/
│   │       ├── ProductRestController.java
│   │       └── CategoryRestController.java
│   ├── out/
│   │   └── persistence/
│   │       ├── ProductPersistenceAdapter.java
│   │       └── CategoryPersistenceAdapter.java
│
├── application/
│   ├── port/
│   │   ├── in/
│   │   │   ├── ManageProductsUseCase.java
│   │   │   └── ManageCategoriesUseCase.java
│   │   └── out/
│   │       ├── ProductPersistencePort.java
│   │       └── CategoryPersistencePort.java
│   └── service/
│       ├── ProductService.java
│       └── CategoryService.java
│
├── domain/
│   ├── model/
│   │   ├── Product.java
│   │   ├── Category.java
│   │   └── ProductType.java (enum)
│   └── exception/
│       └── ProductException.java
│
├── repository/
│   ├── ProductRepository.java
│   └── CategoryRepository.java
│
├── config/
│   ├── MapperConfig.java
│   ├── OpenApiConfig.java
│   └── SecurityConfig.java  (*solo validación JWT / API Key interna)
│
└── resources/
    ├── application.yaml
    └── (sin migraciones - MongoDB usa colecciones dinámicas)
        ├── changelog-master.yaml
        └── changes/
            ├── 001-create-category-table.yaml
            ├── 002-create-product-table.yaml
            └── 003-seed-initial-products.yaml (opcional)

Mantiene:

✔ adapters IN / OUT
✔ ports
✔ domain model puro
✔ persistence aislada
✔ MongoDB con colecciones dinámicas
✔ extensible para descuentos y pricing después

🧩 Modelo de Dominio sugerido
Category

id
name
description
status

Product

id
name
description
price
productType (enum)
categoryId
stockQuantity
status

🗄️ MongoDB — colecciones dinámicas
databaseChangeLog:
  - include:
      file: changes/001-create-category-table.yaml
  - include:
      file: changes/002-create-product-table.yaml
  - include:
      file: changes/003-seed-initial-products.yaml


🎯 Endpoints propuestos (MVP limpio)
Products

GET    /api/products
GET    /api/products/{id}
POST   /api/products
PUT    /api/products/{id}
DELETE /api/products/{id}

GET    /api/products/category/{categoryId}

Categories
GET    /api/categories
POST   /api/categories
GET    /api/categories/{id}
PUT    /api/categories/{id}
DELETE /api/categories/{id}

Importante:

no meter lógica en controllers
no usar mappers automáticos sin puerto
evitar if/else anidados
preparar el diseño pensando en extensiones de pricing y discounts