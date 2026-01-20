package com.novacommerce.product_service.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.novacommerce.product_service.adapter.in.web.dto.ProductRequest;
import com.novacommerce.product_service.adapter.in.web.dto.ProductResponse;
import com.novacommerce.product_service.adapter.in.web.mapper.ProductDtoMapper;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("ProductRestController Tests")
class ProductRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ManageProductsUseCase manageProductsUseCase;

    @MockBean
    private ProductDtoMapper productDtoMapper;

    private Product product;
    private ProductResponse productResponse;

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

        productResponse = ProductResponse.builder()
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
    @DisplayName("Should get all products with authentication")
    @WithMockUser(roles = "ADMIN")
    void testGetAllProducts() throws Exception {
        Page<Product> productPage = new PageImpl<>(List.of(product), PageRequest.of(0, 20), 1);

        when(manageProductsUseCase.getAllProducts(any())).thenReturn(productPage);
        when(productDtoMapper.toResponse(product)).thenReturn(productResponse);

        mockMvc.perform(get("/api/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Laptop")));

        verify(manageProductsUseCase, times(1)).getAllProducts(any());
    }

    @Test
    @DisplayName("Should get product by ID with authentication")
    @WithMockUser(roles = "ADMIN")
    void testGetProductById() throws Exception {
        when(manageProductsUseCase.getProductById("1")).thenReturn(product);
        when(productDtoMapper.toResponse(product)).thenReturn(productResponse);

        mockMvc.perform(get("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.name", is("Laptop")));

        verify(manageProductsUseCase, times(1)).getProductById("1");
    }

    @Test
    @DisplayName("Should create product with authentication and ADMIN role")
    @WithMockUser(roles = "ADMIN")
    void testCreateProduct() throws Exception {
        ProductRequest request = ProductRequest.builder()
                .name("Laptop")
                .description("High-performance laptop")
                .price(new BigDecimal("999.99"))
                .productType(ProductType.PHYSICAL)
                .categoryId("1")
                .stockQuantity(10)
                .status("ACTIVE")
                .build();

        when(productDtoMapper.toDomain(any(ProductRequest.class))).thenReturn(product);
        when(manageProductsUseCase.createProduct(any(Product.class))).thenReturn(product);
        when(productDtoMapper.toResponse(product)).thenReturn(productResponse);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Laptop")));

        verify(manageProductsUseCase, times(1)).createProduct(any(Product.class));
    }

    @Test
    @DisplayName("Should update product with authentication and ADMIN role")
    @WithMockUser(roles = "ADMIN")
    void testUpdateProduct() throws Exception {
        ProductRequest request = ProductRequest.builder()
                .name("Gaming Laptop")
                .description("High-performance gaming laptop")
                .price(new BigDecimal("1299.99"))
                .productType(ProductType.PHYSICAL)
                .categoryId("1")
                .stockQuantity(10)
                .status("ACTIVE")
                .build();

        Product updatedProduct = product;
        updatedProduct.setName("Gaming Laptop");

        ProductResponse updatedResponse = productResponse;
        updatedResponse.setName("Gaming Laptop");

        when(productDtoMapper.toDomain(any(ProductRequest.class))).thenReturn(updatedProduct);
        when(manageProductsUseCase.updateProduct(anyString(), any(Product.class))).thenReturn(updatedProduct);
        when(productDtoMapper.toResponse(updatedProduct)).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Gaming Laptop")));

        verify(manageProductsUseCase, times(1)).updateProduct(anyString(), any(Product.class));
    }

    @Test
    @DisplayName("Should delete product with authentication and ADMIN role")
    @WithMockUser(roles = "ADMIN")
    void testDeleteProduct() throws Exception {
        doNothing().when(manageProductsUseCase).deleteProduct("1");

        mockMvc.perform(delete("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(manageProductsUseCase, times(1)).deleteProduct("1");
    }

    @Test
    @DisplayName("Should get products by category with authentication")
    @WithMockUser(roles = "ADMIN")
    void testGetProductsByCategory() throws Exception {
        Page<Product> productPage = new PageImpl<>(List.of(product), PageRequest.of(0, 20), 1);

        when(manageProductsUseCase.getProductsByCategoryId(eq("1"), any())).thenReturn(productPage);
        when(productDtoMapper.toResponse(product)).thenReturn(productResponse);

        mockMvc.perform(get("/api/products/category/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        verify(manageProductsUseCase, times(1)).getProductsByCategoryId(eq("1"), any());
    }

    @Test
    @DisplayName("Should deny access without authentication")
    void testAccessDeniedWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should deny POST without ADMIN role")
    @WithMockUser(roles = "USER")
    void testCreateProductDeniedWithoutAdminRole() throws Exception {
        ProductRequest request = ProductRequest.builder()
                .name("Laptop")
                .description("High-performance laptop")
                .price(new BigDecimal("999.99"))
                .productType(ProductType.PHYSICAL)
                .categoryId("1")
                .stockQuantity(10)
                .status("ACTIVE")
                .build();

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
