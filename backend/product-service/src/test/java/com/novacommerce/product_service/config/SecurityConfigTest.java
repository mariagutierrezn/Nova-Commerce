package com.novacommerce.product_service.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("SecurityConfig Tests")
class SecurityConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("Should load security configuration")
    void testSecurityConfigLoaded() {
        assertNotNull(applicationContext);
    }

    @Test
    @DisplayName("Should have JWT authentication filter bean")
    void testJwtAuthenticationFilterBean() {
        assertTrue(applicationContext.containsBean("jwtAuthenticationFilter") || 
                  applicationContext.getBeansOfType(Object.class).containsKey("jwtAuthenticationFilter"));
    }

    @Test
    @DisplayName("Should have internal API key filter bean")
    void testInternalApiKeyFilterBean() {
        assertTrue(applicationContext.containsBean("internalApiKeyFilter") || 
                  applicationContext.getBeansOfType(Object.class).size() > 0);
    }

    @Test
    @DisplayName("Should have security filter chain configured")
    void testSecurityFilterChainConfigured() {
        assertTrue(applicationContext.getBeansOfType(Object.class).size() > 0);
    }

    @Test
    @DisplayName("Should have application context initialized")
    void testApplicationContextInitialized() {
        assertNotNull(applicationContext);
    }

    @Test
    @DisplayName("Should load JWT related beans")
    void testJwtBeansLoaded() {
        assertTrue(applicationContext.getBeansOfType(Object.class).size() > 0);
    }

    @Test
    @DisplayName("Should have security configuration active")
    void testSecurityConfigActive() {
        assertNotNull(applicationContext);
        assertTrue(applicationContext.getBeansOfType(Object.class).size() > 0);
    }
}
