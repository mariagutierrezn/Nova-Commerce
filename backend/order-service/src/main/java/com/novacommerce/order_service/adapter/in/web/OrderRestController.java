package com.novacommerce.order_service.adapter.in.web;

import com.novacommerce.order_service.adapter.in.web.dto.CreateOrderRequest;
import com.novacommerce.order_service.adapter.in.web.dto.OrderResponse;
import com.novacommerce.order_service.adapter.in.web.dto.UpdateOrderStatusRequest;
import com.novacommerce.order_service.adapter.in.web.mapper.OrderDtoMapper;
import com.novacommerce.order_service.application.port.in.CreateOrderUseCase;
import com.novacommerce.order_service.application.port.in.GetOrderUseCase;
import com.novacommerce.order_service.application.port.in.UpdateOrderStatusUseCase;
import com.novacommerce.order_service.domain.model.Order;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para órdenes.
 * SIN lógica de negocio - solo delega a use cases.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Orders", description = "Endpoints de gestión de órdenes del sistema")
public class OrderRestController {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;
    private final OrderDtoMapper orderDtoMapper;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @Operation(summary = "Crear orden", description = "Crea una nueva orden validando cliente, productos y aplicando descuentos")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        log.info("Creating order for customer: {}", request.getCustomerId());
        
        Order order = orderDtoMapper.toDomain(request);
        Order created = createOrderUseCase.createOrder(order);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderDtoMapper.toResponse(created));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @Operation(summary = "Listar órdenes", description = "Obtiene todas las órdenes del sistema")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> orders = getOrderUseCase.getAllOrders().stream()
                .map(orderDtoMapper::toResponse)
                .toList();
        
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @Operation(summary = "Obtener orden por ID", description = "Retorna los detalles de una orden específica")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable String id) {
        return getOrderUseCase.getOrderById(id)
                .map(orderDtoMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @Operation(summary = "Obtener órdenes por cliente", description = "Retorna todas las órdenes de un cliente")
    public ResponseEntity<List<OrderResponse>> getOrdersByCustomerId(@PathVariable String customerId) {
        List<OrderResponse> orders = getOrderUseCase.getOrdersByCustomerId(customerId).stream()
                .map(orderDtoMapper::toResponse)
                .toList();
        
        return ResponseEntity.ok(orders);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @Operation(summary = "Actualizar estado de orden", description = "Cambia el estado de una orden validando transiciones permitidas")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        
        log.info("Updating order {} to status {}", id, request.getStatus());
        
        Order updated = updateOrderStatusUseCase.updateOrderStatus(id, request.getStatus());
        
        return ResponseEntity.ok(orderDtoMapper.toResponse(updated));
    }
}
