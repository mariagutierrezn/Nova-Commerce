package com.novacommerce.customer_service.adapter.out.persistence.repository;

import com.novacommerce.customer_service.adapter.out.persistence.entity.CustomerEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CustomerRepository extends MongoRepository<CustomerEntity, String> {
    Optional<CustomerEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}
