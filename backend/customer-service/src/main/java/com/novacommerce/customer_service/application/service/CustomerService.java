package com.novacommerce.customer_service.application.service;

import com.novacommerce.customer_service.application.port.in.ManageCustomersUseCase;
import com.novacommerce.customer_service.application.port.out.CustomerPersistencePort;
import com.novacommerce.customer_service.domain.model.Customer;
import com.novacommerce.customer_service.domain.model.enums.CustomerStatus;

import java.util.List;
import java.util.Optional;

public class CustomerService implements ManageCustomersUseCase {

    private final CustomerPersistencePort persistencePort;

    public CustomerService(CustomerPersistencePort persistencePort) {
        this.persistencePort = persistencePort;
    }

    @Override
    public List<Customer> findAll() {
        return persistencePort.findAll();
    }

    @Override
    public Optional<Customer> findById(String id) {
        return persistencePort.findById(id);
    }

    @Override
    public Customer create(Customer customer) {
        if (persistencePort.existsByEmail(customer.getEmail())) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }
        if (customer.getStatus() == null) {
            customer.setStatus(CustomerStatus.ACTIVE);
        }
        return persistencePort.save(customer);
    }

    @Override
    public Customer update(String id, Customer customer) {
        Optional<Customer> existingOpt = persistencePort.findById(id);
        if (existingOpt.isEmpty()) {
            throw new IllegalArgumentException("Cliente no encontrado");
        }
        Customer existing = existingOpt.get();
        if (existing.getStatus() == CustomerStatus.BLOCKED) {
            throw new IllegalStateException("Cliente bloqueado, no puede actualizarse");
        }
        customer.setId(id);
        return persistencePort.save(customer);
    }

    @Override
    public void delete(String id) {
        Optional<Customer> existingOpt = persistencePort.findById(id);
        if (existingOpt.isEmpty()) {
            return;
        }
        Customer existing = existingOpt.get();
        if (existing.getStatus() == CustomerStatus.BLOCKED) {
            throw new IllegalStateException("Cliente bloqueado, no puede eliminarse");
        }
        persistencePort.deleteById(id);
    }
}
