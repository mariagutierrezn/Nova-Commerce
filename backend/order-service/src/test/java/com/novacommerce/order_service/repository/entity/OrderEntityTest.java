package com.novacommerce.order_service.repository.entity;

import com.novacommerce.order_service.domain.model.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para OrderEntity.
 * Valida el comportamiento de la entidad JPA de órdenes.
 */
class OrderEntityTest {

    @Test
    void givenNewOrderEntity_whenBuild_thenCreatesCorrectly() {
        // GIVEN/WHEN - Construir OrderEntity
        OrderEntity entity = OrderEntity.builder()
                .id("1")
                .customerId("100")
                .status(OrderStatus.CREATED)
                .totalBeforeDiscount(new BigDecimal("1000.00"))
                .discountTotal(new BigDecimal("100.00"))
                .totalAfterDiscount(new BigDecimal("900.00"))
                .createdAt(LocalDateTime.of(2024, 1, 15, 10, 0))
                .updatedAt(LocalDateTime.of(2024, 1, 15, 10, 0))
                .build();

        // THEN - Debe tener los valores correctos
        assertEquals("1", entity.getId());
        assertEquals("100", entity.getCustomerId());
        assertEquals(OrderStatus.CREATED, entity.getStatus());
        assertEquals(0, new BigDecimal("1000.00").compareTo(entity.getTotalBeforeDiscount()));
        assertEquals(0, new BigDecimal("100.00").compareTo(entity.getDiscountTotal()));
        assertEquals(0, new BigDecimal("900.00").compareTo(entity.getTotalAfterDiscount()));
        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
    }

    @Test
    void givenOrderEntity_whenAddItem_thenItemIsAdded() {
        // GIVEN - OrderEntity vacía
        OrderEntity order = OrderEntity.builder()
                .id("1")
                .customerId("50")
                .status(OrderStatus.CREATED)
                .totalBeforeDiscount(new BigDecimal("200.00"))
                .discountTotal(BigDecimal.ZERO)
                .totalAfterDiscount(new BigDecimal("200.00"))
                .build();

        OrderItemEntity item = OrderItemEntity.builder()
                .id("10")
                .productId("500")
                .productName("Laptop")
                .quantity(1)
                .unitPrice(new BigDecimal("200.00"))
                .productType("ELECTRONICS")
                .build();

        // WHEN - Agregar item
        order.addItem(item);

        // THEN - Item debe estar en la lista
        assertEquals(1, order.getItems().size());
        assertEquals(item, order.getItems().get(0));
    }

    @Test
    void givenOrderEntity_whenAddMultipleItems_thenAllItemsAreAdded() {
        // GIVEN - OrderEntity vacía
        OrderEntity order = OrderEntity.builder()
                .id("2")
                .customerId("75")
                .status(OrderStatus.CREATED)
                .totalBeforeDiscount(new BigDecimal("500.00"))
                .discountTotal(BigDecimal.ZERO)
                .totalAfterDiscount(new BigDecimal("500.00"))
                .build();

        OrderItemEntity item1 = OrderItemEntity.builder()
                .id("11")
                .productId("100")
                .productName("Mouse")
                .quantity(2)
                .unitPrice(new BigDecimal("25.00"))
                .build();

        OrderItemEntity item2 = OrderItemEntity.builder()
                .id("12")
                .productId("200")
                .productName("Keyboard")
                .quantity(1)
                .unitPrice(new BigDecimal("75.00"))
                .build();

        // WHEN - Agregar múltiples items
        order.addItem(item1);
        order.addItem(item2);

        // THEN - Ambos items deben estar en la lista
        assertEquals(2, order.getItems().size());
        assertTrue(order.getItems().contains(item1));
        assertTrue(order.getItems().contains(item2));
    }

    @Test
    void givenOrderEntityWithBuilder_whenItemsNotProvided_thenItemsListIsInitialized() {
        // GIVEN/WHEN - Construir OrderEntity sin especificar items
        OrderEntity order = OrderEntity.builder()
                .id("3")
                .customerId("80")
                .status(OrderStatus.PAID)
                .totalBeforeDiscount(new BigDecimal("300.00"))
                .discountTotal(BigDecimal.ZERO)
                .totalAfterDiscount(new BigDecimal("300.00"))
                .build();

        // THEN - Items debe estar inicializada (por @Builder.Default)
        assertNotNull(order.getItems());
        assertTrue(order.getItems().isEmpty());
    }

    @Test
    void givenOrderEntity_whenSetTimestamps_thenTimestampsAreSet() {
        // GIVEN - OrderEntity nueva
        OrderEntity order = OrderEntity.builder()
                .customerId("90")
                .status(OrderStatus.CREATED)
                .totalBeforeDiscount(new BigDecimal("150.00"))
                .discountTotal(BigDecimal.ZERO)
                .totalAfterDiscount(new BigDecimal("150.00"))
                .build();

        // WHEN - Establecer timestamps
        order.setTimestamps();

        // THEN - Timestamps deben estar establecidos y ser muy cercanos
        assertNotNull(order.getCreatedAt());
        assertNotNull(order.getUpdatedAt());
        // Verificar que están en el mismo segundo (pueden diferir por milisegundos)
        assertEquals(order.getCreatedAt().withNano(0), order.getUpdatedAt().withNano(0));
    }

    @Test
    void givenExistingOrderEntity_whenSetTimestamps_thenUpdatedAtChanges() throws InterruptedException {
        // GIVEN - OrderEntity existente
        OrderEntity order = OrderEntity.builder()
                .id("4")
                .customerId("95")
                .status(OrderStatus.CREATED)
                .totalBeforeDiscount(new BigDecimal("200.00"))
                .discountTotal(BigDecimal.ZERO)
                .totalAfterDiscount(new BigDecimal("200.00"))
                .build();

        order.setTimestamps();
        LocalDateTime originalUpdatedAt = order.getUpdatedAt();
        LocalDateTime createdAt = order.getCreatedAt();

        // Pequeña pausa para asegurar diferencia en timestamp
        Thread.sleep(10);

        // WHEN - Establecer timestamps nuevamente
        order.setTimestamps();

        // THEN - UpdatedAt debe cambiar, createdAt debe permanecer igual
        assertNotEquals(originalUpdatedAt, order.getUpdatedAt());
        assertEquals(createdAt, order.getCreatedAt());
        assertTrue(order.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    void givenOrderEntity_whenChangeStatus_thenStatusIsUpdated() {
        // GIVEN - OrderEntity con status CREATED
        OrderEntity order = OrderEntity.builder()
                .id("5")
                .customerId("100")
                .status(OrderStatus.CREATED)
                .totalBeforeDiscount(new BigDecimal("400.00"))
                .discountTotal(BigDecimal.ZERO)
                .totalAfterDiscount(new BigDecimal("400.00"))
                .build();

        // WHEN - Cambiar status
        order.setStatus(OrderStatus.PAID);

        // THEN - Status debe cambiar
        assertEquals(OrderStatus.PAID, order.getStatus());
    }

    @Test
    void givenTwoOrderEntities_whenCompare_thenEqualsAndHashCodeWork() {
        // GIVEN - Dos OrderEntity con mismo ID
        OrderEntity order1 = OrderEntity.builder()
                .id("10")
                .customerId("100")
                .status(OrderStatus.CREATED)
                .totalBeforeDiscount(new BigDecimal("500.00"))
                .discountTotal(BigDecimal.ZERO)
                .totalAfterDiscount(new BigDecimal("500.00"))
                .build();

        OrderEntity order2 = OrderEntity.builder()
                .id("10")
                .customerId("100")
                .status(OrderStatus.CREATED)
                .totalBeforeDiscount(new BigDecimal("500.00"))
                .discountTotal(BigDecimal.ZERO)
                .totalAfterDiscount(new BigDecimal("500.00"))
                .build();

        // THEN - Deben ser iguales (Lombok @Data genera equals/hashCode)
        assertEquals(order1, order2);
        assertEquals(order1.hashCode(), order2.hashCode());
    }
}
