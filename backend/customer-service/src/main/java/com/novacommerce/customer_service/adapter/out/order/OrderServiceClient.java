package com.novacommerce.customer_service.adapter.out.order;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "order-service", url = "${order.service.url:http://localhost:8085}")
public interface OrderServiceClient {
    
    @GetMapping("/api/orders/customer/{customerId}")
    List<OrderDto> getOrdersByCustomerId(@PathVariable("customerId") String customerId);
}
