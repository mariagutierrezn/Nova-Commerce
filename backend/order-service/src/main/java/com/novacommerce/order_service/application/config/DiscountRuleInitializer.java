package com.novacommerce.order_service.application.config;

import com.novacommerce.order_service.repository.DiscountRuleRepository;
import com.novacommerce.order_service.repository.entity.DiscountRuleEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Inicializa reglas de descuento por defecto si la base de datos está vacía.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DiscountRuleInitializer implements CommandLineRunner {
    
    private final DiscountRuleRepository repository;
    
    @Override
    public void run(String... args) {
        if (repository.count() == 0) {
            log.info("Initializing default discount rules...");
            
            List<DiscountRuleEntity> defaultRules = Arrays.asList(
                    // Loyalty Rules
                    DiscountRuleEntity.builder()
                            .name("Descuento Bronce")
                            .description("5% de descuento para clientes nivel Bronce")
                            .strategy("LOYALTY")
                            .value(new BigDecimal("5.00"))
                            .minPurchase(new BigDecimal("0"))
                            .startDate(LocalDateTime.of(2026, 1, 1, 0, 0))
                            .endDate(LocalDateTime.of(2026, 12, 31, 23, 59))
                            .active(true)
                            .usageCount(0)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build(),
                    
                    DiscountRuleEntity.builder()
                            .name("Descuento Plata")
                            .description("10% de descuento para clientes nivel Plata")
                            .strategy("LOYALTY")
                            .value(new BigDecimal("10.00"))
                            .minPurchase(new BigDecimal("0"))
                            .startDate(LocalDateTime.of(2026, 1, 1, 0, 0))
                            .endDate(LocalDateTime.of(2026, 12, 31, 23, 59))
                            .active(true)
                            .usageCount(0)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build(),
                    
                    DiscountRuleEntity.builder()
                            .name("Descuento Oro")
                            .description("15% de descuento para clientes nivel Oro")
                            .strategy("LOYALTY")
                            .value(new BigDecimal("15.00"))
                            .minPurchase(new BigDecimal("0"))
                            .startDate(LocalDateTime.of(2026, 1, 1, 0, 0))
                            .endDate(LocalDateTime.of(2026, 12, 31, 23, 59))
                            .active(true)
                            .usageCount(0)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build(),
                    
                    DiscountRuleEntity.builder()
                            .name("Descuento VIP")
                            .description("20% de descuento para clientes nivel VIP")
                            .strategy("LOYALTY")
                            .value(new BigDecimal("20.00"))
                            .minPurchase(new BigDecimal("0"))
                            .startDate(LocalDateTime.of(2026, 1, 1, 0, 0))
                            .endDate(LocalDateTime.of(2026, 12, 31, 23, 59))
                            .active(true)
                            .usageCount(0)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build(),
                    
                    // Season Rules
                    DiscountRuleEntity.builder()
                            .name("Promoción de Invierno")
                            .description("15% de descuento en temporada de invierno")
                            .strategy("SEASON")
                            .value(new BigDecimal("15.00"))
                            .minPurchase(new BigDecimal("50.00"))
                            .startDate(LocalDateTime.of(2026, 1, 1, 0, 0))
                            .endDate(LocalDateTime.of(2026, 3, 20, 23, 59))
                            .active(true)
                            .usageCount(0)
                            .maxUsage(1000)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build(),
                    
                    DiscountRuleEntity.builder()
                            .name("Promoción de Verano")
                            .description("10% de descuento en temporada de verano")
                            .strategy("SEASON")
                            .value(new BigDecimal("10.00"))
                            .minPurchase(new BigDecimal("30.00"))
                            .startDate(LocalDateTime.of(2026, 6, 21, 0, 0))
                            .endDate(LocalDateTime.of(2026, 9, 22, 23, 59))
                            .active(true)
                            .usageCount(0)
                            .maxUsage(1500)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build(),
                    
                    // Product Type Rules
                    DiscountRuleEntity.builder()
                            .name("Descuento Electrónicos")
                            .description("8% de descuento en productos electrónicos")
                            .strategy("PRODUCT_TYPE")
                            .value(new BigDecimal("8.00"))
                            .minPurchase(new BigDecimal("100.00"))
                            .maxDiscount(new BigDecimal("50.00"))
                            .startDate(LocalDateTime.of(2026, 1, 1, 0, 0))
                            .endDate(LocalDateTime.of(2026, 12, 31, 23, 59))
                            .active(true)
                            .usageCount(0)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build(),
                    
                    DiscountRuleEntity.builder()
                            .name("Descuento Ropa")
                            .description("12% de descuento en ropa y accesorios")
                            .strategy("PRODUCT_TYPE")
                            .value(new BigDecimal("12.00"))
                            .minPurchase(new BigDecimal("50.00"))
                            .maxDiscount(new BigDecimal("30.00"))
                            .startDate(LocalDateTime.of(2026, 1, 1, 0, 0))
                            .endDate(LocalDateTime.of(2026, 12, 31, 23, 59))
                            .active(true)
                            .usageCount(0)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build(),
                    
                    DiscountRuleEntity.builder()
                            .name("Descuento Alimentos")
                            .description("3% de descuento en alimentos y bebidas")
                            .strategy("PRODUCT_TYPE")
                            .value(new BigDecimal("3.00"))
                            .minPurchase(new BigDecimal("20.00"))
                            .maxDiscount(new BigDecimal("10.00"))
                            .startDate(LocalDateTime.of(2026, 1, 1, 0, 0))
                            .endDate(LocalDateTime.of(2026, 12, 31, 23, 59))
                            .active(true)
                            .usageCount(0)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build()
            );
            
            repository.saveAll(defaultRules);
            log.info("Default discount rules initialized successfully: {} rules created", defaultRules.size());
        } else {
            log.info("Discount rules already exist in database, skipping initialization");
        }
    }
}
