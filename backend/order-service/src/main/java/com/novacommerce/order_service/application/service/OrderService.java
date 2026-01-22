package com.novacommerce.order_service.application.service;

import com.novacommerce.order_service.adapter.out.customer.CustomerServiceClient;
import com.novacommerce.order_service.adapter.out.customer.dto.CustomerResponse;
import com.novacommerce.order_service.application.port.in.CreateOrderUseCase;
import com.novacommerce.order_service.application.port.in.GetOrderUseCase;
import com.novacommerce.order_service.application.port.in.UpdateOrderStatusUseCase;
import com.novacommerce.order_service.application.port.out.CustomerValidationPort;
import com.novacommerce.order_service.application.port.out.OrderEventPublisherPort;
import com.novacommerce.order_service.application.port.out.OrderPersistencePort;
import com.novacommerce.order_service.application.port.out.ProductValidationPort;
import com.novacommerce.order_service.domain.event.OrderCreatedEvent;
import com.novacommerce.order_service.domain.event.OrderPaidEvent;
import com.novacommerce.order_service.domain.discount.DiscountStrategy;
import com.novacommerce.order_service.domain.exception.BusinessRuleException;
import com.novacommerce.order_service.domain.exception.OrderException;
import com.novacommerce.order_service.domain.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de aplicación que implementa todos los use cases de Order.
 * Orquesta la lógica de negocio usando los puertos de salida.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OrderService implements CreateOrderUseCase, GetOrderUseCase, UpdateOrderStatusUseCase {

    private final OrderPersistencePort orderPersistencePort;
    private final CustomerValidationPort customerValidationPort;
    private final ProductValidationPort productValidationPort;
    private final OrderEventPublisherPort orderEventPublisherPort;
    private final List<DiscountStrategy> discountStrategies;
    private final SequenceGeneratorService sequenceGeneratorService;
    private final CustomerServiceClient customerServiceClient;
    
    @Value("${app.jwt.internal-api-key}")
    private String internalApiKey;

    @Override
    public Order createOrder(Order order) {
        log.info("Creating order for customer: {}", order.getCustomerId());
        
        // Generar número de orden incremental
        Long orderNumber = sequenceGeneratorService.generateSequence("order_sequence");
        order.setOrderNumber(orderNumber);
        log.info("Generated order number: {}", orderNumber);
        
        // Enriquecer con información del cliente
        try {
            CustomerResponse customer = customerServiceClient.getCustomerById(order.getCustomerId(), internalApiKey);
            String fullName = (customer.getFirstName() != null ? customer.getFirstName() : "") + " " + 
                            (customer.getLastName() != null ? customer.getLastName() : "");
            order.setCustomerName(fullName.trim());
            order.setCustomerEmail(customer.getEmail());
            order.setCustomerPhone(customer.getPhone());
            log.info("Enriched order with customer info: {}", fullName);
        } catch (Exception e) {
            log.warn("Failed to enrich order with customer info: {}", e.getMessage());
            // Continuar sin enriquecer si falla
        }
        
        // Validar dominio
        order.validate();
        
        // Validar cliente
        validateCustomer(order.getCustomerId());
        
        // Validar y enriquecer items
        enrichOrderItems(order);
        
        // Calcular totales iniciales
        order.recalculateTotals();
        
        // Aplicar descuentos
        applyDiscounts(order);
        
        // Marcar como creada
        order.markAsCreated();
        
        // Persistir
        Order savedOrder = orderPersistencePort.save(order);
        log.info("Order created successfully with ID: {}", savedOrder.getId());
        
        // Decrementar stock de productos
        decrementProductStock(savedOrder);
        
        // Publicar evento asíncrono
        publishOrderCreatedEvent(savedOrder);
        
        return savedOrder;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> getOrderById(String id) {
        return orderPersistencePort.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersByCustomerId(String customerId) {
        return orderPersistencePort.findByCustomerId(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderPersistencePort.findAll();
    }

    @Override
    public Order updateOrderStatus(String orderId, OrderStatus newStatus) {
        log.info("Updating order {} to status {}", orderId, newStatus);
        
        Order order = orderPersistencePort.findById(orderId)
                .orElseThrow(() -> new OrderException("Order not found: " + orderId));
        
        // El dominio valida la transición
        order.changeStatus(newStatus);
        
        Order updated = orderPersistencePort.save(order);
        log.info("Order status updated successfully");
        
        // Publicar evento si la orden fue pagada
        if (newStatus == OrderStatus.PAID) {
            publishOrderPaidEvent(updated);
        }
        
        return updated;
    }

    /**
     * Valida que el cliente existe y cumple reglas de negocio.
     */
    private void validateCustomer(String customerId) {
        if (!customerValidationPort.isCustomerValid(customerId)) {
            throw new BusinessRuleException("Customer not found or invalid: " + customerId);
        }
        
        String customerStatus = customerValidationPort.getCustomerStatus(customerId);
        if ("INACTIVE".equalsIgnoreCase(customerStatus)) {
            throw new BusinessRuleException("Cannot create order for INACTIVE customer");
        }
        if ("BLOCKED".equalsIgnoreCase(customerStatus)) {
            throw new BusinessRuleException("Cannot create order for BLOCKED customer");
        }
    }

    /**
     * Valida productos y enriquece items con información faltante.
     */
    private void enrichOrderItems(Order order) {
        for (OrderItem item : order.getItems()) {
            // Validar producto
            if (!productValidationPort.isProductValid(item.getProductId())) {
                throw new BusinessRuleException("Product not found or inactive: " + item.getProductId());
            }
            
            // Validar stock
            if (!productValidationPort.hasStock(item.getProductId(), item.getQuantity())) {
                throw new BusinessRuleException("Insufficient stock for product: " + item.getProductId());
            }
            
            // Enriquecer con nombre y tipo si no vienen
            if (item.getProductName() == null) {
                item.setProductName(productValidationPort.getProductName(item.getProductId()));
            }
            if (item.getProductType() == null) {
                item.setProductType(productValidationPort.getProductType(item.getProductId()));
            }
            // Enriquecer con imagen
            if (item.getImageUrl() == null) {
                item.setImageUrl(productValidationPort.getProductImageUrl(item.getProductId()));
            }
        }
    }

    /**
     * Aplica todas las estrategias de descuento y calcula el descuento total.
     */
    private void applyDiscounts(Order order) {
        // Construir contexto
        DiscountContext context = DiscountContext.builder()
                .customerId(order.getCustomerId())
                .customerLoyaltyLevel(customerValidationPort.getCustomerLoyaltyLevel(order.getCustomerId()))
                .customerStatus(customerValidationPort.getCustomerStatus(order.getCustomerId()))
                .orderTotal(order.getTotalBeforeDiscount())
                .items(order.getItems())
                .currentSeason(getCurrentSeason())
                .build();
        
        Money totalDiscount = Money.zero();
        
        // Aplicar cada estrategia y registrar descuentos aplicados
        for (DiscountStrategy strategy : discountStrategies) {
            if (strategy.isApplicable(context)) {
                DiscountResult result = strategy.apply(context);
                if (result.hasDiscount()) {
                    log.info("Applied {}: {}", strategy.getName(), result.getDescription());
                    totalDiscount = totalDiscount.add(result.getDiscountAmount());
                    
                    // Registrar descuento aplicado
                    String discountType = determineDiscountType(strategy.getName());
                    java.math.BigDecimal percentage = calculateDiscountPercentage(
                        result.getDiscountAmount(), 
                        order.getTotalBeforeDiscount()
                    );
                    
                    order.getDiscounts().add(Order.AppliedDiscount.builder()
                            .type(discountType)
                            .percentage(percentage)
                            .amount(result.getDiscountAmount())
                            .build());
                }
            }
        }
        
        // Aplicar descuento total a la orden
        order.applyDiscount(totalDiscount);
        log.info("Total discount applied: {}", totalDiscount);
    }
    
    /**
     * Determina el tipo de descuento basándose en el nombre de la estrategia.
     */
    private String determineDiscountType(String strategyName) {
        if (strategyName.contains("Loyalty") || strategyName.contains("LOYALTY")) {
            return "LOYALTY";
        } else if (strategyName.contains("Product") || strategyName.contains("PRODUCT")) {
            return "PRODUCT";
        } else if (strategyName.contains("Season") || strategyName.contains("SEASON")) {
            return "SEASON";
        }
        return "OTHER";
    }
    
    /**
     * Calcula el porcentaje de descuento.
     */
    private java.math.BigDecimal calculateDiscountPercentage(Money discountAmount, Money total) {
        if (total.isZero()) {
            return java.math.BigDecimal.ZERO;
        }
        return discountAmount.getAmount()
                .divide(total.getAmount(), 2, java.math.RoundingMode.HALF_UP)
                .multiply(new java.math.BigDecimal("100"));
    }

    /**
     * Obtiene la temporada actual (simplificado - podría venir de configuración).
     * En producción vendría de configuración o regla de negocio.
     */
    private static final String CURRENT_SEASON = "WINTER";
    
    private String getCurrentSeason() {
        return CURRENT_SEASON;
    }

    /**
     * Decrementa el stock de todos los productos de la orden.
     */
    private void decrementProductStock(Order order) {
        log.info("Decrementing stock for order: {}", order.getId());
        for (OrderItem item : order.getItems()) {
            try {
                productValidationPort.decrementStock(item.getProductId(), item.getQuantity());
                log.info("Stock decremented for product {} by {}", item.getProductId(), item.getQuantity());
            } catch (Exception e) {
                log.error("Error decrementing stock for product {}: {}", item.getProductId(), e.getMessage());
                // En producción, aquí se podría implementar compensación o rollback
                throw new BusinessRuleException("Failed to decrement stock for product: " + item.getProductId());
            }
        }
    }

    /**
     * Publica el evento de orden creada de forma asíncrona.
     */
    private void publishOrderCreatedEvent(Order order) {
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(order.getId())
                .customerId(order.getCustomerId())
                .status(order.getStatus().name())
                .totalBeforeDiscount(order.getTotalBeforeDiscountValue())
                .discountTotal(order.getDiscountTotalValue())
                .totalAfterDiscount(order.getTotalAfterDiscountValue())
                .createdAt(order.getCreatedAt())
                .items(order.getItems().stream()
                        .map(item -> OrderCreatedEvent.OrderItemEvent.builder()
                                .productId(item.getProductId())
                                .productName(item.getProductName())
                                .quantity(item.getQuantity())
                                .unitPrice(item.getUnitPriceValue())
                                .subTotal(item.calculateSubTotal().getAmount())
                                .build())
                                .toList())
                .build();
        
        orderEventPublisherPort.publishOrderCreated(event);
    }

    /**
     * Publica el evento de orden pagada de forma asíncrona.
     */
    private void publishOrderPaidEvent(Order order) {
        OrderPaidEvent event = OrderPaidEvent.builder()
                .orderId(order.getId())
                .customerId(order.getCustomerId())
                .totalAmount(order.getTotalAfterDiscountValue())
                .paidAt(order.getUpdatedAt() != null ? order.getUpdatedAt() : java.time.LocalDateTime.now())
                .paymentMethod("CREDIT_CARD") // Por ahora fijo, luego se puede enriquecer
                .build();
        
        orderEventPublisherPort.publishOrderPaid(event);
    }
    
    /**
     * Calcula un preview de descuentos sin crear la orden.
     * Útil para mostrar al usuario los descuentos antes de confirmar.
     */
    public DiscountPreviewResult calculateDiscountPreview(String customerId, List<OrderItem> items) {
        log.info("Calculating discount preview for customer: {}", customerId);
        
        // Validar cliente
        validateCustomer(customerId);
        
        // Calcular subtotal
        Money subtotal = items.stream()
                .map(OrderItem::calculateSubTotal)
                .reduce(Money.zero(), Money::add);
        
        // Construir contexto
        DiscountContext context = DiscountContext.builder()
                .customerId(customerId)
                .customerLoyaltyLevel(customerValidationPort.getCustomerLoyaltyLevel(customerId))
                .customerStatus(customerValidationPort.getCustomerStatus(customerId))
                .orderTotal(subtotal)
                .items(items)
                .currentSeason(getCurrentSeason())
                .build();
        
        Money totalDiscount = Money.zero();
        java.util.List<DiscountPreviewResult.AppliedDiscount> appliedDiscounts = new java.util.ArrayList<>();
        
        // Aplicar cada estrategia
        for (DiscountStrategy strategy : discountStrategies) {
            if (strategy.isApplicable(context)) {
                DiscountResult result = strategy.apply(context);
                if (result.hasDiscount()) {
                    totalDiscount = totalDiscount.add(result.getDiscountAmount());
                    
                    String discountType = determineDiscountType(strategy.getName());
                    java.math.BigDecimal percentage = calculateDiscountPercentage(
                        result.getDiscountAmount(), 
                        subtotal
                    );
                    
                    appliedDiscounts.add(DiscountPreviewResult.AppliedDiscount.builder()
                            .type(discountType)
                            .label(getDiscountLabel(discountType))
                            .description(result.getDescription())
                            .percentage(percentage)
                            .amount(result.getDiscountAmount().getAmount())
                            .build());
                }
            }
        }
        
        Money finalTotal = subtotal.subtract(totalDiscount);
        
        return DiscountPreviewResult.builder()
                .subtotal(subtotal.getAmount())
                .totalDiscount(totalDiscount.getAmount())
                .total(finalTotal.getAmount())
                .discounts(appliedDiscounts)
                .build();
    }
    
    /**
     * Obtiene la etiqueta legible del tipo de descuento.
     */
    private String getDiscountLabel(String type) {
        return switch (type) {
            case "LOYALTY" -> "Descuento por Lealtad";
            case "PRODUCT" -> "Descuento del Producto";
            case "SEASON" -> "Descuento de Temporada";
            default -> "Descuento";
        };
    }
    
    /**
     * Clase interna para el resultado del preview
     */
    @lombok.Data
    @lombok.Builder
    public static class DiscountPreviewResult {
        private java.math.BigDecimal subtotal;
        private java.math.BigDecimal totalDiscount;
        private java.math.BigDecimal total;
        private List<AppliedDiscount> discounts;
        
        @lombok.Data
        @lombok.Builder
        public static class AppliedDiscount {
            private String type;
            private String label;
            private String description;
            private java.math.BigDecimal percentage;
            private java.math.BigDecimal amount;
        }
    }
}
