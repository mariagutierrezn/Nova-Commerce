package com.novacommerce.order_service.repository.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para OrderItemEntity.
 * Valida el comportamiento de la entidad JPA de items de orden.
 */
class OrderItemEntityTest {

    @Test
    void givenNewOrderItemEntity_whenBuild_thenCreatesCorrectly() {
        // GIVEN/WHEN - Construir OrderItemEntity
        OrderItemEntity item = OrderItemEntity.builder()
                .id("1")
                .productId("100")
                .productName("Laptop Dell XPS")
                .quantity(2)
                .unitPrice(new BigDecimal("1200.50"))
                .productType("ELECTRONICS")
                .build();

        // THEN - Debe tener los valores correctos
        assertEquals("1", item.getId());
        assertEquals("100", item.getProductId());
        assertEquals("Laptop Dell XPS", item.getProductName());
        assertEquals(2, item.getQuantity());
        assertEquals(0, new BigDecimal("1200.50").compareTo(item.getUnitPrice()));
        assertEquals("ELECTRONICS", item.getProductType());
    }

    @Test
    void givenOrderItemEntity_whenSetOrder_thenOrderIsSet() {
        // GIVEN - OrderItemEntity y OrderEntity
        OrderItemEntity item = OrderItemEntity.builder()
                .id("2")
                .productId("200")
                .productName("Mouse")
                .quantity(1)
                .unitPrice(new BigDecimal("25.00"))
                .build();

        OrderEntity order = OrderEntity.builder()
                .id("10")
                .customerId("50")
                .build();

        // WHEN - Agregar item a la orden
        order.addItem(item);

        // THEN - El item debe estar en la lista de items de la orden
        assertEquals(1, order.getItems().size());
        assertEquals(item, order.getItems().get(0));
        assertEquals("10", order.getId());
    }

    @Test
    void givenOrderItemEntity_whenGetOrder_thenReturnsOrder() {
        // GIVEN - OrderEntity con item
        OrderEntity order = OrderEntity.builder()
                .id("15")
                .customerId("75")
                .build();

        OrderItemEntity item = OrderItemEntity.builder()
                .id("3")
                .productId("300")
                .productName("Keyboard")
                .quantity(1)
                .unitPrice(new BigDecimal("50.00"))
                .build();

        // WHEN - Agregar item a la orden
        order.addItem(item);

        // THEN - El item debe estar en la lista de items de la orden
        assertEquals(1, order.getItems().size());
        assertEquals(item, order.getItems().get(0));
        assertEquals("15", order.getId());
        assertEquals("75", order.getCustomerId());
    }

    @Test
    void givenOrderItemEntity_whenChangeQuantity_thenQuantityIsUpdated() {
        // GIVEN - OrderItemEntity con cantidad inicial
        OrderItemEntity item = OrderItemEntity.builder()
                .id("4")
                .productId("400")
                .productName("Monitor")
                .quantity(1)
                .unitPrice(new BigDecimal("300.00"))
                .build();

        // WHEN - Cambiar cantidad
        item.setQuantity(3);

        // THEN - Cantidad debe actualizarse
        assertEquals(3, item.getQuantity());
    }

    @Test
    void givenOrderItemEntity_whenChangeUnitPrice_thenPriceIsUpdated() {
        // GIVEN - OrderItemEntity con precio inicial
        OrderItemEntity item = OrderItemEntity.builder()
                .id("5")
                .productId("500")
                .productName("Headphones")
                .quantity(2)
                .unitPrice(new BigDecimal("75.00"))
                .build();

        // WHEN - Cambiar precio unitario
        BigDecimal newPrice = new BigDecimal("85.00");
        item.setUnitPrice(newPrice);

        // THEN - Precio debe actualizarse
        assertEquals(0, newPrice.compareTo(item.getUnitPrice()));
    }

    @Test
    void givenOrderItemEntityWithoutProductType_whenBuild_thenProductTypeIsNull() {
        // GIVEN/WHEN - Construir OrderItemEntity sin productType
        OrderItemEntity item = OrderItemEntity.builder()
                .id("6")
                .productId("600")
                .productName("Generic Product")
                .quantity(1)
                .unitPrice(new BigDecimal("10.00"))
                .build();

        // THEN - ProductType debe ser null
        assertNull(item.getProductType());
    }

    @Test
    void givenOrderItemEntity_whenSetProductType_thenProductTypeIsSet() {
        // GIVEN - OrderItemEntity sin productType
        OrderItemEntity item = OrderItemEntity.builder()
                .id("7")
                .productId("700")
                .productName("T-Shirt")
                .quantity(3)
                .unitPrice(new BigDecimal("20.00"))
                .build();

        // WHEN - Establecer productType
        item.setProductType("CLOTHING");

        // THEN - ProductType debe estar establecido
        assertEquals("CLOTHING", item.getProductType());
    }

    @Test
    void givenTwoOrderItemEntities_whenCompare_thenEqualsAndHashCodeWork() {
        // GIVEN - Dos OrderItemEntity con mismo ID y valores
        OrderItemEntity item1 = OrderItemEntity.builder()
                .id("20")
                .productId("800")
                .productName("Cable USB")
                .quantity(5)
                .unitPrice(new BigDecimal("5.00"))
                .productType("ACCESSORIES")
                .build();

        OrderItemEntity item2 = OrderItemEntity.builder()
                .id("20")
                .productId("800")
                .productName("Cable USB")
                .quantity(5)
                .unitPrice(new BigDecimal("5.00"))
                .productType("ACCESSORIES")
                .build();

        // THEN - Deben ser iguales (Lombok @Data genera equals/hashCode)
        assertEquals(item1, item2);
        assertEquals(item1.hashCode(), item2.hashCode());
    }

    @Test
    void givenOrderItemEntity_whenUseNoArgsConstructor_thenCanSetFields() {
        // GIVEN/WHEN - Crear con constructor sin argumentos
        OrderItemEntity item = new OrderItemEntity();
        item.setId("8");
        item.setProductId("900");
        item.setProductName("Webcam");
        item.setQuantity(1);
        item.setUnitPrice(new BigDecimal("60.00"));
        item.setProductType("ELECTRONICS");

        // THEN - Todos los campos deben estar establecidos
        assertEquals("8", item.getId());
        assertEquals("900", item.getProductId());
        assertEquals("Webcam", item.getProductName());
        assertEquals(1, item.getQuantity());
        assertEquals(0, new BigDecimal("60.00").compareTo(item.getUnitPrice()));
        assertEquals("ELECTRONICS", item.getProductType());
    }

    @Test
    void givenOrderItemEntity_whenUseAllArgsConstructor_thenAllFieldsAreSet() {
        // GIVEN - OrderEntity para la relación
        // WHEN - Crear con constructor de todos los argumentos
        OrderItemEntity item = new OrderItemEntity(
                "9",
                "1000",
                "Smartphone",
                1,
                new BigDecimal("799.99"),
                "ELECTRONICS",
                "https://example.com/smartphone.jpg"
        );

        // THEN - Todos los campos deben estar establecidos
        assertEquals("9", item.getId());
        assertEquals("1000", item.getProductId());
        assertEquals("Smartphone", item.getProductName());
        assertEquals(1, item.getQuantity());
        assertEquals(0, new BigDecimal("799.99").compareTo(item.getUnitPrice()));
        assertEquals("ELECTRONICS", item.getProductType());
    }
}
