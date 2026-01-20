package com.novacommerce.order_service.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.novacommerce.order_service.adapter.in.web.dto.CreateOrderRequest;
import com.novacommerce.order_service.adapter.in.web.dto.OrderItemRequest;
import com.novacommerce.order_service.adapter.in.web.dto.UpdateOrderStatusRequest;
import com.novacommerce.order_service.adapter.in.web.mapper.OrderDtoMapper;
import com.novacommerce.order_service.application.port.in.CreateOrderUseCase;
import com.novacommerce.order_service.application.port.in.GetOrderUseCase;
import com.novacommerce.order_service.application.port.in.UpdateOrderStatusUseCase;
import com.novacommerce.order_service.domain.model.Money;
import com.novacommerce.order_service.domain.model.Order;
import com.novacommerce.order_service.domain.model.OrderItem;
import com.novacommerce.order_service.domain.model.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas @WebMvcTest para OrderRestController.
 * Solo testea la capa web (HTTP parsing, status codes, responses).
 * Todos los use cases y mappers son mocked/importados.
 * 
 * Se excluyen autoconfiguraciones de seguridad problematicas y se usa
 * TestSecurityConfiguration para proporcionar mocks de los beans requeridos.
 */
@WebMvcTest(
        controllers = OrderRestController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class,
                UserDetailsServiceAutoConfiguration.class,
                OAuth2ClientAutoConfiguration.class,
                OAuth2ResourceServerAutoConfiguration.class
        }
)
@Import({OrderDtoMapper.class, TestSecurityConfiguration.class})
class OrderRestControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CreateOrderUseCase createOrderUseCase;
    @MockBean
    GetOrderUseCase getOrderUseCase;
    @MockBean
    UpdateOrderStatusUseCase updateOrderStatusUseCase;

    private Order sampleOrder() {
        Order o = Order.builder().id("1").customerId("3").status(OrderStatus.CREATED).build();
        o.addItem(OrderItem.builder()
                .id("1")
                .productId("1")
                .productName("A")
                .quantity(2)
                .unitPrice(Money.of(10))
                .productType("ELECTRONICS")
                .build());
        o.addItem(OrderItem.builder()
                .id("2")
                .productId("2")
                .productName("B")
                .quantity(1)
                .unitPrice(Money.of(5.5))
                .productType("FOOD")
                .build());
        return o;
    }

    @Test
    @DisplayName("POST /api/orders — invokes createOrderUseCase")
    void createOrder() throws Exception {
        // GIVEN
        Order created = sampleOrder();
        when(createOrderUseCase.createOrder(any(Order.class))).thenReturn(created);

        CreateOrderRequest req = CreateOrderRequest.builder()
                .customerId("3")
                .items(List.of(
                        OrderItemRequest.builder()
                                .productId("1")
                                .quantity(2)
                                .unitPrice(new BigDecimal("10.00"))
                                .build()
                ))
                .build();

        // WHEN
        mvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                // THEN
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/orders/{id} — invokes getOrderUseCase")
    void getOrderById() throws Exception {
        // GIVEN
        when(getOrderUseCase.getOrderById("1")).thenReturn(Optional.of(sampleOrder()));
        when(getOrderUseCase.getOrderById("99")).thenReturn(Optional.empty());

        // WHEN & THEN - Order exists
        mvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk());

        // WHEN & THEN - Order not found
        mvc.perform(get("/api/orders/99"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /api/orders/{id}/status — invokes updateOrderStatusUseCase")
    void patchStatus() throws Exception {
        // GIVEN
        Order updated = sampleOrder();
        updated.setStatus(OrderStatus.PAID);
        when(updateOrderStatusUseCase.updateOrderStatus(eq("1"), eq(OrderStatus.PAID)))
                .thenReturn(updated);

        UpdateOrderStatusRequest req = new UpdateOrderStatusRequest();
        req.setStatus(OrderStatus.PAID);

        // WHEN
        mvc.perform(patch("/api/orders/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                // THEN
                .andExpect(status().isOk());
    }
}
