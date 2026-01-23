package com.novacommerce.order_service.repository;

import com.novacommerce.order_service.repository.entity.OrderEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<OrderEntity, String> {
    // Ordena por fecha de creación descendente (más reciente primero)
    List<OrderEntity> findByCustomerIdOrderByCreatedAtDesc(String customerId);
    
    // Ordena todas las órdenes por fecha de creación descendente
    List<OrderEntity> findAllByOrderByCreatedAtDesc();
}
