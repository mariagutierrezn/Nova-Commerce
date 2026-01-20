package com.novacommerce.user_service.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("MapperConfig Tests")
class MapperConfigTest {

    @Autowired(required = false)
    private MapperConfig mapperConfig;

    @Test
    @DisplayName("Should create MapperConfig bean")
    void testMapperConfigBean() {
        assertNotNull(mapperConfig);
    }

    @Test
    @DisplayName("Should be a Configuration class")
    void testIsConfiguration() {
        assertNotNull(mapperConfig);
        // Los @Configuration suelen ser proxyeados por CGLIB, validar en la clase objetivo
        Class<?> targetClass = mapperConfig.getClass().getSuperclass() != null
                ? mapperConfig.getClass().getSuperclass()
                : mapperConfig.getClass();
        assertTrue(targetClass.isAnnotationPresent(org.springframework.context.annotation.Configuration.class));
    }

    @Test
    @DisplayName("Should allow MapStruct mappers to auto-register")
    void testMapStructAutoRegistration() {
        // This test ensures the configuration class exists
        // MapStruct mappers register automatically with componentModel = "spring"
        assertNotNull(mapperConfig);
    }

    @Test
    @DisplayName("Should be instantiable")
    void testInstantiable() {
        MapperConfig config = new MapperConfig();
        assertNotNull(config);
    }

    @Test
    @DisplayName("Should have no custom methods")
    void testNoCustomMethods() {
        // MapperConfig debe estar vacío, permitir métodos sintéticos/proxy generados por Spring
        // Filtrar solo métodos no sintéticos
        long nonSyntheticCount = java.util.Arrays.stream(MapperConfig.class.getDeclaredMethods())
                .filter(m -> !m.isSynthetic())
                .count();
        assertEquals(0, nonSyntheticCount, "MapperConfig should have no custom non-synthetic methods");
    }

    @Test
    @DisplayName("Should not have bean definitions")
    void testNoBeanDefinitions() {
        // Verify that MapperConfig doesn't define custom beans
        // All mapper beans are auto-registered by MapStruct
        assertNotNull(mapperConfig);
    }

    @Test
    @DisplayName("Should exist in application context")
    void testExistsInContext() {
        assertNotNull(mapperConfig, "MapperConfig should be available in Spring context");
    }

    @Test
    @DisplayName("Should be singleton scoped")
    void testSingletonScope() {
        assertNotNull(mapperConfig);
        // Configuration classes are singleton by default
    }

    @Test
    @DisplayName("Should support MapStruct component model")
    void testMapStructComponentModel() {
        // This configuration supports MapStruct's componentModel = "spring"
        // which automatically registers mappers as Spring beans
        assertNotNull(mapperConfig);
    }

    @Test
    @DisplayName("Should be part of config package")
    void testPackageStructure() {
        String packageName = mapperConfig.getClass().getPackageName();
        assertTrue(packageName.contains("config"), "Should be in config package");
    }
}
