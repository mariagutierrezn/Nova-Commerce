package com.novacommerce.order_service.adapter.in.web;

import com.novacommerce.order_service.adapter.in.web.dto.DiscountRuleRequest;
import com.novacommerce.order_service.adapter.in.web.dto.DiscountRuleResponse;
import com.novacommerce.order_service.application.port.in.ManageDiscountRulesUseCase;
import com.novacommerce.order_service.domain.model.DiscountRule;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestión de reglas de descuento.
 * CORS manejado por el API Gateway - no usar @CrossOrigin aquí
 */
@RestController
@RequestMapping("/api/discounts")
@RequiredArgsConstructor
@Slf4j
public class DiscountRuleRestController {
    
    private final ManageDiscountRulesUseCase manageDiscountRulesUseCase;
    
    @GetMapping
    public ResponseEntity<List<DiscountRuleResponse>> getAllRules() {
        log.info("GET /api/discounts - Fetching all discount rules");
        
        List<DiscountRuleResponse> rules = manageDiscountRulesUseCase.getAllRules().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(rules);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DiscountRuleResponse> getRuleById(@PathVariable String id) {
        log.info("GET /api/discounts/{} - Fetching discount rule by id", id);
        
        return manageDiscountRulesUseCase.getRuleById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<DiscountRuleResponse>> getActiveRules() {
        log.info("GET /api/discounts/active - Fetching active discount rules");
        
        List<DiscountRuleResponse> rules = manageDiscountRulesUseCase.getActiveRules().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(rules);
    }
    
    @PostMapping
    public ResponseEntity<DiscountRuleResponse> createRule(@Valid @RequestBody DiscountRuleRequest request) {
        log.info("POST /api/discounts - Creating new discount rule: {}", request.getName());
        
        DiscountRule rule = toDomain(request);
        DiscountRule created = manageDiscountRulesUseCase.createRule(rule);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<DiscountRuleResponse> updateRule(
            @PathVariable String id,
            @Valid @RequestBody DiscountRuleRequest request) {
        log.info("PUT /api/discounts/{} - Updating discount rule", id);
        
        DiscountRule rule = toDomain(request);
        DiscountRule updated = manageDiscountRulesUseCase.updateRule(id, rule);
        
        return ResponseEntity.ok(toResponse(updated));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable String id) {
        log.info("DELETE /api/discounts/{} - Deleting discount rule", id);
        
        manageDiscountRulesUseCase.deleteRule(id);
        
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Void> toggleRuleStatus(@PathVariable String id) {
        log.info("PATCH /api/discounts/{}/toggle - Toggling discount rule status", id);
        
        manageDiscountRulesUseCase.toggleRuleStatus(id);
        
        return ResponseEntity.ok().build();
    }
    
    private DiscountRule toDomain(DiscountRuleRequest request) {
        return DiscountRule.builder()
                .name(request.getName())
                .description(request.getDescription())
                .strategy(request.getStrategy())
                .value(request.getValue())
                .minPurchase(request.getMinPurchase())
                .maxDiscount(request.getMaxDiscount())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .active(request.getActive() != null ? request.getActive() : true)
                .maxUsage(request.getMaxUsage())
                .build();
    }
    
    private DiscountRuleResponse toResponse(DiscountRule rule) {
        return DiscountRuleResponse.builder()
                .id(rule.getId())
                .name(rule.getName())
                .description(rule.getDescription())
                .strategy(rule.getStrategy())
                .value(rule.getValue())
                .minPurchase(rule.getMinPurchase())
                .maxDiscount(rule.getMaxDiscount())
                .startDate(rule.getStartDate())
                .endDate(rule.getEndDate())
                .active(rule.getActive())
                .usageCount(rule.getUsageCount())
                .maxUsage(rule.getMaxUsage())
                .createdAt(rule.getCreatedAt())
                .updatedAt(rule.getUpdatedAt())
                .isValid(rule.isValid())
                .build();
    }
}
