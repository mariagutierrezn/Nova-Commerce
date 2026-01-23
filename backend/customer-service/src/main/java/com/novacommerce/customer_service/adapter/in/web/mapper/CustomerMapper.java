package com.novacommerce.customer_service.adapter.in.web.mapper;

import com.novacommerce.customer_service.adapter.in.web.dto.CustomerDto;
import com.novacommerce.customer_service.adapter.out.order.OrderDto;
import com.novacommerce.customer_service.adapter.out.order.OrderServiceClient;
import com.novacommerce.customer_service.domain.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class CustomerMapper {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerMapper.class);
    
    @Autowired
    protected OrderServiceClient orderServiceClient;
    
    @Mapping(target = "totalOrders", expression = "java(calculateTotalOrders(customer.getId()))")
    @Mapping(target = "totalSpent", expression = "java(calculateTotalSpent(customer.getId()))")
    public abstract CustomerDto toDto(Customer customer);
    
    public abstract Customer toDomain(CustomerDto dto);
    
    protected Integer calculateTotalOrders(String customerId) {
        try {
            if (customerId == null || orderServiceClient == null) return 0;
            List<OrderDto> orders = orderServiceClient.getOrdersByCustomerId(customerId);
            return orders != null ? orders.size() : 0;
        } catch (Exception e) {
            logger.warn("Error calculando total de órdenes para cliente {}: {}", customerId, e.getMessage());
            return 0;
        }
    }
    
    protected Double calculateTotalSpent(String customerId) {
        try {
            if (customerId == null || orderServiceClient == null) return 0.0;
            List<OrderDto> orders = orderServiceClient.getOrdersByCustomerId(customerId);
            if (orders == null) return 0.0;
            
            return orders.stream()
                .mapToDouble(order -> {
                    Double amount = order.getTotalAmount();
                    return amount != null ? amount : 0.0;
                })
                .sum();
        } catch (Exception e) {
            logger.warn("Error calculando total gastado para cliente {}: {}", customerId, e.getMessage());
            return 0.0;
        }
    }
}
