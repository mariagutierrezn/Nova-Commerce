package com.novacommerce.product_service.adapter.in.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    // Evitar que el contexto intente instanciar el mapper real
    @org.springframework.boot.test.mock.mockito.MockBean(name = "productEntityMapperImpl")
    private com.novacommerce.product_service.repository.mapper.ProductEntityMapper productEntityMapper;

    @Test
    @DisplayName("Should handle ResourceNotFoundException")
    @WithMockUser(roles = "ADMIN")
    void testHandleResourceNotFoundException() throws Exception {
        mockMvc.perform(get("/api/products/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Should return error response structure")
    @WithMockUser(roles = "ADMIN")
    void testErrorResponseStructure() throws Exception {
        mockMvc.perform(get("/api/products/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should handle validation errors")
    @WithMockUser(roles = "ADMIN")
    void testHandleValidationErrors() throws Exception {
        String invalidRequest = "{}";

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle unauthorized access")
    void testHandleUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/api/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should handle forbidden access")
    @WithMockUser(roles = "USER")
    void testHandleForbiddenAccess() throws Exception {
        // Send valid data to ensure authorization check happens (not validation error)
        String validRequest = "{\"name\":\"Product\",\"description\":\"Desc\",\"price\":100.00,\"productType\":\"PHYSICAL\",\"categoryId\":1,\"stockQuantity\":10,\"status\":\"ACTIVE\"}";
        
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRequest))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should handle category not found")
    @WithMockUser(roles = "ADMIN")
    void testHandleCategoryNotFound() throws Exception {
        mockMvc.perform(get("/api/categories/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should include error message in response")
    @WithMockUser(roles = "ADMIN")
    void testErrorMessageInResponse() throws Exception {
        mockMvc.perform(get("/api/products/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", notNullValue()));
    }

    @Test
    @DisplayName("Should handle bad request for invalid product type")
    @WithMockUser(roles = "ADMIN")
    void testHandleInvalidProductType() throws Exception {
        String request = "{\"name\":\"Product\",\"description\":\"Desc\",\"price\":100,\"productType\":\"INVALID\",\"categoryId\":1,\"stockQuantity\":10,\"status\":\"ACTIVE\"}";

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle null required fields")
    @WithMockUser(roles = "ADMIN")
    void testHandleNullRequiredFields() throws Exception {
        String request = "{\"description\":\"Desc\"}";

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isBadRequest());
    }
}
