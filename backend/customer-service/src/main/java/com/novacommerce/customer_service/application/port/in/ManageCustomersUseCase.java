package com.novacommerce.customer_service.application.port.in;

import com.novacommerce.customer_service.domain.model.Customer;

import java.util.List;
import java.util.Optional;

public interface ManageCustomersUseCase {
    List<Customer> findAll();
    Optional<Customer> findById(String id);
    Customer create(Customer customer);
    Customer update(String id, Customer customer);
    void delete(String id);
}
