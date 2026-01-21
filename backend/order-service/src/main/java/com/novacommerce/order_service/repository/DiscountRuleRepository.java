package com.novacommerce.order_service.repository;

import com.novacommerce.order_service.repository.entity.DiscountRuleEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para reglas de descuento.
 */
@Repository
public interface DiscountRuleRepository extends MongoRepository<DiscountRuleEntity, String> {
    
    List<DiscountRuleEntity> findByActiveTrue();
    
    List<DiscountRuleEntity> findByStrategy(String strategy);
}
