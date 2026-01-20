package com.novacommerce.order_service.adapter.in.web;

import com.novacommerce.order_service.adapter.in.security.JwtAuthenticationFilter;
import com.novacommerce.order_service.adapter.in.security.JwtTokenValidator;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración para tests @WebMvcTest.
 * Proporciona mocks de beans de seguridad y deshabilita CSRF para facilitar testing.
 */
@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfiguration {

    @Bean
    @Primary
    public JwtTokenValidator jwtTokenValidator() {
        return Mockito.mock(JwtTokenValidator.class);
    }

    @Bean
    @Primary
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtTokenValidator validator) {
        return Mockito.mock(JwtAuthenticationFilter.class);
    }

    @Bean
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll());
        return http.build();
    }
}
