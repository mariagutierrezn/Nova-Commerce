package com.novacommerce.gateway.adapter.in.filter;

import com.novacommerce.gateway.adapter.out.header.HeaderUtils;
import com.novacommerce.gateway.application.port.TokenValidatorPort;
import com.novacommerce.gateway.config.SecurityProperties;
import com.novacommerce.gateway.domain.exception.AuthenticationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * Filtro global de autenticación (Adapter de entrada)
 * Valida JWT para rutas protegidas antes de enrutar a los microservicios
 */
@Slf4j
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final TokenValidatorPort tokenValidatorPort;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final SecurityProperties securityProperties;

    public JwtAuthenticationFilter(
            TokenValidatorPort tokenValidatorPort,
            SecurityProperties securityProperties) {
        this.tokenValidatorPort = tokenValidatorPort;
        this.securityProperties = securityProperties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        log.debug("Processing request for path: {}", path);

        // Permitir rutas públicas sin validación
        if (isPublicPath(path)) {
            log.debug("Public path detected, skipping JWT validation: {}", path);
            return chain.filter(exchange);
        }

        // Log de diagnóstico: listar nombres de headers recibidos
        if (log.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder("Headers recibidos en gateway para ").append(path).append(": [");
            request.getHeaders().forEach((name, values) -> sb.append(name).append(", "));
            sb.append("]");
            log.debug(sb.toString());
        }

        // Extraer token del header
        String token = HeaderUtils.extractToken(request);

        if (token == null) {
            log.warn("No JWT token found in request for protected path: {}", path);
            return sendErrorResponse(exchange, "Missing authentication token", HttpStatus.UNAUTHORIZED);
        }

        // Validar token
        if (!tokenValidatorPort.validateToken(token)) {
            log.warn("Invalid JWT token for path: {}", path);
            return sendErrorResponse(exchange, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
        }

        try {
            // Extraer información del token y inyectar en headers
            String username = tokenValidatorPort.extractUsername(token);
            String authorities = tokenValidatorPort.extractAuthorities(token);

            log.debug("JWT validated successfully for user: {} with authorities: {}", username, authorities);

            ServerHttpRequest modifiedRequest = HeaderUtils.addCustomHeaders(request, username, authorities);
            ServerWebExchange modifiedExchange = exchange.mutate().request(modifiedRequest).build();

            return chain.filter(modifiedExchange);

        } catch (AuthenticationException e) {
            log.error("Authentication error: {}", e.getMessage());
            return sendErrorResponse(exchange, e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("Error processing JWT token: {}", e.getMessage());
            return sendErrorResponse(exchange, "Token processing error", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Verifica si la ruta es pública (no requiere autenticación)
     */
    private boolean isPublicPath(String path) {
        // TEMPORAL: Permitir /api/customers/** sin autenticación para debug
        if (path.startsWith("/api/customers")) {
            return true;
        }
        // Permitir /api/discounts/** sin autenticación
        if (path.startsWith("/api/discounts")) {
            return true;
        }
        return securityProperties.getPublicPaths().stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    /**
     * Envía respuesta de error en formato JSON estructurado
     */
    private Mono<Void> sendErrorResponse(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String errorJson = String.format(
                "{\"error\":\"%s\",\"message\":\"%s\",\"status\":%d,\"timestamp\":\"%s\",\"path\":\"%s\"}",
                status.getReasonPhrase(),
                message,
                status.value(),
                LocalDateTime.now().toString(),
                exchange.getRequest().getURI().getPath()
        );

        DataBuffer buffer = response.bufferFactory()
                .wrap(errorJson.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
