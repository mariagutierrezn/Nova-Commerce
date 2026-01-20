package com.novacommerce.order_service.adapter.out.customer;

import com.novacommerce.order_service.adapter.out.customer.dto.CustomerResponse;
import com.novacommerce.order_service.application.port.out.CustomerValidationPort;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Adaptador para validación de clientes vía Feign.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerClientAdapter implements CustomerValidationPort {

    private final CustomerServiceClient customerServiceClient;

    @Value("${app.jwt.internal-api-key}")
    private String internalApiKey;

    @Override
    public boolean isCustomerValid(String customerId) {
        try {
            CustomerResponse customer = customerServiceClient.getCustomerById(customerId, internalApiKey);
            return customer != null && customer.getId() != null;
        } catch (FeignException.NotFound e) {
            log.warn("Customer not found: {}", customerId);
            return false;
        } catch (Exception e) {
            log.error("Error validating customer: {}", customerId, e);
            return false;
        }
    }

    @Override
    public String getCustomerStatus(String customerId) {
        try {
            CustomerResponse customer = customerServiceClient.getCustomerById(customerId, internalApiKey);
            return customer != null ? customer.getStatus() : "UNKNOWN";
        } catch (Exception e) {
            log.error("Error getting customer status: {}", customerId, e);
            return "UNKNOWN";
        }
    }

    @Override
    public String getCustomerLoyaltyLevel(String customerId) {
        try {
            CustomerResponse customer = customerServiceClient.getCustomerById(customerId, internalApiKey);
            return customer != null ? customer.getLoyaltyLevel() : null;
        } catch (Exception e) {
            log.error("Error getting customer loyalty level: {}", customerId, e);
            return null;
        }
    }
}
