package com.novacommerce.order_service.application.service;

import com.novacommerce.order_service.adapter.out.persistence.DiscountRuleMapper;
import com.novacommerce.order_service.application.port.in.ManageDiscountRulesUseCase;
import com.novacommerce.order_service.domain.exception.OrderException;
import com.novacommerce.order_service.domain.model.DiscountRule;
import com.novacommerce.order_service.repository.DiscountRuleRepository;
import com.novacommerce.order_service.repository.entity.DiscountRuleEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio de aplicación para gestión de reglas de descuento.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DiscountRuleService implements ManageDiscountRulesUseCase {
    
    private final DiscountRuleRepository repository;
    private final DiscountRuleMapper mapper;
    
    @Override
    @Transactional(readOnly = true)
    public List<DiscountRule> getAllRules() {
        log.info("Fetching all discount rules");
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<DiscountRule> getRuleById(String id) {
        log.info("Fetching discount rule by id: {}", id);
        return repository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DiscountRule> getActiveRules() {
        log.info("Fetching active discount rules");
        return repository.findByActiveTrue().stream()
                .map(mapper::toDomain)
                .filter(DiscountRule::isValid)
                .collect(Collectors.toList());
    }
    
    @Override
    public DiscountRule createRule(DiscountRule rule) {
        log.info("Creating new discount rule: {}", rule.getName());
        
        LocalDateTime now = LocalDateTime.now();
        rule.setCreatedAt(now);
        rule.setUpdatedAt(now);
        
        if (rule.getUsageCount() == null) {
            rule.setUsageCount(0);
        }
        
        if (rule.getActive() == null) {
            rule.setActive(true);
        }
        
        DiscountRuleEntity entity = mapper.toEntity(rule);
        DiscountRuleEntity saved = repository.save(entity);
        
        log.info("Discount rule created with id: {}", saved.getId());
        return mapper.toDomain(saved);
    }
    
    @Override
    public DiscountRule updateRule(String id, DiscountRule rule) {
        log.info("Updating discount rule: {}", id);
        
        DiscountRuleEntity existing = repository.findById(id)
                .orElseThrow(() -> new OrderException("Discount rule not found: " + id));
        
        rule.setId(id);
        rule.setCreatedAt(existing.getCreatedAt());
        rule.setUpdatedAt(LocalDateTime.now());
        rule.setUsageCount(existing.getUsageCount()); // Preserve usage count
        
        DiscountRuleEntity entity = mapper.toEntity(rule);
        DiscountRuleEntity updated = repository.save(entity);
        
        log.info("Discount rule updated: {}", id);
        return mapper.toDomain(updated);
    }
    
    @Override
    public void deleteRule(String id) {
        log.info("Deleting discount rule: {}", id);
        
        if (!repository.existsById(id)) {
            throw new OrderException("Discount rule not found: " + id);
        }
        
        repository.deleteById(id);
        log.info("Discount rule deleted: {}", id);
    }
    
    @Override
    public void toggleRuleStatus(String id) {
        log.info("Toggling status for discount rule: {}", id);
        
        DiscountRuleEntity entity = repository.findById(id)
                .orElseThrow(() -> new OrderException("Discount rule not found: " + id));
        
        entity.setActive(!entity.getActive());
        entity.setUpdatedAt(LocalDateTime.now());
        repository.save(entity);
        
        log.info("Discount rule status toggled: {} - New status: {}", id, entity.getActive());
    }
}
