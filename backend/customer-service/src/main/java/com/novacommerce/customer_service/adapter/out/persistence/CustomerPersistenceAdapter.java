package com.novacommerce.customer_service.adapter.out.persistence;

import com.novacommerce.customer_service.adapter.out.persistence.entity.CustomerEntity;
import com.novacommerce.customer_service.adapter.out.persistence.mapper.CustomerEntityMapper;
import com.novacommerce.customer_service.adapter.out.persistence.repository.CustomerRepository;
import com.novacommerce.customer_service.application.port.out.CustomerPersistencePort;
import com.novacommerce.customer_service.domain.model.Customer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CustomerPersistenceAdapter implements CustomerPersistencePort {

    private final CustomerRepository repository;
    private final CustomerEntityMapper mapper;

    public CustomerPersistenceAdapter(CustomerRepository repository, CustomerEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Customer save(Customer customer) {
        CustomerEntity entity = mapper.toEntity(customer);
        CustomerEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Customer> findById(String id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Customer> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }
}
