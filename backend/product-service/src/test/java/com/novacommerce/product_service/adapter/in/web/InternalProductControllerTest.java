package com.novacommerce.product_service.adapter.in.web;

import com.novacommerce.product_service.application.port.in.ManageProductsUseCase;
import com.novacommerce.product_service.domain.model.Product;
import com.novacommerce.product_service.domain.model.ProductType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("InternalProductController Tests")
class InternalProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ManageProductsUseCase manageProductsUseCase;

    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id("1")
                .name("Laptop")
                .description("High-performance laptop")
                .price(new BigDecimal("999.99"))
                .productType(ProductType.PHYSICAL)
                .categoryId("1")
                .stockQuantity(10)
                .status("ACTIVE")
                .build();
    }

    @Test
    @DisplayName("Should get product by ID from internal endpoint with valid API key")
    void testGetByIdInternalWithValidApiKey() throws Exception {
        when(manageProductsUseCase.getProductById("1")).thenReturn(product);

        mockMvc.perform(get("/internal/products/1")
                .header("X-Internal-API-Key", "nova-internal-service-key-2024")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.name", is("Laptop")))
                .andExpect(jsonPath("$.price", is(999.99)))
                .andExpect(jsonPath("$.stock", is(10)))
                .andExpect(jsonPath("$.status", is("ACTIVE")));

        verify(manageProductsUseCase, times(1)).getProductById("1");
    }

    @Test
    @DisplayName("Should deny access to internal endpoint without API key")
    void testGetByIdInternalWithoutApiKey() throws Exception {
        mockMvc.perform(get("/internal/products/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(manageProductsUseCase, never()).getProductById(anyString());
    }

    @Test
    @DisplayName("Should deny access to internal endpoint with invalid API key")
    void testGetByIdInternalWithInvalidApiKey() throws Exception {
        mockMvc.perform(get("/internal/products/1")
                .header("X-Internal-API-Key", "invalid-key")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(manageProductsUseCase, never()).getProductById(anyString());
    }

    @Test
    @DisplayName("Should return internal product response format")
    void testInternalProductResponseFormat() throws Exception {
        when(manageProductsUseCase.getProductById("1")).thenReturn(product);

        mockMvc.perform(get("/internal/products/1")
                .header("X-Internal-API-Key", "nova-internal-service-key-2024")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.price").exists())
                .andExpect(jsonPath("$.stock").exists())
                .andExpect(jsonPath("$.status").exists());
    }

    @Test
    @DisplayName("Should handle product not found in internal endpoint")
    void testGetByIdInternalNotFound() throws Exception {
        when(manageProductsUseCase.getProductById("999"))
                .thenThrow(new RuntimeException("Product not found"));

        mockMvc.perform(get("/internal/products/999")
                .header("X-Internal-API-Key", "nova-internal-service-key-2024")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("Should handle different product types in internal endpoint")
    void testGetByIdInternalDifferentTypes() throws Exception {
        for (ProductType type : ProductType.values()) {
            Product p = product;
            p.setProductType(type);

            when(manageProductsUseCase.getProductById("1")).thenReturn(p);

            mockMvc.perform(get("/internal/products/1")
                    .header("X-Internal-API-Key", "nova-internal-service-key-2024")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
    }

    @Test
    @DisplayName("Should expose product data to other microservices")
    void testInternalEndpointDataExposure() throws Exception {
        when(manageProductsUseCase.getProductById("1")).thenReturn(product);

        mockMvc.perform(get("/internal/products/1")
                .header("X-Internal-API-Key", "nova-internal-service-key-2024")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.name", is("Laptop")))
                .andExpect(jsonPath("$.status", is("ACTIVE")));
    }
}
