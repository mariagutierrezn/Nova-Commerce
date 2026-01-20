package com.novacommerce.auth_service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AuthServiceApplication Tests")
class AuthServiceApplicationTest {

    @Test
    @DisplayName("Debe tener @SpringBootApplication annotation")
    void testSpringBootApplicationAnnotation() {
        assertTrue(AuthServiceApplication.class.isAnnotationPresent(SpringBootApplication.class));
    }

    @Test
    @DisplayName("Debe tener @EnableFeignClients annotation")
    void testEnableFeignClientsAnnotation() {
        assertTrue(AuthServiceApplication.class.isAnnotationPresent(EnableFeignClients.class));
    }

    @Test
    @DisplayName("Debe tener método main")
    void testMainMethodExists() throws NoSuchMethodException {
        var mainMethod = AuthServiceApplication.class.getMethod("main", String[].class);
        assertNotNull(mainMethod);
        assertTrue(java.lang.reflect.Modifier.isStatic(mainMethod.getModifiers()));
        assertTrue(java.lang.reflect.Modifier.isPublic(mainMethod.getModifiers()));
    }

    @Test
    @DisplayName("Debe tener constructor público")
    void testPublicConstructor() {
        assertDoesNotThrow(AuthServiceApplication::new);
    }

    @Test
    @DisplayName("Debe poder instanciarse")
    void testInstantiation() {
        AuthServiceApplication application = new AuthServiceApplication();
        assertNotNull(application);
    }
}
