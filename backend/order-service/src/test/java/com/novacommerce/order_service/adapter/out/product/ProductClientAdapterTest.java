package com.novacommerce.order_service.adapter.out.product;

import com.novacommerce.order_service.adapter.out.product.dto.ProductResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ProductClientAdapterTest {

    @Test
    @DisplayName("GIVEN ACTIVE product WHEN isProductValid THEN returns true and stock check works")
    void isProductValidAndStock() {
        ProductServiceClient client = mock(ProductServiceClient.class);
        ProductClientAdapter adapter = new ProductClientAdapter(client);
        try { var f = ProductClientAdapter.class.getDeclaredField("internalApiKey"); f.setAccessible(true); f.set(adapter, "k"); } catch (Exception ignored) {}

        ProductResponse resp = new ProductResponse();
        resp.setId("1"); resp.setStatus("ACTIVE"); resp.setName("P"); resp.setCategoryName("ELECTRONICS"); resp.setStock(5);
        when(client.getProductById(anyString(), anyString())).thenReturn(resp);

        assertTrue(adapter.isProductValid("1"));
        assertEquals("P", adapter.getProductName("1"));
        assertEquals("ELECTRONICS", adapter.getProductType("1"));
        assertTrue(adapter.hasStock("1", 3));
        assertFalse(adapter.hasStock("1", 10));
    }
}
