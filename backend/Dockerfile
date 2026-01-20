# Dockerfile común para servicios Spring Boot
ARG SERVICE_NAME
ARG SERVICE_PORT=8080

FROM maven:3.9-eclipse-temurin-17 AS build
ARG SERVICE_NAME
WORKDIR /app

# Copiar pom.xml del parent
COPY pom.xml .

# Copiar todos los módulos completos (necesario para el parent POM multi-módulo)
COPY nova-gateway ./nova-gateway
COPY auth-service ./auth-service
COPY user-service ./user-service
COPY product-service ./product-service
COPY customer-service ./customer-service
COPY order-service ./order-service
COPY notification-service ./notification-service

# Descargar dependencias del parent y todos los módulos
RUN mvn dependency:go-offline -B || true

# Compilar y empaquetar solo el servicio específico
RUN mvn clean package -pl ${SERVICE_NAME} -am -Dmaven.test.skip=true -B

# Imagen de runtime
FROM eclipse-temurin:17-jre-alpine
ARG SERVICE_NAME
ARG SERVICE_PORT=8080
WORKDIR /app

# Instalar wget para healthchecks
RUN apk add --no-cache wget

# Crear usuario no-root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar JAR desde build
COPY --from=build --chown=spring:spring /app/${SERVICE_NAME}/target/*.jar app.jar

EXPOSE ${SERVICE_PORT}

ENTRYPOINT ["java", "-jar", "app.jar"]
