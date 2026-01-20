package com.novacommerce.customer_service.adapter.out.persistence.entity;

import com.novacommerce.customer_service.domain.model.enums.CustomerStatus;
import com.novacommerce.customer_service.domain.model.enums.LoyaltyLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CustomerEntityTest")
class CustomerEntityTest {

    @Test
    @DisplayName("givenCustomerEntity_whenNoArgsConstructor_thenCreateEmpty")
    void givenCustomerEntity_whenNoArgsConstructor_thenCreateEmpty() {
        // GIVEN & WHEN
        CustomerEntity entity = new CustomerEntity();

        // THEN
        assertNotNull(entity);
        assertNull(entity.getId());
        assertNull(entity.getFirstName());
        assertNull(entity.getLastName());
        assertNull(entity.getEmail());
        assertNull(entity.getPhone());
        assertNull(entity.getStatus());
        assertNull(entity.getLoyaltyLevel());
    }

    @Test
    @DisplayName("givenCustomerEntity_whenSettersUsed_thenFieldsUpdated")
    void givenCustomerEntity_whenSettersUsed_thenFieldsUpdated() {
        // GIVEN
        CustomerEntity entity = new CustomerEntity();

        // WHEN
        entity.setId("1");
        entity.setFirstName("Juan");
        entity.setLastName("Pérez");
        entity.setEmail("juan@example.com");
        entity.setPhone("123456789");
        entity.setStatus(CustomerStatus.ACTIVE);
        entity.setLoyaltyLevel(LoyaltyLevel.GOLD);

        // THEN
        assertEquals("1", entity.getId());
        assertEquals("Juan", entity.getFirstName());
        assertEquals("Pérez", entity.getLastName());
        assertEquals("juan@example.com", entity.getEmail());
        assertEquals("123456789", entity.getPhone());
        assertEquals(CustomerStatus.ACTIVE, entity.getStatus());
        assertEquals(LoyaltyLevel.GOLD, entity.getLoyaltyLevel());
    }

    @Test
    @DisplayName("givenCustomerEntity_whenNullPhone_thenAllowNull")
    void givenCustomerEntity_whenNullPhone_thenAllowNull() {
        // GIVEN
        CustomerEntity entity = new CustomerEntity();

        // WHEN
        entity.setId("2");
        entity.setFirstName("María");
        entity.setLastName("García");
        entity.setEmail("maria@example.com");
        entity.setPhone(null);
        entity.setStatus(CustomerStatus.INACTIVE);
        entity.setLoyaltyLevel(LoyaltyLevel.SILVER);

        // THEN
        assertNull(entity.getPhone());
        assertEquals("María", entity.getFirstName());
        assertEquals(CustomerStatus.INACTIVE, entity.getStatus());
    }

    @Test
    @DisplayName("givenCustomerEntity_whenAllStatuses_thenSetCorrectly")
    void givenCustomerEntity_whenAllStatuses_thenSetCorrectly() {
        // Test all statuses
        for (CustomerStatus status : CustomerStatus.values()) {
            // GIVEN
            CustomerEntity entity = new CustomerEntity();

            // WHEN
            entity.setStatus(status);

            // THEN
            assertEquals(status, entity.getStatus());
        }
    }

    @Test
    @DisplayName("givenCustomerEntity_whenAllLoyaltyLevels_thenSetCorrectly")
    void givenCustomerEntity_whenAllLoyaltyLevels_thenSetCorrectly() {
        // Test all loyalty levels
        for (LoyaltyLevel level : LoyaltyLevel.values()) {
            // GIVEN
            CustomerEntity entity = new CustomerEntity();

            // WHEN
            entity.setLoyaltyLevel(level);

            // THEN
            assertEquals(level, entity.getLoyaltyLevel());
        }
    }

    @Test
    @DisplayName("givenCustomerEntity_whenGettersUsed_thenReturnValues")
    void givenCustomerEntity_whenGettersUsed_thenReturnValues() {
        // GIVEN
        CustomerEntity entity = new CustomerEntity();
        entity.setId("3");
        entity.setFirstName("Carlos");
        entity.setLastName("López");
        entity.setEmail("carlos@example.com");
        entity.setPhone("555123456");
        entity.setStatus(CustomerStatus.BLOCKED);
        entity.setLoyaltyLevel(LoyaltyLevel.BRONZE);

        // WHEN & THEN
        assertEquals("3", entity.getId());
        assertEquals("Carlos", entity.getFirstName());
        assertEquals("López", entity.getLastName());
        assertEquals("carlos@example.com", entity.getEmail());
        assertEquals("555123456", entity.getPhone());
        assertEquals(CustomerStatus.BLOCKED, entity.getStatus());
        assertEquals(LoyaltyLevel.BRONZE, entity.getLoyaltyLevel());
    }

    @Test
    @DisplayName("givenCustomerEntity_whenMultipleUpdates_thenFinalStateCorrect")
    void givenCustomerEntity_whenMultipleUpdates_thenFinalStateCorrect() {
        // GIVEN
        CustomerEntity entity = new CustomerEntity();
        entity.setId("4");
        entity.setFirstName("Ana");
        entity.setStatus(CustomerStatus.ACTIVE);

        // WHEN
        entity.setFirstName("Andrea");
        entity.setStatus(CustomerStatus.INACTIVE);
        entity.setLoyaltyLevel(LoyaltyLevel.PLATINUM);

        // THEN
        assertEquals("4", entity.getId());
        assertEquals("Andrea", entity.getFirstName());
        assertEquals(CustomerStatus.INACTIVE, entity.getStatus());
        assertEquals(LoyaltyLevel.PLATINUM, entity.getLoyaltyLevel());
    }

    @Test
    @DisplayName("givenCustomerEntity_whenEmptyStrings_thenAllowEmpty")
    void givenCustomerEntity_whenEmptyStrings_thenAllowEmpty() {
        // GIVEN
        CustomerEntity entity = new CustomerEntity();

        // WHEN
        entity.setFirstName("");
        entity.setLastName("");
        entity.setEmail("test@example.com");
        entity.setPhone("");

        // THEN
        assertEquals("", entity.getFirstName());
        assertEquals("", entity.getLastName());
        assertEquals("", entity.getPhone());
        assertEquals("test@example.com", entity.getEmail());
    }
}
