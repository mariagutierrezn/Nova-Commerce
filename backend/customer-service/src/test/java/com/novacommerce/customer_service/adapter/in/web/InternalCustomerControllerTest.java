package com.novacommerce.customer_service.adapter.in.web;

import com.novacommerce.customer_service.application.port.in.ManageCustomersUseCase;
import com.novacommerce.customer_service.domain.model.Customer;
import com.novacommerce.customer_service.domain.model.enums.CustomerStatus;
import com.novacommerce.customer_service.domain.model.enums.LoyaltyLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("InternalCustomerControllerTest")
class InternalCustomerControllerTest {

    @Mock
    private ManageCustomersUseCase manageCustomersUseCase;

    @InjectMocks
    private InternalCustomerController controller;

    @Test
    @DisplayName("givenExistingCustomerId_whenGetByIdInternal_thenReturnCustomer")
    void givenExistingCustomerId_whenGetByIdInternal_thenReturnCustomer() {
        // GIVEN
        String customerId = "1";
        Customer customer = new Customer("1", "Juan", "Pérez", "juan@example.com", "123456789",
            CustomerStatus.ACTIVE, LoyaltyLevel.GOLD);

        when(manageCustomersUseCase.findById(customerId)).thenReturn(Optional.of(customer));

        // WHEN
        ResponseEntity<InternalCustomerResponse> response = controller.getByIdInternal(customerId);

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("1", response.getBody().id);
        assertEquals("Juan", response.getBody().firstName);
        assertEquals("Pérez", response.getBody().lastName);
        assertEquals("juan@example.com", response.getBody().email);
        assertEquals("123456789", response.getBody().phone);
        assertEquals("ACTIVE", response.getBody().status);
        assertEquals("GOLD", response.getBody().loyaltyLevel);
        verify(manageCustomersUseCase).findById(customerId);
    }

    @Test
    @DisplayName("givenNonExistentCustomerId_whenGetByIdInternal_thenReturnNotFound")
    void givenNonExistentCustomerId_whenGetByIdInternal_thenReturnNotFound() {
        // GIVEN
        String customerId = "999";
        when(manageCustomersUseCase.findById(customerId)).thenReturn(Optional.empty());

        // WHEN
        ResponseEntity<InternalCustomerResponse> response = controller.getByIdInternal(customerId);

        // THEN
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(manageCustomersUseCase).findById(customerId);
    }

    @Test
    @DisplayName("givenCustomerWithInactiveStatus_whenGetByIdInternal_thenReturnInactiveStatus")
    void givenCustomerWithInactiveStatus_whenGetByIdInternal_thenReturnInactiveStatus() {
        // GIVEN
        String customerId = "2";
        Customer customer = new Customer("2", "María", "García", "maria@example.com", "987654321",
            CustomerStatus.INACTIVE, LoyaltyLevel.SILVER);

        when(manageCustomersUseCase.findById(customerId)).thenReturn(Optional.of(customer));

        // WHEN
        ResponseEntity<InternalCustomerResponse> response = controller.getByIdInternal(customerId);

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INACTIVE", response.getBody().status);
        assertEquals("SILVER", response.getBody().loyaltyLevel);
    }

    @Test
    @DisplayName("givenCustomerWithBlockedStatus_whenGetByIdInternal_thenReturnBlockedStatus")
    void givenCustomerWithBlockedStatus_whenGetByIdInternal_thenReturnBlockedStatus() {
        // GIVEN
        String customerId = "3";
        Customer customer = new Customer("3", "Carlos", "López", "carlos@example.com", "555123456",
            CustomerStatus.BLOCKED, LoyaltyLevel.BRONZE);

        when(manageCustomersUseCase.findById(customerId)).thenReturn(Optional.of(customer));

        // WHEN
        ResponseEntity<InternalCustomerResponse> response = controller.getByIdInternal(customerId);

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("BLOCKED", response.getBody().status);
    }

    @Test
    @DisplayName("givenCustomerWithNullPhone_whenGetByIdInternal_thenReturnNullPhone")
    void givenCustomerWithNullPhone_whenGetByIdInternal_thenReturnNullPhone() {
        // GIVEN
        String customerId = "4";
        Customer customer = new Customer("4", "Ana", "Rodríguez", "ana@example.com", null,
            CustomerStatus.ACTIVE, LoyaltyLevel.PLATINUM);

        when(manageCustomersUseCase.findById(customerId)).thenReturn(Optional.of(customer));

        // WHEN
        ResponseEntity<InternalCustomerResponse> response = controller.getByIdInternal(customerId);

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().phone);
    }

    @Test
    @DisplayName("givenCustomerWithAllLoyaltyLevels_whenGetByIdInternal_thenReturnCorrectLevel")
    void givenCustomerWithAllLoyaltyLevels_whenGetByIdInternal_thenReturnCorrectLevel() {
        // Test all loyalty levels
        for (LoyaltyLevel level : LoyaltyLevel.values()) {
            // GIVEN
            String customerId = "5";
            Customer customer = new Customer("5", "Test", "User", "test@example.com", null,
                CustomerStatus.ACTIVE, level);

            when(manageCustomersUseCase.findById(customerId)).thenReturn(Optional.of(customer));

            // WHEN
            ResponseEntity<InternalCustomerResponse> response = controller.getByIdInternal(customerId);

            // THEN
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(level.name(), response.getBody().loyaltyLevel);
        }
    }
}
