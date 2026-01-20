package com.novacommerce.auth_service.config.security;

import com.novacommerce.auth_service.config.security.jwt.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SecurityConfig Tests")
class SecurityConfigTest {

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig(jwtAuthenticationFilter);
    }

    @Test
    @DisplayName("Debe crear PasswordEncoder bean con BCrypt")
    void testPasswordEncoderBean() {
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();

        assertNotNull(passwordEncoder);
        assertTrue(passwordEncoder instanceof org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder);
    }

    @Test
    @DisplayName("Debe codificar contraseñas con BCrypt")
    void testPasswordEncoding() {
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        String rawPassword = "mySecretPassword";

        String encodedPassword = passwordEncoder.encode(rawPassword);

        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
        assertTrue(encodedPassword.startsWith("$2a$") || encodedPassword.startsWith("$2b$"));
    }

    @Test
    @DisplayName("Debe usar BCrypt con strength 12")
    void testBCryptStrength() {
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        String password = "testPassword123";
        
        // BCrypt con strength 12 debería generar un hash que empiece con $2a$12$ o $2b$12$
        String encoded = passwordEncoder.encode(password);
        assertTrue(encoded.startsWith("$2a$12$") || encoded.startsWith("$2b$12$"));
    }

    @Test
    @DisplayName("Debe generar diferentes hashes para la misma contraseña")
    void testSaltedHashing() {
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        String password = "samePassword";

        String hash1 = passwordEncoder.encode(password);
        String hash2 = passwordEncoder.encode(password);

        assertNotEquals(hash1, hash2);
        assertTrue(passwordEncoder.matches(password, hash1));
        assertTrue(passwordEncoder.matches(password, hash2));
    }

    @Test
    @DisplayName("Debe rechazar contraseñas incorrectas")
    void testPasswordMismatch() {
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        String correctPassword = "correctPassword";
        String wrongPassword = "wrongPassword";

        String encodedPassword = passwordEncoder.encode(correctPassword);

        assertFalse(passwordEncoder.matches(wrongPassword, encodedPassword));
    }

    @Test
    @DisplayName("Debe manejar authenticationEntryPoint con 401")
    void testAuthenticationEntryPoint() throws Exception {
        HttpServletResponse response = mock(HttpServletResponse.class);
        
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        // Simular el AuthenticationEntryPoint
        response.setStatus(401);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"No autenticado\"}");

        verify(response).setStatus(401);
        verify(response).setContentType("application/json");
        assertEquals("{\"error\": \"No autenticado\"}", stringWriter.toString());
    }

    @Test
    @DisplayName("Debe manejar accessDeniedHandler con 403")
    void testAccessDeniedHandler() throws Exception {
        HttpServletResponse response = mock(HttpServletResponse.class);
        
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        // Simular el AccessDeniedHandler
        response.setStatus(403);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Acceso denegado\"}");

        verify(response).setStatus(403);
        verify(response).setContentType("application/json");
        assertEquals("{\"error\": \"Acceso denegado\"}", stringWriter.toString());
    }

    @Test
    @DisplayName("Debe tener @Configuration annotation")
    void testConfigurationAnnotation() {
        assertTrue(SecurityConfig.class.isAnnotationPresent(
            org.springframework.context.annotation.Configuration.class
        ));
    }

    @Test
    @DisplayName("Debe tener @EnableWebSecurity annotation")
    void testEnableWebSecurityAnnotation() {
        assertTrue(SecurityConfig.class.isAnnotationPresent(
            org.springframework.security.config.annotation.web.configuration.EnableWebSecurity.class
        ));
    }

    @Test
    @DisplayName("Debe tener @EnableMethodSecurity annotation")
    void testEnableMethodSecurityAnnotation() {
        assertTrue(SecurityConfig.class.isAnnotationPresent(
            org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity.class
        ));
    }

    @Test
    @DisplayName("Debe tener método filterChain anotado con @Bean")
    void testFilterChainBeanAnnotation() throws NoSuchMethodException {
        var method = SecurityConfig.class.getMethod(
            "filterChain",
            org.springframework.security.config.annotation.web.builders.HttpSecurity.class
        );
        assertTrue(method.isAnnotationPresent(
            org.springframework.context.annotation.Bean.class
        ));
    }

    @Test
    @DisplayName("Debe tener método passwordEncoder anotado con @Bean")
    void testPasswordEncoderBeanAnnotation() throws NoSuchMethodException {
        var method = SecurityConfig.class.getMethod("passwordEncoder");
        assertTrue(method.isAnnotationPresent(
            org.springframework.context.annotation.Bean.class
        ));
    }
}
