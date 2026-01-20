package com.novacommerce.user_service.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "app.seed.enabled=true",
    "app.seed.admin.username=testadmin",
    "app.seed.admin.email=testadmin@nova.com",
    "app.seed.admin.password=testpass123"
})
@DisplayName("SeedProperties Tests")
class SeedPropertiesTest {

    @Autowired
    private SeedProperties seedProperties;

    @Test
    @DisplayName("Should load SeedProperties bean")
    void testBeanLoading() {
        assertNotNull(seedProperties);
    }

    @Test
    @DisplayName("Should have enabled property set to true")
    void testEnabledProperty() {
        assertTrue(seedProperties.isEnabled());
    }

    @Test
    @DisplayName("Should load admin configuration")
    void testAdminConfig() {
        SeedProperties.AdminConfig adminConfig = seedProperties.getAdmin();
        assertNotNull(adminConfig);
    }

    @Test
    @DisplayName("Should have admin username from properties")
    void testAdminUsername() {
        assertEquals("testadmin", seedProperties.getAdmin().getUsername());
    }

    @Test
    @DisplayName("Should have admin email from properties")
    void testAdminEmail() {
        assertEquals("testadmin@nova.com", seedProperties.getAdmin().getEmail());
    }

    @Test
    @DisplayName("Should have admin password from properties")
    void testAdminPassword() {
        assertEquals("testpass123", seedProperties.getAdmin().getPassword());
    }

    @Test
    @DisplayName("Should have default values when not specified")
    void testDefaultValues() {
        SeedProperties.AdminConfig adminConfig = new SeedProperties.AdminConfig();
        assertEquals("admin", adminConfig.getUsername());
        assertEquals("admin@nova.com", adminConfig.getEmail());
        assertEquals("admin123", adminConfig.getPassword());
    }

    @Test
    @DisplayName("Should allow setting enabled property")
    void testSetEnabledProperty() {
        seedProperties.setEnabled(false);
        assertFalse(seedProperties.isEnabled());

        seedProperties.setEnabled(true);
        assertTrue(seedProperties.isEnabled());
    }

    @Test
    @DisplayName("AdminConfig should be independent instance")
    void testAdminConfigIndependence() {
        SeedProperties.AdminConfig admin1 = seedProperties.getAdmin();
        SeedProperties.AdminConfig admin2 = new SeedProperties.AdminConfig();

        admin2.setUsername("admin2");
        admin2.setEmail("admin2@nova.com");

        assertNotEquals(admin1.getUsername(), admin2.getUsername());
        assertNotEquals(admin1.getEmail(), admin2.getEmail());
    }

    @Test
    @DisplayName("Should be ConfigurationProperties for app.seed prefix")
    void testConfigurationPropertiesPrefix() {
        assertNotNull(seedProperties);
        // Properties are loaded from app.seed prefix as defined in class annotation
        assertTrue(seedProperties.isEnabled());
    }
}
