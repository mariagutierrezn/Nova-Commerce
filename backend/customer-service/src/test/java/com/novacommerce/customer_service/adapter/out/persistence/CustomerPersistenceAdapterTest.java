package com.novacommerce.customer_service.adapter.out.persistence;

import com.novacommerce.customer_service.adapter.out.persistence.entity.CustomerEntity;
import com.novacommerce.customer_service.adapter.out.persistence.mapper.CustomerEntityMapper;
import com.novacommerce.customer_service.adapter.out.persistence.repository.CustomerRepository;
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
@DisplayName("CustomerPersistenceAdapterTest")
class CustomerPersistenceAdapterTest {

    @Mock
    private CustomerRepository repository;

    @Mock
    private CustomerEntityMapper mapper;

    @InjectMocks
    private CustomerPersistenceAdapter adapter;

    @Test
    @DisplayName("givenCustomer_whenSave_thenRepositorySavesCalled")
    void givenCustomer_whenSave_thenRepositorySavesCalled() {
        // GIVEN
        Customer customer = new Customer(null, "Juan", "Pérez", "juan@example.com", "123456789",
            CustomerStatus.ACTIVE, LoyaltyLevel.GOLD);
        CustomerEntity entity = new CustomerEntity();
        entity.setId("1");
        entity.setFirstName("Juan");
        CustomerEntity savedEntity = new CustomerEntity();
        savedEntity.setId("1");
        savedEntity.setFirstName("Juan");

        when(mapper.toEntity(customer)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(savedEntity);
        when(mapper.toDomain(savedEntity)).thenReturn(
            new Customer("1", "Juan", "Pérez", "juan@example.com", "123456789",
                CustomerStatus.ACTIVE, LoyaltyLevel.GOLD)
        );

        // WHEN
        Customer result = adapter.save(customer);

        // THEN
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("Juan", result.getFirstName());
        verify(mapper).toEntity(customer);
        verify(repository).save(entity);
        verify(mapper).toDomain(savedEntity);
    }

    @Test
    @DisplayName("givenValidId_whenFindById_thenReturnCustomer")
    void givenValidId_whenFindById_thenReturnCustomer() {
        // GIVEN
        String customerId = "1";
        CustomerEntity entity = new CustomerEntity();
        entity.setId("1");
        entity.setFirstName("María");
        Customer customer = new Customer("1", "María", "García", "maria@example.com", "987654321",
            CustomerStatus.ACTIVE, LoyaltyLevel.SILVER);

        when(repository.findById(customerId)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(customer);

        // WHEN
        Optional<Customer> result = adapter.findById(customerId);

        // THEN
        assertTrue(result.isPresent());
        assertEquals("María", result.get().getFirstName());
        verify(repository).findById(customerId);
        verify(mapper).toDomain(entity);
    }

    @Test
    @DisplayName("givenInvalidId_whenFindById_thenReturnEmpty")
    void givenInvalidId_whenFindById_thenReturnEmpty() {
        // GIVEN
        String customerId = "999";
        when(repository.findById(customerId)).thenReturn(Optional.empty());

        // WHEN
        Optional<Customer> result = adapter.findById(customerId);

        // THEN
        assertTrue(result.isEmpty());
        verify(repository).findById(customerId);
        verify(mapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("givenMultipleCustomers_whenFindAll_thenReturnList")
    void givenMultipleCustomers_whenFindAll_thenReturnList() {
        // GIVEN
        CustomerEntity entity1 = new CustomerEntity();
        entity1.setId("1");
        entity1.setFirstName("Juan");
        CustomerEntity entity2 = new CustomerEntity();
        entity2.setId("2");
        entity2.setFirstName("María");

        when(repository.findAll()).thenReturn(List.of(entity1, entity2));
        when(mapper.toDomain(entity1)).thenReturn(
            new Customer("1", "Juan", "Pérez", "juan@example.com", "123456789",
                CustomerStatus.ACTIVE, LoyaltyLevel.GOLD)
        );
        when(mapper.toDomain(entity2)).thenReturn(
            new Customer("2", "María", "García", "maria@example.com", "987654321",
                CustomerStatus.ACTIVE, LoyaltyLevel.SILVER)
        );

        // WHEN
        List<Customer> result = adapter.findAll();

        // THEN
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(repository).findAll();
        verify(mapper, times(2)).toDomain(any());
    }

    @Test
    @DisplayName("givenEmptyRepository_whenFindAll_thenReturnEmptyList")
    void givenEmptyRepository_whenFindAll_thenReturnEmptyList() {
        // GIVEN
        when(repository.findAll()).thenReturn(List.of());

        // WHEN
        List<Customer> result = adapter.findAll();

        // THEN
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository).findAll();
        verify(mapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("givenValidId_whenDeleteById_thenRepositoryDeleteCalled")
    void givenValidId_whenDeleteById_thenRepositoryDeleteCalled() {
        // GIVEN
        String customerId = "1";

        // WHEN
        adapter.deleteById(customerId);

        // THEN
        verify(repository).deleteById(customerId);
    }

    @Test
    @DisplayName("givenExistingEmail_whenExistsByEmail_thenReturnTrue")
    void givenExistingEmail_whenExistsByEmail_thenReturnTrue() {
        // GIVEN
        String email = "juan@example.com";
        when(repository.existsByEmail(email)).thenReturn(true);

        // WHEN
        boolean result = adapter.existsByEmail(email);

        // THEN
        assertTrue(result);
        verify(repository).existsByEmail(email);
    }

    @Test
    @DisplayName("givenNonExistingEmail_whenExistsByEmail_thenReturnFalse")
    void givenNonExistingEmail_whenExistsByEmail_thenReturnFalse() {
        // GIVEN
        String email = "nonexistent@example.com";
        when(repository.existsByEmail(email)).thenReturn(false);

        // WHEN
        boolean result = adapter.existsByEmail(email);

        // THEN
        assertFalse(result);
        verify(repository).existsByEmail(email);
    }
}
