package com.novacommerce.gateway.adapter.out.header;

import com.novacommerce.gateway.routing.RouteConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HeaderUtilsTest {

    @Test
    void testHeaderConstantValuesAreDefined() {
        // Verify that the constants from RouteConstants are properly defined
        assertEquals("Authorization", RouteConstants.HEADER_AUTHORIZATION);
        assertEquals("X-Username", RouteConstants.HEADER_USERNAME);
        assertEquals("X-Authorities", RouteConstants.HEADER_AUTHORITIES);
        assertEquals("Bearer ", RouteConstants.BEARER_PREFIX);
    }

    @Test
    void testConstructorThrowsException() {
        Exception exception = assertThrows(Exception.class, () -> {
            java.lang.reflect.Constructor<?> constructor = HeaderUtils.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        });
        assertTrue(exception.getCause() instanceof UnsupportedOperationException,
            "Constructor should throw UnsupportedOperationException");
    }

    @Test
    void testHeaderUtilsIsUtilityClass() {
        // Verify HeaderUtils is a utility class with static methods
        assertTrue(HeaderUtils.class.getDeclaredMethods().length > 0,
            "Should have utility methods");
    }
}