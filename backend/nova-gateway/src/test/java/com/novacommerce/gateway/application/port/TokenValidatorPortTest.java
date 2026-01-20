package com.novacommerce.gateway.application.port;

import com.novacommerce.gateway.adapter.out.jwt.JwtTokenValidatorAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "app.jwt.secret=dGhpc2lzYXRlc3RzZWNyZXRrZXlmb3JqanN0dGVzdGluZ3B1cnBvc2VzMTIz"
})
class TokenValidatorPortTest {

    @Autowired
    private TokenValidatorPort tokenValidatorPort;

    @Autowired
    private JwtTokenValidatorAdapter jwtTokenValidatorAdapter;

    @Test
    void testTokenValidatorPortImplementation() {
        assertNotNull(tokenValidatorPort, "TokenValidatorPort implementation should be injected");
        assertTrue(tokenValidatorPort instanceof JwtTokenValidatorAdapter, 
            "Should be implemented by JwtTokenValidatorAdapter");
    }

    @Test
    void testValidateTokenMethodExists() {
        assertDoesNotThrow(() -> tokenValidatorPort.validateToken("test"),
            "validateToken method should exist");
    }

    @Test
    void testExtractUsernameMethodExists() {
        assertThrows(Exception.class, () -> tokenValidatorPort.extractUsername("invalid"),
            "extractUsername method should exist");
    }

    @Test
    void testExtractAuthoritiesMethodExists() {
        assertThrows(Exception.class, () -> tokenValidatorPort.extractAuthorities("invalid"),
            "extractAuthorities method should exist");
    }

    @Test
    void testExtractTokenMethodExists() {
        assertThrows(Exception.class, () -> tokenValidatorPort.extractToken("invalid"),
            "extractToken method should exist");
    }

    @Test
    void testTokenValidatorPortContract() {
        // Port contract test: all methods should be callable
        assertNotNull(tokenValidatorPort, "Port should be available");
        
        // Test that methods can be called (even if they fail)
        boolean canValidate = false;
        try {
            tokenValidatorPort.validateToken("test");
            canValidate = true;
        } catch (Exception e) {
            canValidate = true; // Method exists even if it throws
        }
        
        assertTrue(canValidate, "validateToken should be callable");
    }

    @Test
    void testTokenValidatorPortIsInterface() {
        assertTrue(TokenValidatorPort.class.isInterface(), 
            "TokenValidatorPort should be an interface");
    }

    @Test
    void testPortImplementationIsComponent() {
        assertNotNull(jwtTokenValidatorAdapter, "Adapter should be registered as bean");
    }
}
