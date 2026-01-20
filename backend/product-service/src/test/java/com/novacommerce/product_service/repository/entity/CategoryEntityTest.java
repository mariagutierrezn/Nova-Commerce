package com.novacommerce.product_service.repository.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CategoryEntity JPA Entity Tests")
class CategoryEntityTest {

    private CategoryEntity categoryEntity;

    @BeforeEach
    void setUp() {
        categoryEntity = CategoryEntity.builder()
                .id("1")
                .name("Electronics")
                .description("Electronic devices")
                .status("ACTIVE")
                .build();
    }

    @Test
    @DisplayName("Should create category entity with all fields")
    void testCategoryEntityCreation() {
        assertNotNull(categoryEntity);
        assertEquals("1", categoryEntity.getId());
        assertEquals("Electronics", categoryEntity.getName());
        assertEquals("Electronic devices", categoryEntity.getDescription());
        assertEquals("ACTIVE", categoryEntity.getStatus());
    }

    @Test
    @DisplayName("Should allow setting category name")
    void testSetName() {
        categoryEntity.setName("Computing");
        assertEquals("Computing", categoryEntity.getName());
    }

    @Test
    @DisplayName("Should allow setting category description")
    void testSetDescription() {
        categoryEntity.setDescription("Computing devices and accessories");
        assertEquals("Computing devices and accessories", categoryEntity.getDescription());
    }

    @Test
    @DisplayName("Should allow setting category status")
    void testSetStatus() {
        categoryEntity.setStatus("INACTIVE");
        assertEquals("INACTIVE", categoryEntity.getStatus());
    }

    @Test
    @DisplayName("Should allow setting category id")
    void testSetId() {
        categoryEntity.setId("10");
        assertEquals("10", categoryEntity.getId());
    }

    @Test
    @DisplayName("Should handle null description")
    void testNullDescription() {
        categoryEntity.setDescription(null);
        assertNull(categoryEntity.getDescription());
    }

    @Test
    @DisplayName("Should use builder pattern correctly")
    void testBuilderPattern() {
        CategoryEntity built = CategoryEntity.builder()
                .id("2")
                .name("Clothing")
                .description("Clothing and accessories")
                .status("ACTIVE")
                .build();

        assertEquals("2", built.getId());
        assertEquals("Clothing", built.getName());
    }

    @Test
    @DisplayName("Should create category entity with no-args constructor")
    void testNoArgsConstructor() {
        CategoryEntity entity = new CategoryEntity();
        assertNotNull(entity);
    }

    @Test
    @DisplayName("Should support all-args constructor")
    void testAllArgsConstructor() {
        CategoryEntity entity = new CategoryEntity("3", "Books", "Books and media", "ACTIVE");
        assertEquals("3", entity.getId());
        assertEquals("Books", entity.getName());
        assertEquals("Books and media", entity.getDescription());
        assertEquals("ACTIVE", entity.getStatus());
    }

    @Test
    @DisplayName("Should handle empty description")
    void testEmptyDescription() {
        categoryEntity.setDescription("");
        assertEquals("", categoryEntity.getDescription());
    }

    @Test
    @DisplayName("Should maintain all field values after modification")
    void testFieldsAfterModification() {
        categoryEntity.setId("100");
        categoryEntity.setName("Furniture");
        categoryEntity.setDescription("Home furniture");
        categoryEntity.setStatus("INACTIVE");

        assertEquals("100", categoryEntity.getId());
        assertEquals("Furniture", categoryEntity.getName());
        assertEquals("Home furniture", categoryEntity.getDescription());
        assertEquals("INACTIVE", categoryEntity.getStatus());
    }
}
