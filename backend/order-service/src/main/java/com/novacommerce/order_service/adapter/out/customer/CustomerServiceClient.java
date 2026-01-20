package com.novacommerce.order_service.adapter.out.customer;

import com.novacommerce.order_service.adapter.out.customer.dto.CustomerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Cliente Feign para comunicarse con customer-service.
 */
@FeignClient(
        name = "customer-service",
        url = "${app.services.customer-service.url}"
)
public interface CustomerServiceClient {

    @GetMapping("/internal/customers/{id}")
    CustomerResponse getCustomerById(
            @PathVariable("id") String id,
            @RequestHeader("X-Internal-API-Key") String apiKey
    );
}
