
package com.flowforge.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    // Jackson 3: Used by Spring Boot 4 and AI workflow generation.
    @Bean
    public tools.jackson.databind.ObjectMapper objectMapper() {
        return new tools.jackson.databind.ObjectMapper();
    }

    // Jackson 2: Retained for existing execution modules.
    @Bean
    public com.fasterxml.jackson.databind.ObjectMapper legacyObjectMapper() {
        return new com.fasterxml.jackson.databind.ObjectMapper();
    }
}
