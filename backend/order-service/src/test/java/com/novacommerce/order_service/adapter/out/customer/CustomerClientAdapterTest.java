package com.novacommerce.order_service.adapter.out.customer;

import com.novacommerce.order_service.adapter.out.customer.dto.CustomerResponse;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerClientAdapterTest {

    @Test
    @DisplayName("GIVEN 404 from Feign WHEN isCustomerValid THEN returns false")
    void notFoundReturnsFalse() {
        CustomerServiceClient client = mock(CustomerServiceClient.class);
        CustomerClientAdapter adapter = new CustomerClientAdapter(client);
        adapter.getClass(); // ensure no NPE
        // inject internalApiKey via reflection (value not used in this test path)
        try { 
            var f = CustomerClientAdapter.class.getDeclaredField("internalApiKey"); 
            f.setAccessible(true); 
            f.set(adapter, "k"); 
        } catch (Exception ignored) {
            // Ignore reflection exceptions in tests
        }

        Request req = Request.create(Request.HttpMethod.GET, "/internal/customers/99", Collections.emptyMap(), null, StandardCharsets.UTF_8, null);
        when(client.getCustomerById(eq("99"), anyString())).thenThrow(new FeignException.NotFound("not found", req, null, Collections.emptyMap()));

        boolean valid = adapter.isCustomerValid("99");
        assertFalse(valid);
    }

    @Test
    @DisplayName("GIVEN OK from Feign WHEN getCustomerStatus THEN returns status")
    void getStatus() {
        CustomerServiceClient client = mock(CustomerServiceClient.class);
        CustomerClientAdapter adapter = new CustomerClientAdapter(client);
        try { var f = CustomerClientAdapter.class.getDeclaredField("internalApiKey"); f.setAccessible(true); f.set(adapter, "k"); } catch (Exception ignored) {}

        CustomerResponse resp = new CustomerResponse();
        resp.setId("1"); resp.setStatus("ACTIVE"); resp.setLoyaltyLevel("GOLD");
        when(client.getCustomerById(eq("1"), anyString())).thenReturn(resp);

        assertEquals("ACTIVE", adapter.getCustomerStatus("1"));
        assertEquals("GOLD", adapter.getCustomerLoyaltyLevel("1"));
    }
}
