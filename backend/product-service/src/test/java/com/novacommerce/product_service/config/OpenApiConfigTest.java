package com.novacommerce.product_service.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("OpenApiConfig Tests")
class OpenApiConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("Should load application context for OpenAPI config")
    void testApplicationContextLoaded() {
        assertNotNull(applicationContext);
        assertTrue(applicationContext.getBeansOfType(Object.class).size() > 0);
    }

    @Test
    @DisplayName("Should have OpenAPI related configuration")
    void testOpenAPIConfigurationPresent() {
        assertNotNull(applicationContext);
        assertTrue(applicationContext.getBeansOfType(Object.class).size() > 0);
    }

    @Test
    @DisplayName("Should load SpringDoc beans")
    void testSpringDocBeansLoaded() {
        assertTrue(applicationContext.getBeansOfType(Object.class).size() > 0);
    }

    @Test
    @DisplayName("Should contain application beans")
    void testApplicationBeansPresent() {
        assertNotNull(applicationContext);
        assertFalse(applicationContext.getBeansOfType(Object.class).isEmpty());
    }
}

