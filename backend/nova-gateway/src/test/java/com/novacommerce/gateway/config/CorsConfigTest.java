package com.novacommerce.gateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.cors.reactive.CorsWebFilter;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class CorsConfigTest {

    @Autowired
    private CorsConfig corsConfig;

    @Autowired
    private CorsWebFilter corsWebFilter;

    @Test
    void testCorsWebFilterBeanCreation() {
        assertNotNull(corsWebFilter, "CorsWebFilter bean should not be null");
        assertNotNull(corsConfig, "CorsConfig should not be null");
    }

    @Test
    void testCorsWebFilterConfiguration() {
        assertNotNull(corsWebFilter, "CorsWebFilter should be configured");
    }

    @Test
    void testCorsConfigBeanExists() {
        assertNotNull(corsConfig);
    }
}
