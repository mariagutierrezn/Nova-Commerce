package com.novacommerce.user_service.web.rest.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InvalidCredentialsException Tests")
class InvalidCredentialsExceptionTest {

    @Test
    @DisplayName("Should create exception with custom message")
    void testCreateWithMessage() {
        String message = "Usuario o contraseña inválidos";
        
        InvalidCredentialsException exception = new InvalidCredentialsException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("Should create exception with default message")
    void testCreateWithDefaultMessage() {
        InvalidCredentialsException exception = new InvalidCredentialsException();

        assertNotNull(exception);
        assertEquals("Credenciales inválidas", exception.getMessage());
    }

    @Test
    @DisplayName("Should extend AuthServiceException")
    void testInheritsFromAuthServiceException() {
        InvalidCredentialsException exception = new InvalidCredentialsException();
        
        assertTrue(exception instanceof AuthServiceException);
    }

    @Test
    @DisplayName("Should be RuntimeException")
    void testIsRuntimeException() {
        InvalidCredentialsException exception = new InvalidCredentialsException();
        
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("Should throw and catch exception")
    void testThrowAndCatch() {
        String expectedMessage = "Usuario bloqueado";
        
        assertThrows(InvalidCredentialsException.class, () -> {
            throw new InvalidCredentialsException(expectedMessage);
        });
    }

    @Test
    @DisplayName("Should preserve message when thrown")
    void testMessagePreservation() {
        String message = "Contraseña incorrecta";
        
        InvalidCredentialsException exception = assertThrows(
            InvalidCredentialsException.class,
            () -> { throw new InvalidCredentialsException(message); }
        );
        
        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("Should have cause capability")
    void testWithCause() {
        InvalidCredentialsException exception = new InvalidCredentialsException("Credenciales inválidas");
        
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Should work with different messages")
    void testDifferentMessages() {
        InvalidCredentialsException ex1 = new InvalidCredentialsException("Mensaje 1");
        InvalidCredentialsException ex2 = new InvalidCredentialsException("Mensaje 2");
        
        assertNotEquals(ex1.getMessage(), ex2.getMessage());
    }

    @Test
    @DisplayName("Should work with empty message")
    void testEmptyMessage() {
        InvalidCredentialsException exception = new InvalidCredentialsException("");
        
        assertNotNull(exception);
        assertEquals("", exception.getMessage());
    }
}
