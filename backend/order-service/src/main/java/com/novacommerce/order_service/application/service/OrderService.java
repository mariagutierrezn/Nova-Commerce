package com.novacommerce.order_service.application.service;

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

    @Override
    public Order createOrder(Order order) {
        log.info("Creating order for customer: {}", order.getCustomerId());
        
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
        
        // Aplicar cada estrategia
        for (DiscountStrategy strategy : discountStrategies) {
            if (strategy.isApplicable(context)) {
                DiscountResult result = strategy.apply(context);
                if (result.hasDiscount()) {
                    log.info("Applied {}: {}", strategy.getName(), result.getDescription());
                    totalDiscount = totalDiscount.add(result.getDiscountAmount());
                }
            }
        }
        
        // Aplicar descuento total a la orden
        order.applyDiscount(totalDiscount);
        log.info("Total discount applied: {}", totalDiscount);
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
}
