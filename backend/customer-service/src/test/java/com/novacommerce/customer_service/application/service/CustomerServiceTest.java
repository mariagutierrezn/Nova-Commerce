package com.novacommerce.customer_service.application.service;

import com.novacommerce.customer_service.application.port.out.CustomerPersistencePort;
import com.novacommerce.customer_service.domain.model.Customer;
import com.novacommerce.customer_service.domain.model.enums.CustomerStatus;
import com.novacommerce.customer_service.domain.model.enums.LoyaltyLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerServiceTest")
class CustomerServiceTest {

    @Mock
    private CustomerPersistencePort persistencePort;

    @InjectMocks
    private CustomerService service;

    @Test
    @DisplayName("givenAllCustomers_whenFindAll_thenReturnList")
    void givenAllCustomers_whenFindAll_thenReturnList() {
        // GIVEN
        Customer customer1 = new Customer("1", "Juan", "Pérez", "juan@example.com", "123456789",
            CustomerStatus.ACTIVE, LoyaltyLevel.GOLD);
        Customer customer2 = new Customer("2", "María", "García", "maria@example.com", "987654321",
            CustomerStatus.ACTIVE, LoyaltyLevel.SILVER);

        when(persistencePort.findAll()).thenReturn(List.of(customer1, customer2));

        // WHEN
        List<Customer> result = service.findAll();

        // THEN
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(persistencePort).findAll();
    }

    @Test
    @DisplayName("givenEmptyRepository_whenFindAll_thenReturnEmptyList")
    void givenEmptyRepository_whenFindAll_thenReturnEmptyList() {
        // GIVEN
        when(persistencePort.findAll()).thenReturn(List.of());

        // WHEN
        List<Customer> result = service.findAll();

        // THEN
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(persistencePort).findAll();
    }

    @Test
    @DisplayName("givenValidId_whenFindById_thenReturnCustomer")
    void givenValidId_whenFindById_thenReturnCustomer() {
        // GIVEN
        String customerId = "1";
        Customer customer = new Customer("1", "Juan", "Pérez", "juan@example.com", "123456789",
            CustomerStatus.ACTIVE, LoyaltyLevel.GOLD);

        when(persistencePort.findById(customerId)).thenReturn(Optional.of(customer));

        // WHEN
        Optional<Customer> result = service.findById(customerId);

        // THEN
        assertTrue(result.isPresent());
        assertEquals("Juan", result.get().getFirstName());
        verify(persistencePort).findById(customerId);
    }

    @Test
    @DisplayName("givenInvalidId_whenFindById_thenReturnEmpty")
    void givenInvalidId_whenFindById_thenReturnEmpty() {
        // GIVEN
        String customerId = "999";
        when(persistencePort.findById(customerId)).thenReturn(Optional.empty());

        // WHEN
        Optional<Customer> result = service.findById(customerId);

        // THEN
        assertTrue(result.isEmpty());
        verify(persistencePort).findById(customerId);
    }

    @Test
    @DisplayName("givenNewCustomer_whenCreate_thenCustomerSavedWithActiveStatus")
    void givenNewCustomer_whenCreate_thenCustomerSavedWithActiveStatus() {
        // GIVEN
        Customer customer = new Customer(null, "Carlos", "López", "carlos@example.com", "555123456",
            null, null);
        Customer savedCustomer = new Customer("3", "Carlos", "López", "carlos@example.com", "555123456",
            CustomerStatus.ACTIVE, LoyaltyLevel.BRONZE);

        when(persistencePort.existsByEmail("carlos@example.com")).thenReturn(false);
        when(persistencePort.save(any(Customer.class))).thenReturn(savedCustomer);

        // WHEN
        Customer result = service.create(customer);

        // THEN
        assertNotNull(result);
        assertEquals("3", result.getId());
        assertEquals(CustomerStatus.ACTIVE, result.getStatus());
        verify(persistencePort).existsByEmail("carlos@example.com");
        verify(persistencePort).save(any(Customer.class));
    }

    @Test
    @DisplayName("givenDuplicateEmail_whenCreate_thenThrowException")
    void givenDuplicateEmail_whenCreate_thenThrowException() {
        // GIVEN
        Customer customer = new Customer(null, "Ana", "Rodríguez", "ana@example.com", "444777888",
            null, null);

        when(persistencePort.existsByEmail("ana@example.com")).thenReturn(true);

        // WHEN & THEN
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> service.create(customer)
        );
        assertEquals("El correo ya está registrado", exception.getMessage());
        verify(persistencePort).existsByEmail("ana@example.com");
        verify(persistencePort, never()).save(any());
    }

    @Test
    @DisplayName("givenCustomerWithStatus_whenCreate_thenKeepProvidedStatus")
    void givenCustomerWithStatus_whenCreate_thenKeepProvidedStatus() {
        // GIVEN
        Customer customer = new Customer(null, "Test", "User", "test@example.com", null,
            CustomerStatus.INACTIVE, LoyaltyLevel.GOLD);
        Customer savedCustomer = new Customer("4", "Test", "User", "test@example.com", null,
            CustomerStatus.INACTIVE, LoyaltyLevel.GOLD);

        when(persistencePort.existsByEmail("test@example.com")).thenReturn(false);
        when(persistencePort.save(any(Customer.class))).thenReturn(savedCustomer);

        // WHEN
        Customer result = service.create(customer);

        // THEN
        assertEquals(CustomerStatus.INACTIVE, result.getStatus());
        verify(persistencePort).save(any(Customer.class));
    }

    @Test
    @DisplayName("givenExistingCustomer_whenUpdate_thenUpdateSuccessful")
    void givenExistingCustomer_whenUpdate_thenUpdateSuccessful() {
        // GIVEN
        String customerId = "1";
        Customer existingCustomer = new Customer("1", "Juan", "Pérez", "juan@example.com", "123456789",
            CustomerStatus.ACTIVE, LoyaltyLevel.GOLD);
        Customer updateData = new Customer(null, "Juan", "Pérez Updated", "juan@example.com", "123456789",
            CustomerStatus.ACTIVE, LoyaltyLevel.PLATINUM);
        Customer updated = new Customer("1", "Juan", "Pérez Updated", "juan@example.com", "123456789",
            CustomerStatus.ACTIVE, LoyaltyLevel.PLATINUM);

        when(persistencePort.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(persistencePort.save(any(Customer.class))).thenReturn(updated);

        // WHEN
        Customer result = service.update(customerId, updateData);

        // THEN
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("Pérez Updated", result.getLastName());
        verify(persistencePort).findById(customerId);
        verify(persistencePort).save(any(Customer.class));
    }

    @Test
    @DisplayName("givenNonExistentCustomer_whenUpdate_thenThrowException")
    void givenNonExistentCustomer_whenUpdate_thenThrowException() {
        // GIVEN
        String customerId = "999";
        Customer updateData = new Customer(null, "Test", "User", "test@example.com", null,
            null, null);

        when(persistencePort.findById(customerId)).thenReturn(Optional.empty());

        // WHEN & THEN
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> service.update(customerId, updateData)
        );
        assertEquals("Cliente no encontrado", exception.getMessage());
        verify(persistencePort, never()).save(any());
    }

    @Test
    @DisplayName("givenBlockedCustomer_whenUpdate_thenThrowException")
    void givenBlockedCustomer_whenUpdate_thenThrowException() {
        // GIVEN
        String customerId = "1";
        Customer blockedCustomer = new Customer("1", "Juan", "Pérez", "juan@example.com", "123456789",
            CustomerStatus.BLOCKED, LoyaltyLevel.GOLD);
        Customer updateData = new Customer(null, "Juan", "Updated", "juan@example.com", null,
            null, null);

        when(persistencePort.findById(customerId)).thenReturn(Optional.of(blockedCustomer));

        // WHEN & THEN
        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> service.update(customerId, updateData)
        );
        assertEquals("Cliente bloqueado, no puede actualizarse", exception.getMessage());
        verify(persistencePort, never()).save(any());
    }

    @Test
    @DisplayName("givenExistingCustomer_whenDelete_thenDeleteSuccessful")
    void givenExistingCustomer_whenDelete_thenDeleteSuccessful() {
        // GIVEN
        String customerId = "1";
        Customer customer = new Customer("1", "Juan", "Pérez", "juan@example.com", "123456789",
            CustomerStatus.ACTIVE, LoyaltyLevel.GOLD);

        when(persistencePort.findById(customerId)).thenReturn(Optional.of(customer));

        // WHEN
        service.delete(customerId);

        // THEN
        verify(persistencePort).findById(customerId);
        verify(persistencePort).deleteById(customerId);
    }

    @Test
    @DisplayName("givenNonExistentCustomer_whenDelete_thenNoException")
    void givenNonExistentCustomer_whenDelete_thenNoException() {
        // GIVEN
        String customerId = "999";
        when(persistencePort.findById(customerId)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertDoesNotThrow(() -> service.delete(customerId));
        verify(persistencePort).findById(customerId);
        verify(persistencePort, never()).deleteById(any());
    }

    @Test
    @DisplayName("givenBlockedCustomer_whenDelete_thenThrowException")
    void givenBlockedCustomer_whenDelete_thenThrowException() {
        // GIVEN
        String customerId = "1";
        Customer blockedCustomer = new Customer("1", "Juan", "Pérez", "juan@example.com", "123456789",
            CustomerStatus.BLOCKED, LoyaltyLevel.GOLD);

        when(persistencePort.findById(customerId)).thenReturn(Optional.of(blockedCustomer));

        // WHEN & THEN
        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> service.delete(customerId)
        );
        assertEquals("Cliente bloqueado, no puede eliminarse", exception.getMessage());
        verify(persistencePort, never()).deleteById(any());
    }
}
