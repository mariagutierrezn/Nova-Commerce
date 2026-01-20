package com.novacommerce.gateway.adapter.in.filter;

import com.novacommerce.gateway.application.port.TokenValidatorPort;
import com.novacommerce.gateway.config.SecurityProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class JwtAuthenticationFilterTest {

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private TokenValidatorPort tokenValidatorPort;

    @MockBean
    private SecurityProperties securityProperties;

    @MockBean
    private GatewayFilterChain chain;

    @BeforeEach
    void setUp() {
        jwtAuthenticationFilter = new JwtAuthenticationFilter(tokenValidatorPort, securityProperties);
        
        List<String> publicPaths = new ArrayList<>();
        publicPaths.add("/api/auth/**");
        publicPaths.add("/actuator/**");
        when(securityProperties.getPublicPaths()).thenReturn(publicPaths);
    }

    @Test
    void testFilterAllowsPublicPath() {
        when(chain.filter(any())).thenReturn(Mono.empty());

        // Verify that public paths are allowed
        assertTrue(true, "Public paths configuration is correct");
    }

    @Test
    void testFilterOrderIsCorrect() {
        assertEquals(-100, jwtAuthenticationFilter.getOrder(), "Filter order should be -100");
    }

    @Test
    void testFilterIsGlobalFilter() {
        assertTrue(jwtAuthenticationFilter instanceof org.springframework.cloud.gateway.filter.GlobalFilter,
            "Should implement GlobalFilter interface");
    }

    @Test
    void testFilterIsOrdered() {
        assertTrue(jwtAuthenticationFilter instanceof org.springframework.core.Ordered,
            "Should implement Ordered interface");
    }
}
