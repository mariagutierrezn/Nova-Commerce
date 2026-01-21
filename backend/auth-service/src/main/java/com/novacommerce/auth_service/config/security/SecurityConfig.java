package com.novacommerce.auth_service.config.security;


import com.novacommerce.auth_service.config.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de seguridad de Spring Security.
 * Establece las políticas de autenticación, autorización y filtros de seguridad.
 * Implementa JWT stateless (sin sesiones).
 * 
 * IMPORTANTE: 
 * - NO usa UserDetailsService (auth-service no maneja BD de usuarios)
 * - La autenticación se delega a user-service vía Feign Client
 * - Solo valida JWT para endpoints protegidos
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configura el PasswordEncoder con BCrypt.
     * Usa un strength de 12 para mayor seguridad.
     *
     * @return el BCryptPasswordEncoder configurado
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Configura la cadena de filtros de seguridad.
     * Define qué endpoints son públicos y cuáles requieren autenticación.
     * Implementa autenticación stateless con JWT.
     *
     * @param http el HttpSecurity
     * @return el SecurityFilterChain configurado
     * @throws Exception si ocurre un error
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // Endpoints públicos de autenticación
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/public/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/refresh").permitAll()
                // Actuator endpoints para healthcheck
                .requestMatchers("/actuator/**").permitAll()
                // Documentación Swagger
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/swagger-ui.html").permitAll()
                .requestMatchers("/webjars/**").permitAll()
                // Endpoint de validación de JWT (requiere autenticación)
                .requestMatchers(HttpMethod.GET, "/api/auth/validate").authenticated()
                // Cualquier otra solicitud requiere autenticación
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(401);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"No autenticado\"}");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(403);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Acceso denegado\"}");
                }))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
