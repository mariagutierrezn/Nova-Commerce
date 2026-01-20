package com.novacommerce.product_service.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.novacommerce.product_service.adapter.in.web.dto.CategoryRequest;
import com.novacommerce.product_service.adapter.in.web.dto.CategoryResponse;
import com.novacommerce.product_service.adapter.in.web.mapper.CategoryDtoMapper;
import com.novacommerce.product_service.application.port.in.ManageCategoriesUseCase;
import com.novacommerce.product_service.domain.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("CategoryRestController Tests")
class CategoryRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ManageCategoriesUseCase manageCategoriesUseCase;

    @MockBean
    private CategoryDtoMapper categoryDtoMapper;

        // Evitar la creación del bean real de ProductEntityMapper durante el contexto
        @MockBean(name = "productEntityMapperImpl")
        private com.novacommerce.product_service.repository.mapper.ProductEntityMapper productEntityMapper;

    private Category category;
    private CategoryResponse categoryResponse;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id("1")
                .name("Electronics")
                .description("Electronic devices")
                .status("ACTIVE")
                .build();

        categoryResponse = CategoryResponse.builder()
                .id("1")
                .name("Electronics")
                .description("Electronic devices")
                .status("ACTIVE")
                .build();
    }

    @Test
    @DisplayName("Should get all categories with authentication")
    @WithMockUser(roles = "ADMIN")
    void testGetAllCategories() throws Exception {
        Page<Category> categoryPage = new PageImpl<>(List.of(category), PageRequest.of(0, 20), 1);

        when(manageCategoriesUseCase.getAllCategories(any())).thenReturn(categoryPage);
        when(categoryDtoMapper.toResponse(category)).thenReturn(categoryResponse);

        mockMvc.perform(get("/api/categories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Electronics")));

        verify(manageCategoriesUseCase, times(1)).getAllCategories(any());
    }

    @Test
    @DisplayName("Should get category by ID with authentication")
    @WithMockUser(roles = "ADMIN")
    void testGetCategoryById() throws Exception {
        when(manageCategoriesUseCase.getCategoryById("1")).thenReturn(category);
        when(categoryDtoMapper.toResponse(category)).thenReturn(categoryResponse);

        mockMvc.perform(get("/api/categories/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.name", is("Electronics")));

        verify(manageCategoriesUseCase, times(1)).getCategoryById("1");
    }

    @Test
    @DisplayName("Should create category with authentication and ADMIN role")
    @WithMockUser(roles = "ADMIN")
    void testCreateCategory() throws Exception {
        CategoryRequest request = CategoryRequest.builder()
                .name("Electronics")
                .description("Electronic devices")
                .status("ACTIVE")
                .build();

        when(categoryDtoMapper.toDomain(any(CategoryRequest.class))).thenReturn(category);
        when(manageCategoriesUseCase.createCategory(any(Category.class))).thenReturn(category);
        when(categoryDtoMapper.toResponse(category)).thenReturn(categoryResponse);

        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Electronics")));

        verify(manageCategoriesUseCase, times(1)).createCategory(any(Category.class));
    }

    @Test
    @DisplayName("Should update category with authentication and ADMIN role")
    @WithMockUser(roles = "ADMIN")
    void testUpdateCategory() throws Exception {
        CategoryRequest request = CategoryRequest.builder()
                .name("Computing")
                .description("Computing devices")
                .status("ACTIVE")
                .build();

        Category updatedCategory = category;
        updatedCategory.setName("Computing");

        CategoryResponse updatedResponse = categoryResponse;
        updatedResponse.setName("Computing");

        when(categoryDtoMapper.toDomain(any(CategoryRequest.class))).thenReturn(updatedCategory);
        when(manageCategoriesUseCase.updateCategory(anyString(), any(Category.class))).thenReturn(updatedCategory);
        when(categoryDtoMapper.toResponse(updatedCategory)).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Computing")));

        verify(manageCategoriesUseCase, times(1)).updateCategory(anyString(), any(Category.class));
    }

    @Test
    @DisplayName("Should delete category with authentication and ADMIN role")
    @WithMockUser(roles = "ADMIN")
    void testDeleteCategory() throws Exception {
        doNothing().when(manageCategoriesUseCase).deleteCategory("1");

        mockMvc.perform(delete("/api/categories/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(manageCategoriesUseCase, times(1)).deleteCategory("1");
    }

    @Test
    @DisplayName("Should deny access without authentication")
    void testAccessDeniedWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should deny POST without ADMIN role")
    @WithMockUser(roles = "USER")
    void testCreateCategoryDeniedWithoutAdminRole() throws Exception {
        CategoryRequest request = CategoryRequest.builder()
                .name("Electronics")
                .description("Electronic devices")
                .status("ACTIVE")
                .build();

        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should handle empty categories list")
    @WithMockUser(roles = "ADMIN")
    void testGetAllCategoriesEmpty() throws Exception {
        Page<Category> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);

        when(manageCategoriesUseCase.getAllCategories(any())).thenReturn(emptyPage);

        mockMvc.perform(get("/api/categories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));

        verify(manageCategoriesUseCase, times(1)).getAllCategories(any());
    }
}
