package com.novacommerce.gateway.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "gateway")
public class SecurityProperties {

    private List<String> publicPaths = new ArrayList<>();
    
    @PostConstruct
    public void init() {
        log.info("SecurityProperties initialized. Public paths loaded: {}", publicPaths);
        log.info("Total public paths count: {}", publicPaths.size());
    }
}
