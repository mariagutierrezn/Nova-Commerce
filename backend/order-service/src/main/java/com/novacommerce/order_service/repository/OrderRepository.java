package com.novacommerce.order_service.repository;

import com.novacommerce.order_service.repository.entity.OrderEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<OrderEntity, String> {
    List<OrderEntity> findByCustomerId(String customerId);
}
