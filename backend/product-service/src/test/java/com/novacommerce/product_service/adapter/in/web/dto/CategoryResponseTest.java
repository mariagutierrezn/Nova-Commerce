package com.novacommerce.product_service.adapter.in.web.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CategoryResponse DTO Tests")
class CategoryResponseTest {

    private CategoryResponse response;

    @BeforeEach
    void setUp() {
        response = CategoryResponse.builder()
                .id("1")
                .name("Electronics")
                .description("Electronic devices")
                .status("ACTIVE")
                .build();
    }

    @Test
    @DisplayName("Should create CategoryResponse with all fields")
    void testCategoryResponseCreation() {
        assertNotNull(response);
        assertEquals("1", response.getId());
        assertEquals("Electronics", response.getName());
        assertEquals("Electronic devices", response.getDescription());
        assertEquals("ACTIVE", response.getStatus());
    }

    @Test
    @DisplayName("Should allow setting id")
    void testSetId() {
        response.setId("2");
        assertEquals("2", response.getId());
    }

    @Test
    @DisplayName("Should allow setting name")
    void testSetName() {
        response.setName("Computing");
        assertEquals("Computing", response.getName());
    }

    @Test
    @DisplayName("Should allow setting description")
    void testSetDescription() {
        response.setDescription("Computing devices");
        assertEquals("Computing devices", response.getDescription());
    }

    @Test
    @DisplayName("Should allow setting status")
    void testSetStatus() {
        response.setStatus("INACTIVE");
        assertEquals("INACTIVE", response.getStatus());
    }

    @Test
    @DisplayName("Should handle null description")
    void testNullDescription() {
        response.setDescription(null);
        assertNull(response.getDescription());
    }

    @Test
    @DisplayName("Should use builder pattern")
    void testBuilderPattern() {
        CategoryResponse built = CategoryResponse.builder()
                .id("2")
                .name("Clothing")
                .description("Clothing and fashion")
                .status("ACTIVE")
                .build();

        assertEquals("2", built.getId());
        assertEquals("Clothing", built.getName());
    }

    @Test
    @DisplayName("Should handle empty description")
    void testEmptyDescription() {
        response.setDescription("");
        assertEquals("", response.getDescription());
    }

    @Test
    @DisplayName("Should create with no-args constructor")
    void testNoArgsConstructor() {
        CategoryResponse empty = new CategoryResponse();
        assertNotNull(empty);
    }

    @Test
    @DisplayName("Should create with all-args constructor")
    void testAllArgsConstructor() {
        CategoryResponse allArgs = new CategoryResponse(
                "3",
                "Books",
                "Books and media",
                "ACTIVE"
        );
        assertEquals("3", allArgs.getId());
        assertEquals("Books", allArgs.getName());
        assertEquals("ACTIVE", allArgs.getStatus());
    }

    @Test
    @DisplayName("Should handle all statuses")
    void testAllStatuses() {
        response.setStatus("ACTIVE");
        assertEquals("ACTIVE", response.getStatus());

        response.setStatus("INACTIVE");
        assertEquals("INACTIVE", response.getStatus());
    }
}
