package com.novacommerce.product_service.adapter.in.web.mapper;

import com.novacommerce.product_service.adapter.in.web.dto.CategoryRequest;
import com.novacommerce.product_service.adapter.in.web.dto.CategoryResponse;
import com.novacommerce.product_service.domain.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("CategoryDtoMapper Tests")
class CategoryDtoMapperTest {

    @Autowired
    private CategoryDtoMapper mapper;

    private CategoryRequest categoryRequest;
    private Category category;

    @BeforeEach
    void setUp() {
        categoryRequest = CategoryRequest.builder()
                .name("Electronics")
                .description("Electronic devices")
                .status("ACTIVE")
                .build();

        category = Category.builder()
                .id("1")
                .name("Electronics")
                .description("Electronic devices")
                .status("ACTIVE")
                .build();
    }

    @Test
    @DisplayName("Should map CategoryRequest to Category")
    void testToDomain() {
        Category mappedCategory = mapper.toDomain(categoryRequest);

        assertNotNull(mappedCategory);
        assertEquals(categoryRequest.getName(), mappedCategory.getName());
        assertEquals(categoryRequest.getDescription(), mappedCategory.getDescription());
        assertEquals(categoryRequest.getStatus(), mappedCategory.getStatus());
    }

    @Test
    @DisplayName("Should map Category to CategoryResponse")
    void testToResponse() {
        CategoryResponse response = mapper.toResponse(category);

        assertNotNull(response);
        assertEquals(category.getId(), response.getId());
        assertEquals(category.getName(), response.getName());
        assertEquals(category.getDescription(), response.getDescription());
        assertEquals(category.getStatus(), response.getStatus());
    }

    @Test
    @DisplayName("Should handle null CategoryRequest")
    void testToDomainWithNull() {
        Category result = mapper.toDomain(null);
        assertNull(result);
    }

    @Test
    @DisplayName("Should handle null Category")
    void testToResponseWithNull() {
        CategoryResponse result = mapper.toResponse(null);
        assertNull(result);
    }

    @Test
    @DisplayName("Should handle null description in request")
    void testNullDescriptionRequest() {
        categoryRequest.setDescription(null);
        Category mapped = mapper.toDomain(categoryRequest);
        assertNull(mapped.getDescription());
    }

    @Test
    @DisplayName("Should handle status ACTIVE")
    void testStatusActive() {
        categoryRequest.setStatus("ACTIVE");
        Category mapped = mapper.toDomain(categoryRequest);
        assertEquals("ACTIVE", mapped.getStatus());
    }

    @Test
    @DisplayName("Should handle status INACTIVE")
    void testStatusInactive() {
        categoryRequest.setStatus("INACTIVE");
        Category mapped = mapper.toDomain(categoryRequest);
        assertEquals("INACTIVE", mapped.getStatus());
    }

    @Test
    @DisplayName("Should map response with all fields")
    void testResponseMapping() {
        CategoryResponse response = mapper.toResponse(category);
        assertEquals("1", response.getId());
        assertEquals("Electronics", response.getName());
        assertEquals("Electronic devices", response.getDescription());
        assertEquals("ACTIVE", response.getStatus());
    }

    @Test
    @DisplayName("Should handle empty description")
    void testEmptyDescription() {
        categoryRequest.setDescription("");
        Category mapped = mapper.toDomain(categoryRequest);
        assertEquals("", mapped.getDescription());
    }

    @Test
    @DisplayName("Should preserve status during mapping")
    void testStatusPreservation() {
        categoryRequest.setStatus("INACTIVE");
        Category mapped = mapper.toDomain(categoryRequest);
        assertEquals("INACTIVE", mapped.getStatus());
    }

    @Test
    @DisplayName("Should response map with id from category")
    void testResponseWithCategoryId() {
        Category category2 = Category.builder()
                .id("5")
                .name("Books")
                .description("Books")
                .status("ACTIVE")
                .build();

        CategoryResponse response = mapper.toResponse(category2);
        assertEquals("5", response.getId());
        assertEquals("Books", response.getName());
    }
}
