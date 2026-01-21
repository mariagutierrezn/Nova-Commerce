package com.novacommerce.order_service.application.port.in;

import com.novacommerce.order_service.domain.model.DiscountRule;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de entrada para gestión de reglas de descuento.
 */
public interface ManageDiscountRulesUseCase {
    
    List<DiscountRule> getAllRules();
    
    Optional<DiscountRule> getRuleById(String id);
    
    List<DiscountRule> getActiveRules();
    
    DiscountRule createRule(DiscountRule rule);
    
    DiscountRule updateRule(String id, DiscountRule rule);
    
    void deleteRule(String id);
    
    void toggleRuleStatus(String id);
}
