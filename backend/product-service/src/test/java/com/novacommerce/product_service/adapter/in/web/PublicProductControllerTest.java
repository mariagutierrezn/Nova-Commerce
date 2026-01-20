package com.novacommerce.product_service.adapter.in.web;

import com.novacommerce.product_service.adapter.in.web.dto.PublicProductResponse;
import com.novacommerce.product_service.adapter.in.web.mapper.PublicProductDtoMapper;
import com.novacommerce.product_service.application.port.in.GetPublicProductsUseCase;
import com.novacommerce.product_service.domain.model.Product;
import com.novacommerce.product_service.domain.model.ProductType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("PublicProductController Tests")
class PublicProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetPublicProductsUseCase getPublicProductsUseCase;

    @MockBean
    private PublicProductDtoMapper publicProductDtoMapper;

    private Product product1;
    private Product product2;
    private PublicProductResponse response1;
    private PublicProductResponse response2;

    @BeforeEach
    void setUp() {
        product1 = Product.builder()
                .id("1")
                .name("Laptop")
                .description("High-performance laptop")
                .price(new BigDecimal("999.99"))
                .productType(ProductType.PHYSICAL)
                .categoryId("1")
                .stockQuantity(10)
                .status("ACTIVE")
                .build();

        product2 = Product.builder()
                .id("2")
                .name("Mouse")
                .description("Wireless mouse")
                .price(new BigDecimal("25.99"))
                .productType(ProductType.PHYSICAL)
                .categoryId("2")
                .stockQuantity(50)
                .status("ACTIVE")
                .build();

        response1 = PublicProductResponse.builder()
                .id("1")
                .name("Laptop")
                .description("High-performance laptop")
                .price(new BigDecimal("999.99"))
                .productType(ProductType.PHYSICAL)
                .build();

        response2 = PublicProductResponse.builder()
                .id("2")
                .name("Mouse")
                .description("Wireless mouse")
                .price(new BigDecimal("25.99"))
                .productType(ProductType.PHYSICAL)
                .build();
    }

    @Test
    @DisplayName("Should get home products without authentication")
    void testGetHomeProductsNoAuth() throws Exception {
        List<Product> products = Arrays.asList(product1, product2);
        List<PublicProductResponse> responses = Arrays.asList(response1, response2);

        when(getPublicProductsUseCase.getPublicHomeProducts(12)).thenReturn(products);
        when(publicProductDtoMapper.toPublicResponseList(products)).thenReturn(responses);

        mockMvc.perform(get("/api/public/products/home"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Laptop")))
                .andExpect(jsonPath("$[0].price", is(999.99)))
                .andExpect(jsonPath("$[1].name", is("Mouse")))
                .andExpect(jsonPath("$[1].price", is(25.99)));

        verify(getPublicProductsUseCase, times(1)).getPublicHomeProducts(12);
        verify(publicProductDtoMapper, times(1)).toPublicResponseList(products);
    }

    @Test
    @DisplayName("Should return empty list when no products available")
    void testGetHomeProductsEmpty() throws Exception {
        when(getPublicProductsUseCase.getPublicHomeProducts(12)).thenReturn(Collections.emptyList());
        when(publicProductDtoMapper.toPublicResponseList(Collections.emptyList())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/public/products/home"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(getPublicProductsUseCase, times(1)).getPublicHomeProducts(12);
    }

    @Test
    @DisplayName("Should not expose categoryId in response")
    void testCategoryIdNotExposed() throws Exception {
        List<Product> products = Arrays.asList(product1);
        List<PublicProductResponse> responses = Arrays.asList(response1);

        when(getPublicProductsUseCase.getPublicHomeProducts(12)).thenReturn(products);
        when(publicProductDtoMapper.toPublicResponseList(products)).thenReturn(responses);

        mockMvc.perform(get("/api/public/products/home"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryId").doesNotExist());

        verify(getPublicProductsUseCase, times(1)).getPublicHomeProducts(12);
    }

    @Test
    @DisplayName("Should not expose stockQuantity in response")
    void testStockQuantityNotExposed() throws Exception {
        List<Product> products = Arrays.asList(product1);
        List<PublicProductResponse> responses = Arrays.asList(response1);

        when(getPublicProductsUseCase.getPublicHomeProducts(12)).thenReturn(products);
        when(publicProductDtoMapper.toPublicResponseList(products)).thenReturn(responses);

        mockMvc.perform(get("/api/public/products/home"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].stockQuantity").doesNotExist());

        verify(getPublicProductsUseCase, times(1)).getPublicHomeProducts(12);
    }

    @Test
    @DisplayName("Should not expose status in response")
    void testStatusNotExposed() throws Exception {
        List<Product> products = Arrays.asList(product1);
        List<PublicProductResponse> responses = Arrays.asList(response1);

        when(getPublicProductsUseCase.getPublicHomeProducts(12)).thenReturn(products);
        when(publicProductDtoMapper.toPublicResponseList(products)).thenReturn(responses);

        mockMvc.perform(get("/api/public/products/home"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").doesNotExist());

        verify(getPublicProductsUseCase, times(1)).getPublicHomeProducts(12);
    }

    @Test
    @DisplayName("Should return products with correct structure")
    void testProductStructure() throws Exception {
        List<Product> products = Arrays.asList(product1);
        List<PublicProductResponse> responses = Arrays.asList(response1);

        when(getPublicProductsUseCase.getPublicHomeProducts(12)).thenReturn(products);
        when(publicProductDtoMapper.toPublicResponseList(products)).thenReturn(responses);

        mockMvc.perform(get("/api/public/products/home"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is("1")))
                .andExpect(jsonPath("$[0].name", is("Laptop")))
                .andExpect(jsonPath("$[0].description", is("High-performance laptop")))
                .andExpect(jsonPath("$[0].price", is(999.99)))
                .andExpect(jsonPath("$[0].productType", is("PHYSICAL")));

        verify(getPublicProductsUseCase, times(1)).getPublicHomeProducts(12);
    }

    @Test
    @DisplayName("Should use default limit of 12 products")
    void testDefaultLimit() throws Exception {
        when(getPublicProductsUseCase.getPublicHomeProducts(12)).thenReturn(Collections.emptyList());
        when(publicProductDtoMapper.toPublicResponseList(Collections.emptyList())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/public/products/home"))
                .andExpect(status().isOk());

        // Verify that the default limit of 12 is used
        verify(getPublicProductsUseCase, times(1)).getPublicHomeProducts(12);
    }

    @Test
    @DisplayName("Should handle all ProductType enums")
    void testAllProductTypes() throws Exception {
        Product digitalProduct = Product.builder()
                .id("3")
                .name("E-Book")
                .description("Digital book")
                .price(new BigDecimal("9.99"))
                .productType(ProductType.DIGITAL)
                .stockQuantity(100)
                .status("ACTIVE")
                .build();

        PublicProductResponse digitalResponse = PublicProductResponse.builder()
                .id("3")
                .name("E-Book")
                .description("Digital book")
                .price(new BigDecimal("9.99"))
                .productType(ProductType.DIGITAL)
                .build();

        when(getPublicProductsUseCase.getPublicHomeProducts(12)).thenReturn(Arrays.asList(digitalProduct));
        when(publicProductDtoMapper.toPublicResponseList(Arrays.asList(digitalProduct)))
                .thenReturn(Arrays.asList(digitalResponse));

        mockMvc.perform(get("/api/public/products/home"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productType", is("DIGITAL")));

        verify(getPublicProductsUseCase, times(1)).getPublicHomeProducts(12);
    }
}
