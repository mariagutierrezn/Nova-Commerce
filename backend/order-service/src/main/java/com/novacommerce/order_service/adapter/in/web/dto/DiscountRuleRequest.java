package com.novacommerce.order_service.adapter.in.web.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para creación/actualización de reglas de descuento.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountRuleRequest {
    
    @NotBlank(message = "El nombre es requerido")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String name;
    
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String description;
    
    @NotBlank(message = "La estrategia es requerida")
    @Pattern(regexp = "LOYALTY|SEASON|PRODUCT_TYPE", message = "Estrategia inválida")
    private String strategy;
    
    @NotNull(message = "El valor es requerido")
    @DecimalMin(value = "0.0", message = "El valor debe ser mayor o igual a 0")
    @DecimalMax(value = "100.0", message = "El valor no puede exceder 100")
    private BigDecimal value;
    
    @DecimalMin(value = "0.0", message = "El monto mínimo debe ser mayor o igual a 0")
    private BigDecimal minPurchase;
    
    @DecimalMin(value = "0.0", message = "El descuento máximo debe ser mayor o igual a 0")
    private BigDecimal maxDiscount;
    
    @NotNull(message = "La fecha de inicio es requerida")
    private LocalDateTime startDate;
    
    private LocalDateTime endDate;
    
    private Boolean active;
    
    @Min(value = 0, message = "El uso máximo debe ser mayor o igual a 0")
    private Integer maxUsage;
}
