package com.novacommerce.customer_service.adapter.in.web.dto;

import com.novacommerce.customer_service.domain.model.enums.CustomerStatus;
import com.novacommerce.customer_service.domain.model.enums.LoyaltyLevel;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CustomerDto Tests")
class CustomerDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("givenValidDto_whenValidate_thenNoViolations")
    void givenValidDto_whenValidate_thenNoViolations() {
        // GIVEN
        CustomerDto dto = new CustomerDto();
        dto.setId("1");
        dto.setFirstName("Juan");
        dto.setLastName("Pérez");
        dto.setEmail("juan@example.com");
        dto.setPhone("123456789");
        dto.setStatus(CustomerStatus.ACTIVE);
        dto.setLoyaltyLevel(LoyaltyLevel.GOLD);

        // WHEN
        Set<ConstraintViolation<CustomerDto>> violations = validator.validate(dto);

        // THEN
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("givenBlankFirstName_whenValidate_thenViolation")
    void givenBlankFirstName_whenValidate_thenViolation() {
        // GIVEN
        CustomerDto dto = new CustomerDto();
        dto.setFirstName("");
        dto.setLastName("Pérez");
        dto.setEmail("juan@example.com");

        // WHEN
        Set<ConstraintViolation<CustomerDto>> violations = validator.validate(dto);

        // THEN
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("firstName")));
    }

    @Test
    @DisplayName("givenBlankLastName_whenValidate_thenViolation")
    void givenBlankLastName_whenValidate_thenViolation() {
        // GIVEN
        CustomerDto dto = new CustomerDto();
        dto.setFirstName("Juan");
        dto.setLastName("");
        dto.setEmail("juan@example.com");

        // WHEN
        Set<ConstraintViolation<CustomerDto>> violations = validator.validate(dto);

        // THEN
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("lastName")));
    }

    @Test
    @DisplayName("givenInvalidEmail_whenValidate_thenViolation")
    void givenInvalidEmail_whenValidate_thenViolation() {
        // GIVEN
        CustomerDto dto = new CustomerDto();
        dto.setFirstName("Juan");
        dto.setLastName("Pérez");
        dto.setEmail("invalid-email");

        // WHEN
        Set<ConstraintViolation<CustomerDto>> violations = validator.validate(dto);

        // THEN
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("givenBlankEmail_whenValidate_thenViolation")
    void givenBlankEmail_whenValidate_thenViolation() {
        // GIVEN
        CustomerDto dto = new CustomerDto();
        dto.setFirstName("Juan");
        dto.setLastName("Pérez");
        dto.setEmail("");

        // WHEN
        Set<ConstraintViolation<CustomerDto>> violations = validator.validate(dto);

        // THEN
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("givenTooLongFirstName_whenValidate_thenViolation")
    void givenTooLongFirstName_whenValidate_thenViolation() {
        // GIVEN
        CustomerDto dto = new CustomerDto();
        dto.setFirstName("A".repeat(101));
        dto.setLastName("Pérez");
        dto.setEmail("juan@example.com");

        // WHEN
        Set<ConstraintViolation<CustomerDto>> violations = validator.validate(dto);

        // THEN
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("firstName")));
    }

    @Test
    @DisplayName("givenTooLongPhone_whenValidate_thenViolation")
    void givenTooLongPhone_whenValidate_thenViolation() {
        // GIVEN
        CustomerDto dto = new CustomerDto();
        dto.setFirstName("Juan");
        dto.setLastName("Pérez");
        dto.setEmail("juan@example.com");
        dto.setPhone("1".repeat(21));

        // WHEN
        Set<ConstraintViolation<CustomerDto>> violations = validator.validate(dto);

        // THEN
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("phone")));
    }

    @Test
    @DisplayName("givenAllFields_whenGettersSetters_thenCorrect")
    void givenAllFields_whenGettersSetters_thenCorrect() {
        // GIVEN
        CustomerDto dto = new CustomerDto();

        // WHEN
        dto.setId("1");
        dto.setFirstName("Juan");
        dto.setLastName("Pérez");
        dto.setEmail("juan@example.com");
        dto.setPhone("123456789");
        dto.setStatus(CustomerStatus.BLOCKED);
        dto.setLoyaltyLevel(LoyaltyLevel.PLATINUM);

        // THEN
        assertEquals("1", dto.getId());
        assertEquals("Juan", dto.getFirstName());
        assertEquals("Pérez", dto.getLastName());
        assertEquals("juan@example.com", dto.getEmail());
        assertEquals("123456789", dto.getPhone());
        assertEquals(CustomerStatus.BLOCKED, dto.getStatus());
        assertEquals(LoyaltyLevel.PLATINUM, dto.getLoyaltyLevel());
    }

    @Test
    @DisplayName("givenNullOptionalFields_whenValidate_thenNoViolations")
    void givenNullOptionalFields_whenValidate_thenNoViolations() {
        // GIVEN
        CustomerDto dto = new CustomerDto();
        dto.setFirstName("Juan");
        dto.setLastName("Pérez");
        dto.setEmail("juan@example.com");
        dto.setPhone(null);
        dto.setStatus(null);
        dto.setLoyaltyLevel(null);

        // WHEN
        Set<ConstraintViolation<CustomerDto>> violations = validator.validate(dto);

        // THEN
        assertTrue(violations.isEmpty());
    }
}
