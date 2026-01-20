package com.novacommerce.order_service.adapter.out.persistence;

import com.novacommerce.order_service.domain.model.Money;
import com.novacommerce.order_service.domain.model.Order;
import com.novacommerce.order_service.domain.model.OrderStatus;
import com.novacommerce.order_service.repository.OrderRepository;
import com.novacommerce.order_service.repository.entity.OrderEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para OrderPersistenceAdapter.
 * Valida la persistencia de órdenes usando JPA repository.
 */
@ExtendWith(MockitoExtension.class)
class OrderPersistenceAdapterTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderPersistenceAdapter persistenceAdapter;

    private Order mockOrder;
    private OrderEntity mockEntity;

    @BeforeEach
    void setUp() {
        mockOrder = Order.builder()
                .id("1")
                .customerId("100")
                .status(OrderStatus.CREATED)
                .totalBeforeDiscount(Money.of(new BigDecimal("1000.00")))
                .discountTotal(Money.of(new BigDecimal("100.00")))
                .totalAfterDiscount(Money.of(new BigDecimal("900.00")))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        mockEntity = OrderEntity.builder()
                .id("1")
                .customerId("100")
                .status(OrderStatus.CREATED)
                .totalBeforeDiscount(new BigDecimal("1000.00"))
                .discountTotal(new BigDecimal("100.00"))
                .totalAfterDiscount(new BigDecimal("900.00"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void givenOrder_whenSave_thenPersistsAndReturnsDomain() {
        // GIVEN - Orden de dominio para guardar
        when(orderMapper.toEntity(mockOrder)).thenReturn(mockEntity);
        when(orderRepository.save(mockEntity)).thenReturn(mockEntity);
        when(orderMapper.toDomain(mockEntity)).thenReturn(mockOrder);

        // WHEN - Guardar orden
        Order savedOrder = persistenceAdapter.save(mockOrder);

        // THEN - Debe persistir y retornar dominio
        assertNotNull(savedOrder);
        assertEquals("1", savedOrder.getId());
        assertEquals("100", savedOrder.getCustomerId());
        assertEquals(OrderStatus.CREATED, savedOrder.getStatus());

        verify(orderMapper).toEntity(mockOrder);
        verify(orderRepository).save(mockEntity);
        verify(orderMapper).toDomain(mockEntity);
    }

    @Test
    void givenExistingOrderId_whenFindById_thenReturnsOrder() {
        // GIVEN - ID de orden existente
        String orderId = "1";
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockEntity));
        when(orderMapper.toDomain(mockEntity)).thenReturn(mockOrder);

        // WHEN - Buscar por ID
        Optional<Order> result = persistenceAdapter.findById(orderId);

        // THEN - Debe retornar la orden
        assertTrue(result.isPresent());
        assertEquals("1", result.get().getId());
        assertEquals("100", result.get().getCustomerId());

        verify(orderRepository).findById(orderId);
        verify(orderMapper).toDomain(mockEntity);
    }

    @Test
    void givenNonExistingOrderId_whenFindById_thenReturnsEmpty() {
        // GIVEN - ID de orden no existente
        String orderId = "999";
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // WHEN - Buscar por ID
        Optional<Order> result = persistenceAdapter.findById(orderId);

        // THEN - Debe retornar vacío
        assertFalse(result.isPresent());

        verify(orderRepository).findById(orderId);
        verify(orderMapper, never()).toDomain(any());
    }

    @Test
    void givenCustomerId_whenFindByCustomerId_thenReturnsOrders() {
        // GIVEN - Customer ID con órdenes
        String customerId = "100";
        OrderEntity entity1 = OrderEntity.builder()
                .id("1")
                .customerId(customerId)
                .status(OrderStatus.CREATED)
                .build();

        OrderEntity entity2 = OrderEntity.builder()
                .id("2")
                .customerId(customerId)
                .status(OrderStatus.PAID)
                .build();

        Order order1 = Order.builder().id("1").customerId(customerId).build();
        Order order2 = Order.builder().id("2").customerId(customerId).build();

        when(orderRepository.findByCustomerId(customerId))
                .thenReturn(Arrays.asList(entity1, entity2));
        when(orderMapper.toDomain(entity1)).thenReturn(order1);
        when(orderMapper.toDomain(entity2)).thenReturn(order2);

        // WHEN - Buscar por customer ID
        List<Order> orders = persistenceAdapter.findByCustomerId(customerId);

        // THEN - Debe retornar las órdenes del cliente
        assertNotNull(orders);
        assertEquals(2, orders.size());
        assertEquals("1", orders.get(0).getId());
        assertEquals("2", orders.get(1).getId());

        verify(orderRepository).findByCustomerId(customerId);
        verify(orderMapper, times(2)).toDomain(any(OrderEntity.class));
    }

    @Test
    void givenCustomerIdWithNoOrders_whenFindByCustomerId_thenReturnsEmptyList() {
        // GIVEN - Customer ID sin órdenes
        String customerId = "999";
        when(orderRepository.findByCustomerId(customerId)).thenReturn(List.of());

        // WHEN - Buscar por customer ID
        List<Order> orders = persistenceAdapter.findByCustomerId(customerId);

        // THEN - Debe retornar lista vacía
        assertNotNull(orders);
        assertTrue(orders.isEmpty());

        verify(orderRepository).findByCustomerId(customerId);
        verify(orderMapper, never()).toDomain(any());
    }

    @Test
    void whenFindAll_thenReturnsAllOrders() {
        // GIVEN - Múltiples órdenes en la base de datos
        OrderEntity entity1 = OrderEntity.builder().id("1").build();
        OrderEntity entity2 = OrderEntity.builder().id("2").build();
        OrderEntity entity3 = OrderEntity.builder().id("3").build();

        Order order1 = Order.builder().id("1").build();
        Order order2 = Order.builder().id("2").build();
        Order order3 = Order.builder().id("3").build();

        when(orderRepository.findAll()).thenReturn(Arrays.asList(entity1, entity2, entity3));
        when(orderMapper.toDomain(entity1)).thenReturn(order1);
        when(orderMapper.toDomain(entity2)).thenReturn(order2);
        when(orderMapper.toDomain(entity3)).thenReturn(order3);

        // WHEN - Buscar todas las órdenes
        List<Order> orders = persistenceAdapter.findAll();

        // THEN - Debe retornar todas las órdenes
        assertNotNull(orders);
        assertEquals(3, orders.size());

        verify(orderRepository).findAll();
        verify(orderMapper, times(3)).toDomain(any(OrderEntity.class));
    }

    @Test
    void givenOrderId_whenDeleteById_thenDelegatesToRepository() {
        // GIVEN - ID de orden a eliminar
        String orderId = "1";

        // WHEN - Eliminar orden
        persistenceAdapter.deleteById(orderId);

        // THEN - Debe delegar al repository
        verify(orderRepository).deleteById(orderId);
    }
}
