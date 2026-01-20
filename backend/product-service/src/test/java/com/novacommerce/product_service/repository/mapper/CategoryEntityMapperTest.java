package com.novacommerce.product_service.repository.mapper;

import com.novacommerce.product_service.domain.model.Category;
import com.novacommerce.product_service.repository.entity.CategoryEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("CategoryEntityMapper Tests")
class CategoryEntityMapperTest {

    @Autowired
    private CategoryEntityMapper mapper;
    private CategoryEntity categoryEntity;
    private Category category;

    @BeforeEach
    void setUp() {
        
        categoryEntity = CategoryEntity.builder()
                .id("1")
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
    @DisplayName("Should map CategoryEntity to Category")
    void testToDomain() {
        Category mappedCategory = mapper.toDomain(categoryEntity);

        assertNotNull(mappedCategory);
        assertEquals(categoryEntity.getId(), mappedCategory.getId());
        assertEquals(categoryEntity.getName(), mappedCategory.getName());
        assertEquals(categoryEntity.getDescription(), mappedCategory.getDescription());
        assertEquals(categoryEntity.getStatus(), mappedCategory.getStatus());
    }

    @Test
    @DisplayName("Should map Category to CategoryEntity")
    void testToEntity() {
        CategoryEntity mappedEntity = mapper.toEntity(category);

        assertNotNull(mappedEntity);
        assertEquals(category.getId(), mappedEntity.getId());
        assertEquals(category.getName(), mappedEntity.getName());
        assertEquals(category.getDescription(), mappedEntity.getDescription());
        assertEquals(category.getStatus(), mappedEntity.getStatus());
    }

    @Test
    @DisplayName("Should handle null CategoryEntity")
    void testToDomainWithNull() {
        Category result = mapper.toDomain(null);
        assertNull(result);
    }

    @Test
    @DisplayName("Should handle null Category")
    void testToEntityWithNull() {
        CategoryEntity result = mapper.toEntity(null);
        assertNull(result);
    }

    @Test
    @DisplayName("Should handle null description in entity")
    void testNullDescriptionEntity() {
        categoryEntity.setDescription(null);
        Category mapped = mapper.toDomain(categoryEntity);
        assertNull(mapped.getDescription());
    }

    @Test
    @DisplayName("Should handle null description in domain")
    void testNullDescriptionDomain() {
        category.setDescription(null);
        CategoryEntity mapped = mapper.toEntity(category);
        assertNull(mapped.getDescription());
    }

    @Test
    @DisplayName("Should bidirectional mapping consistency")
    void testBidirectionalMapping() {
        Category domainCategory = mapper.toDomain(categoryEntity);
        CategoryEntity remappedEntity = mapper.toEntity(domainCategory);

        assertEquals(categoryEntity.getId(), remappedEntity.getId());
        assertEquals(categoryEntity.getName(), remappedEntity.getName());
        assertEquals(categoryEntity.getDescription(), remappedEntity.getDescription());
        assertEquals(categoryEntity.getStatus(), remappedEntity.getStatus());
    }

    @Test
    @DisplayName("Should handle status ACTIVE")
    void testStatusActive() {
        categoryEntity.setStatus("ACTIVE");
        Category mapped = mapper.toDomain(categoryEntity);
        assertEquals("ACTIVE", mapped.getStatus());
    }

    @Test
    @DisplayName("Should handle status INACTIVE")
    void testStatusInactive() {
        categoryEntity.setStatus("INACTIVE");
        Category mapped = mapper.toDomain(categoryEntity);
        assertEquals("INACTIVE", mapped.getStatus());
    }

    @Test
    @DisplayName("Should preserve all fields during round-trip mapping")
    void testRoundTripMapping() {
        // Domain -> Entity -> Domain
        Category original = Category.builder()
                .id("5")
                .name("Books")
                .description("Books and reading materials")
                .status("ACTIVE")
                .build();

        CategoryEntity entity = mapper.toEntity(original);
        Category remapped = mapper.toDomain(entity);

        assertEquals(original.getId(), remapped.getId());
        assertEquals(original.getName(), remapped.getName());
        assertEquals(original.getDescription(), remapped.getDescription());
        assertEquals(original.getStatus(), remapped.getStatus());
    }

    @Test
    @DisplayName("Should handle empty description")
    void testEmptyDescription() {
        categoryEntity.setDescription("");
        Category mapped = mapper.toDomain(categoryEntity);
        assertEquals("", mapped.getDescription());
    }
}
