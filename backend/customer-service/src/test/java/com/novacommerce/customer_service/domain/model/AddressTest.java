package com.novacommerce.customer_service.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AddressTest")
class AddressTest {

    @Test
    @DisplayName("givenAddress_whenNoArgsConstructor_thenCreateEmpty")
    void givenAddress_whenNoArgsConstructor_thenCreateEmpty() {
        // GIVEN & WHEN
        Address address = new Address();

        // THEN
        assertNotNull(address);
        assertNull(address.getId());
        assertNull(address.getCustomerId());
        assertNull(address.getStreet());
        assertNull(address.getCity());
        assertNull(address.getState());
        assertNull(address.getZipCode());
        assertNull(address.getCountry());
    }

    @Test
    @DisplayName("givenAddress_whenAllArgsConstructor_thenCreateWithValues")
    void givenAddress_whenAllArgsConstructor_thenCreateWithValues() {
        // GIVEN
        String id = "1";
        String customerId = "100";
        String street = "Main Street 123";
        String city = "New York";
        String state = "NY";
        String zipCode = "10001";
        String country = "United States";

        // WHEN
        Address address = new Address(id, customerId, street, city, state, zipCode, country);

        // THEN
        assertEquals("1", address.getId());
        assertEquals("100", address.getCustomerId());
        assertEquals("Main Street 123", address.getStreet());
        assertEquals("New York", address.getCity());
        assertEquals("NY", address.getState());
        assertEquals("10001", address.getZipCode());
        assertEquals("United States", address.getCountry());
    }

    @Test
    @DisplayName("givenAddress_whenSetters_thenUpdateFields")
    void givenAddress_whenSetters_thenUpdateFields() {
        // GIVEN
        Address address = new Address();

        // WHEN
        address.setId("2");
        address.setCustomerId("200");
        address.setStreet("Second Avenue 456");
        address.setCity("Los Angeles");
        address.setState("CA");
        address.setZipCode("90001");
        address.setCountry("United States");

        // THEN
        assertEquals("2", address.getId());
        assertEquals("200", address.getCustomerId());
        assertEquals("Second Avenue 456", address.getStreet());
        assertEquals("Los Angeles", address.getCity());
        assertEquals("CA", address.getState());
        assertEquals("90001", address.getZipCode());
        assertEquals("United States", address.getCountry());
    }

    @Test
    @DisplayName("givenAddress_whenGetters_thenReturnValues")
    void givenAddress_whenGetters_thenReturnValues() {
        // GIVEN
        Address address = new Address("3", "300", "Third Street 789", "Chicago", "IL", "60601", "United States");

        // WHEN & THEN
        assertEquals("3", address.getId());
        assertEquals("300", address.getCustomerId());
        assertEquals("Third Street 789", address.getStreet());
        assertEquals("Chicago", address.getCity());
        assertEquals("IL", address.getState());
        assertEquals("60601", address.getZipCode());
        assertEquals("United States", address.getCountry());
    }

    @Test
    @DisplayName("givenAddress_whenNullFields_thenAllowNull")
    void givenAddress_whenNullFields_thenAllowNull() {
        // GIVEN
        Address address = new Address("4", "400", null, null, null, null, null);

        // WHEN & THEN
        assertNull(address.getStreet());
        assertNull(address.getCity());
        assertNull(address.getState());
        assertNull(address.getZipCode());
        assertNull(address.getCountry());
    }

    @Test
    @DisplayName("givenAddress_whenEmptyStrings_thenAllowEmpty")
    void givenAddress_whenEmptyStrings_thenAllowEmpty() {
        // GIVEN
        Address address = new Address();

        // WHEN
        address.setStreet("");
        address.setCity("");
        address.setState("");
        address.setZipCode("");
        address.setCountry("");

        // THEN
        assertEquals("", address.getStreet());
        assertEquals("", address.getCity());
        assertEquals("", address.getState());
        assertEquals("", address.getZipCode());
        assertEquals("", address.getCountry());
    }

    @Test
    @DisplayName("givenAddress_whenMultipleUpdates_thenFinalStateCorrect")
    void givenAddress_whenMultipleUpdates_thenFinalStateCorrect() {
        // GIVEN
        Address address = new Address();
        address.setId("5");
        address.setCustomerId("500");
        address.setCity("Boston");

        // WHEN
        address.setCity("Cambridge");
        address.setState("MA");
        address.setZipCode("02138");

        // THEN
        assertEquals("5", address.getId());
        assertEquals("500", address.getCustomerId());
        assertEquals("Cambridge", address.getCity());
        assertEquals("MA", address.getState());
        assertEquals("02138", address.getZipCode());
    }

    @Test
    @DisplayName("givenAddress_whenLongStrings_thenStoreCorrectly")
    void givenAddress_whenLongStrings_thenStoreCorrectly() {
        // GIVEN
        String longStreet = "Very Long Street Name with multiple words and numbers 123456789";
        String longCity = "City with Very Long Name";

        // WHEN
        Address address = new Address();
        address.setStreet(longStreet);
        address.setCity(longCity);

        // THEN
        assertEquals(longStreet, address.getStreet());
        assertEquals(longCity, address.getCity());
    }

    @Test
    @DisplayName("givenMultipleAddresses_whenCreated_thenIndependent")
    void givenMultipleAddresses_whenCreated_thenIndependent() {
        // GIVEN
        Address address1 = new Address("1", "100", "Street 1", "City 1", "ST", "12345", "Country 1");
        Address address2 = new Address("2", "200", "Street 2", "City 2", "ST", "54321", "Country 2");

        // WHEN & THEN
        assertNotEquals(address1.getId(), address2.getId());
        assertNotEquals(address1.getCustomerId(), address2.getCustomerId());
        assertNotEquals(address1.getStreet(), address2.getStreet());
        assertEquals("City 1", address1.getCity());
        assertEquals("City 2", address2.getCity());
    }

    @Test
    @DisplayName("givenAddress_whenInternationalData_thenStoreCorrectly")
    void givenAddress_whenInternationalData_thenStoreCorrectly() {
        // GIVEN
        Address address = new Address("1", "100", "Calle Principal 123", "Madrid", "MD", "28001", "España");

        // WHEN & THEN
        assertEquals("Calle Principal 123", address.getStreet());
        assertEquals("Madrid", address.getCity());
        assertEquals("España", address.getCountry());
    }
}
