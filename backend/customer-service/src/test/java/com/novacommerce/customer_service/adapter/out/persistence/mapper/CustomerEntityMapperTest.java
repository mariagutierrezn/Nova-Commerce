package com.novacommerce.customer_service.adapter.out.persistence.mapper;

import com.novacommerce.customer_service.adapter.out.persistence.entity.CustomerEntity;
import com.novacommerce.customer_service.domain.model.Customer;
import com.novacommerce.customer_service.domain.model.enums.CustomerStatus;
import com.novacommerce.customer_service.domain.model.enums.LoyaltyLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CustomerEntityMapperTest")
class CustomerEntityMapperTest {

    private CustomerEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CustomerEntityMapperImpl();
    }

    @Test
    @DisplayName("givenCustomer_whenToEntity_thenEntityCreatedCorrectly")
    void givenCustomer_whenToEntity_thenEntityCreatedCorrectly() {
        // GIVEN
        Customer customer = new Customer("1", "Juan", "Pérez", "juan@example.com", "123456789",
            CustomerStatus.ACTIVE, LoyaltyLevel.GOLD);

        // WHEN
        CustomerEntity entity = mapper.toEntity(customer);

        // THEN
        assertNotNull(entity);
        assertEquals("1", entity.getId());
        assertEquals("Juan", entity.getFirstName());
        assertEquals("Pérez", entity.getLastName());
        assertEquals("juan@example.com", entity.getEmail());
        assertEquals("123456789", entity.getPhone());
        assertEquals(CustomerStatus.ACTIVE, entity.getStatus());
        assertEquals(LoyaltyLevel.GOLD, entity.getLoyaltyLevel());
    }

    @Test
    @DisplayName("givenEntity_whenToDomain_thenCustomerCreatedCorrectly")
    void givenEntity_whenToDomain_thenCustomerCreatedCorrectly() {
        // GIVEN
        CustomerEntity entity = new CustomerEntity();
        entity.setId("2");
        entity.setFirstName("María");
        entity.setLastName("García");
        entity.setEmail("maria@example.com");
        entity.setPhone("987654321");
        entity.setStatus(CustomerStatus.INACTIVE);
        entity.setLoyaltyLevel(LoyaltyLevel.SILVER);

        // WHEN
        Customer customer = mapper.toDomain(entity);

        // THEN
        assertNotNull(customer);
        assertEquals("2", customer.getId());
        assertEquals("María", customer.getFirstName());
        assertEquals("García", customer.getLastName());
        assertEquals("maria@example.com", customer.getEmail());
        assertEquals("987654321", customer.getPhone());
        assertEquals(CustomerStatus.INACTIVE, customer.getStatus());
        assertEquals(LoyaltyLevel.SILVER, customer.getLoyaltyLevel());
    }

    @Test
    @DisplayName("givenCustomerWithNullFields_whenToEntity_thenMapNullsCorrectly")
    void givenCustomerWithNullFields_whenToEntity_thenMapNullsCorrectly() {
        // GIVEN
        Customer customer = new Customer("3", "Carlos", "López", "carlos@example.com", null,
            CustomerStatus.ACTIVE, null);

        // WHEN
        CustomerEntity entity = mapper.toEntity(customer);

        // THEN
        assertNotNull(entity);
        assertEquals("3", entity.getId());
        assertEquals("Carlos", entity.getFirstName());
        assertNull(entity.getPhone());
        assertNull(entity.getLoyaltyLevel());
    }

    @Test
    @DisplayName("givenEntityAndDomain_whenBidirectionalMapping_thenDataConsistency")
    void givenEntityAndDomain_whenBidirectionalMapping_thenDataConsistency() {
        // GIVEN
        Customer originalCustomer = new Customer("4", "Ana", "Rodríguez", "ana@example.com", "555123456",
            CustomerStatus.BLOCKED, LoyaltyLevel.PLATINUM);

        // WHEN - Customer -> Entity
        CustomerEntity entity = mapper.toEntity(originalCustomer);
        // Entity -> Customer
        Customer mappedCustomer = mapper.toDomain(entity);

        // THEN - Data should be consistent
        assertNotNull(mappedCustomer);
        assertEquals(originalCustomer.getId(), mappedCustomer.getId());
        assertEquals(originalCustomer.getFirstName(), mappedCustomer.getFirstName());
        assertEquals(originalCustomer.getLastName(), mappedCustomer.getLastName());
        assertEquals(originalCustomer.getEmail(), mappedCustomer.getEmail());
        assertEquals(originalCustomer.getPhone(), mappedCustomer.getPhone());
        assertEquals(originalCustomer.getStatus(), mappedCustomer.getStatus());
        assertEquals(originalCustomer.getLoyaltyLevel(), mappedCustomer.getLoyaltyLevel());
    }

    @Test
    @DisplayName("givenAllCustomerStatuses_whenMapping_thenStatusesPreserved")
    void givenAllCustomerStatuses_whenMapping_thenStatusesPreserved() {
        // Test all customer statuses
        for (CustomerStatus status : CustomerStatus.values()) {
            // GIVEN
            Customer customer = new Customer("5", "Test", "User", "test@example.com", "123456",
                status, LoyaltyLevel.GOLD);

            // WHEN
            CustomerEntity entity = mapper.toEntity(customer);
            Customer mappedBack = mapper.toDomain(entity);

            // THEN
            assertEquals(status, mappedBack.getStatus());
        }
    }

    @Test
    @DisplayName("givenAllLoyaltyLevels_whenMapping_thenLevelsPreserved")
    void givenAllLoyaltyLevels_whenMapping_thenLevelsPreserved() {
        // Test all loyalty levels
        for (LoyaltyLevel level : LoyaltyLevel.values()) {
            // GIVEN
            Customer customer = new Customer("6", "Test", "User", "test@example.com", "123456",
                CustomerStatus.ACTIVE, level);

            // WHEN
            CustomerEntity entity = mapper.toEntity(customer);
            Customer mappedBack = mapper.toDomain(entity);

            // THEN
            assertEquals(level, mappedBack.getLoyaltyLevel());
        }
    }
}
