package com.novacommerce.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.novacommerce.gateway.routing.RouteConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * Configuración de rutas del API Gateway
 * Define el enrutamiento de peticiones hacia los microservicios backend
 */
@Slf4j
@Configuration
public class GatewayConfig {

    @Value("${gateway.routes.auth-service.uri}")
    private String authServiceUri;

    @Value("${gateway.routes.auth-service.path}")
    private String authServicePath;

    @Value("${gateway.routes.user-service-users.uri}")
    private String userServiceUri;

    @Value("${gateway.routes.user-service-users.path}")
    private String userServiceUsersPath;

    @Value("${gateway.routes.user-service-roles.path}")
    private String userServiceRolesPath;

    @Value("${gateway.routes.product-service-products.uri}")
    private String productServiceUri;

    @Value("${gateway.routes.product-service-products.path}")
    private String productServiceProductsPath;

    @Value("${gateway.routes.product-service-categories.path}")
    private String productServiceCategoriesPath;

    @Value("${gateway.routes.product-service-public.path}")
    private String productServicePublicPath;

    @Value("${gateway.routes.product-service-images.path:}")
    private String productServiceImagesPath;

    @Value("${gateway.routes.customer-service-customers.uri}")
    private String customerServiceUri;

    @Value("${gateway.routes.customer-service-customers.path}")
    private String customerServiceCustomersPath;

    @Value("${gateway.routes.order-service-orders.uri}")
    private String orderServiceUri;

    @Value("${gateway.routes.order-service-orders.path}")
    private String orderServiceOrdersPath;

    @Value("${gateway.routes.order-service-discounts.path}")
    private String orderServiceDiscountsPath;

    @Value("${gateway.routes.notification-service-notifications.uri}")
    private String notificationServiceUri;

    @Value("${gateway.routes.notification-service-notifications.path}")
    private String notificationServiceNotificationsPath;

    @Value("${gateway.routes.notification-service-websocket.uri}")
    private String notificationServiceWsUri;

    @Value("${gateway.routes.notification-service-websocket.path}")
    private String notificationServiceWsPath;

    @Value("${gateway.routes.notification-service-chat.uri}")
    private String notificationServiceChatUri;

    @Value("${gateway.routes.notification-service-chat.path}")
    private String notificationServiceChatPath;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        log.info("Configuring Gateway routes");
        log.info("Route: {} -> {}", authServicePath, authServiceUri);
        log.info("Route: {} -> {}", userServiceUsersPath, userServiceUri);
        log.info("Route: {} -> {}", userServiceRolesPath, userServiceUri);
        log.info("Route: {} -> {}", productServiceProductsPath, productServiceUri);
        log.info("Route: {} -> {}", productServiceCategoriesPath, productServiceUri);
        log.info("Route: {} -> {}", productServicePublicPath, productServiceUri);
        log.info("Route: {} -> {}", productServiceImagesPath, productServiceUri);
        log.info("Route: {} -> {}", customerServiceCustomersPath, customerServiceUri);
        log.info("Route: {} -> {}", orderServiceOrdersPath, orderServiceUri);
        log.info("Route: {} -> {}", orderServiceDiscountsPath, orderServiceUri);
        log.info("Route: {} -> {}", notificationServiceNotificationsPath, notificationServiceUri);
        log.info("Route: {} (WebSocket) -> {}", notificationServiceWsPath, notificationServiceWsUri);
        log.info("Route: {} (Chat) -> {}", notificationServiceChatPath, notificationServiceChatUri);

        return builder.routes()
                .route(RouteConstants.AUTH_SERVICE_ID, r -> r
                        .path(authServicePath)
                        .filters(f -> f
                                .stripPrefix(0)
                                .removeRequestHeader("Cookie"))
                        .uri(authServiceUri))
                .route("user-service-users", r -> r
                        .path(userServiceUsersPath)
                        .filters(f -> f
                                .stripPrefix(0)
                                .removeRequestHeader("Cookie"))
                        .uri(userServiceUri))
                .route("user-service-roles", r -> r
                        .path(userServiceRolesPath)
                        .filters(f -> f
                                .stripPrefix(0)
                                .removeRequestHeader("Cookie"))
                        .uri(userServiceUri))
                .route("product-service-products", r -> r
                        .path(productServiceProductsPath)
                        .filters(f -> f
                                .stripPrefix(0)
                                .removeRequestHeader("Cookie"))
                        .uri(productServiceUri))
                .route("product-service-categories", r -> r
                        .path(productServiceCategoriesPath)
                        .filters(f -> f
                                .stripPrefix(0)
                                .removeRequestHeader("Cookie"))
                        .uri(productServiceUri))
                .route("product-service-public", r -> r
                        .path(productServicePublicPath)
                        .filters(f -> f
                                .stripPrefix(0)
                                .removeRequestHeader("Cookie"))
                        .uri(productServiceUri))
                .route("product-service-images", r -> r
                        .path(productServiceImagesPath)
                        .filters(f -> f
                                .stripPrefix(0)
                                .removeRequestHeader("Cookie"))
                        .uri(productServiceUri))
                .route("customer-service-customers", r -> r
                        .path(customerServiceCustomersPath)
                        .filters(f -> f
                                .stripPrefix(0)
                                .removeRequestHeader("Cookie"))
                        .uri(customerServiceUri))
                .route("order-service-orders", r -> r
                        .path(orderServiceOrdersPath)
                        .filters(f -> f
                                .stripPrefix(0)
                                .removeRequestHeader("Cookie"))
                        .uri(orderServiceUri))
                .route("order-service-discounts", r -> r
                        .path(orderServiceDiscountsPath)
                        .filters(f -> f
                                .stripPrefix(0)
                                .removeRequestHeader("Cookie"))
                        .uri(orderServiceUri))
                .route("notification-service-notifications", r -> r
                        .path(notificationServiceNotificationsPath)
                        .filters(f -> f
                                .stripPrefix(0)
                                .removeRequestHeader("Cookie"))
                        .uri(notificationServiceUri))
                .route("notification-service-websocket", r -> r
                        .path(notificationServiceWsPath)
                        .filters(f -> f
                                .stripPrefix(0)
                                .removeRequestHeader("Cookie"))
                        .uri(notificationServiceWsUri))
                .route("notification-service-chat", r -> r
                        .path(notificationServiceChatPath)
                        .filters(f -> f
                                .stripPrefix(0)
                                .removeRequestHeader("Cookie"))
                        .uri(notificationServiceChatUri))
                .build();
    }
}
