package com.novacommerce.customer_service.domain.model;

import com.novacommerce.customer_service.domain.model.enums.CustomerStatus;
import com.novacommerce.customer_service.domain.model.enums.LoyaltyLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CustomerTest")
class CustomerTest {

    @Test
    @DisplayName("givenCustomer_whenNoArgsConstructor_thenCreateEmpty")
    void givenCustomer_whenNoArgsConstructor_thenCreateEmpty() {
        // GIVEN & WHEN
        Customer customer = new Customer();

        // THEN
        assertNotNull(customer);
        assertNull(customer.getId());
        assertNull(customer.getFirstName());
        assertNull(customer.getLastName());
        assertNull(customer.getEmail());
        assertNull(customer.getPhone());
        assertNull(customer.getStatus());
        assertNull(customer.getLoyaltyLevel());
    }

    @Test
    @DisplayName("givenCustomer_whenAllArgsConstructor_thenCreateWithValues")
    void givenCustomer_whenAllArgsConstructor_thenCreateWithValues() {
        // GIVEN
        String id = "1";
        String firstName = "Juan";
        String lastName = "Pérez";
        String email = "juan@example.com";
        String phone = "123456789";
        CustomerStatus status = CustomerStatus.ACTIVE;
        LoyaltyLevel loyaltyLevel = LoyaltyLevel.GOLD;

        // WHEN
        Customer customer = new Customer(id, firstName, lastName, email, phone, status, loyaltyLevel);

        // THEN
        assertEquals("1", customer.getId());
        assertEquals("Juan", customer.getFirstName());
        assertEquals("Pérez", customer.getLastName());
        assertEquals("juan@example.com", customer.getEmail());
        assertEquals("123456789", customer.getPhone());
        assertEquals(CustomerStatus.ACTIVE, customer.getStatus());
        assertEquals(LoyaltyLevel.GOLD, customer.getLoyaltyLevel());
    }

    @Test
    @DisplayName("givenCustomer_whenSetters_thenUpdateFields")
    void givenCustomer_whenSetters_thenUpdateFields() {
        // GIVEN
        Customer customer = new Customer();

        // WHEN
        customer.setId("2");
        customer.setFirstName("María");
        customer.setLastName("García");
        customer.setEmail("maria@example.com");
        customer.setPhone("987654321");
        customer.setStatus(CustomerStatus.INACTIVE);
        customer.setLoyaltyLevel(LoyaltyLevel.SILVER);

        // THEN
        assertEquals("2", customer.getId());
        assertEquals("María", customer.getFirstName());
        assertEquals("García", customer.getLastName());
        assertEquals("maria@example.com", customer.getEmail());
        assertEquals("987654321", customer.getPhone());
        assertEquals(CustomerStatus.INACTIVE, customer.getStatus());
        assertEquals(LoyaltyLevel.SILVER, customer.getLoyaltyLevel());
    }

    @Test
    @DisplayName("givenCustomer_whenNullPhone_thenAllowNull")
    void givenCustomer_whenNullPhone_thenAllowNull() {
        // GIVEN
        Customer customer = new Customer("3", "Carlos", "López", "carlos@example.com", null,
            CustomerStatus.BLOCKED, LoyaltyLevel.BRONZE);

        // WHEN & THEN
        assertNull(customer.getPhone());
        assertEquals("Carlos", customer.getFirstName());
    }

    @Test
    @DisplayName("givenCustomer_whenAllStatuses_thenSetCorrectly")
    void givenCustomer_whenAllStatuses_thenSetCorrectly() {
        // Test all statuses
        for (CustomerStatus status : CustomerStatus.values()) {
            // GIVEN
            Customer customer = new Customer();

            // WHEN
            customer.setStatus(status);

            // THEN
            assertEquals(status, customer.getStatus());
        }
    }

    @Test
    @DisplayName("givenCustomer_whenAllLoyaltyLevels_thenSetCorrectly")
    void givenCustomer_whenAllLoyaltyLevels_thenSetCorrectly() {
        // Test all loyalty levels
        for (LoyaltyLevel level : LoyaltyLevel.values()) {
            // GIVEN
            Customer customer = new Customer();

            // WHEN
            customer.setLoyaltyLevel(level);

            // THEN
            assertEquals(level, customer.getLoyaltyLevel());
        }
    }

    @Test
    @DisplayName("givenCustomer_whenGetters_thenReturnValues")
    void givenCustomer_whenGetters_thenReturnValues() {
        // GIVEN
        Customer customer = new Customer("4", "Ana", "Rodríguez", "ana@example.com", "555123456",
            CustomerStatus.ACTIVE, LoyaltyLevel.PLATINUM);

        // WHEN & THEN
        assertEquals("4", customer.getId());
        assertEquals("Ana", customer.getFirstName());
        assertEquals("Rodríguez", customer.getLastName());
        assertEquals("ana@example.com", customer.getEmail());
        assertEquals("555123456", customer.getPhone());
        assertEquals(CustomerStatus.ACTIVE, customer.getStatus());
        assertEquals(LoyaltyLevel.PLATINUM, customer.getLoyaltyLevel());
    }

    @Test
    @DisplayName("givenCustomer_whenMultipleUpdates_thenFinalStateCorrect")
    void givenCustomer_whenMultipleUpdates_thenFinalStateCorrect() {
        // GIVEN
        Customer customer = new Customer();
        customer.setId("5");
        customer.setFirstName("Test");
        customer.setStatus(CustomerStatus.ACTIVE);

        // WHEN
        customer.setFirstName("Updated");
        customer.setStatus(CustomerStatus.INACTIVE);
        customer.setLoyaltyLevel(LoyaltyLevel.GOLD);

        // THEN
        assertEquals("5", customer.getId());
        assertEquals("Updated", customer.getFirstName());
        assertEquals(CustomerStatus.INACTIVE, customer.getStatus());
        assertEquals(LoyaltyLevel.GOLD, customer.getLoyaltyLevel());
    }

    @Test
    @DisplayName("givenTwoCustomers_whenSameValues_thenEqualityBehavior")
    void givenTwoCustomers_whenSameValues_thenEqualityBehavior() {
        // GIVEN
        Customer customer1 = new Customer("1", "Juan", "Pérez", "juan@example.com", "123456789",
            CustomerStatus.ACTIVE, LoyaltyLevel.GOLD);
        Customer customer2 = new Customer("1", "Juan", "Pérez", "juan@example.com", "123456789",
            CustomerStatus.ACTIVE, LoyaltyLevel.GOLD);

        // WHEN & THEN
        assertEquals(customer1.getId(), customer2.getId());
        assertEquals(customer1.getFirstName(), customer2.getFirstName());
        assertEquals(customer1.getEmail(), customer2.getEmail());
    }

    @Test
    @DisplayName("givenCustomer_whenEmptyStrings_thenAllowEmpty")
    void givenCustomer_whenEmptyStrings_thenAllowEmpty() {
        // GIVEN
        Customer customer = new Customer();

        // WHEN
        customer.setFirstName("");
        customer.setLastName("");
        customer.setEmail("test@example.com");
        customer.setPhone("");

        // THEN
        assertEquals("", customer.getFirstName());
        assertEquals("", customer.getLastName());
        assertEquals("", customer.getPhone());
    }
}
