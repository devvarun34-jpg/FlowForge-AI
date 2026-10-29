
package com.flowforge.backend.execution.action;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class DelayAction implements WorkflowAction {

    private static final long MAX_DELAY_MS = 10_000;

    private final ObjectMapper objectMapper;

    public DelayAction(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String execute(String configuration) throws Exception {
        if (configuration == null || configuration.isBlank()) {
            throw new IllegalArgumentException(
                    "Delay configuration cannot be empty");
        }

        JsonNode config = objectMapper.readTree(configuration);
        JsonNode delayNode = config.get("delayMs");

        if (delayNode == null
                || !delayNode.isIntegralNumber()
                || !delayNode.canConvertToLong()) {
            throw new IllegalArgumentException(
                    "delayMs must be a non-negative integer");
        }

        long delayMs = delayNode.longValue();

        if (delayMs < 0 || delayMs > MAX_DELAY_MS) {
            throw new IllegalArgumentException(
                    "delayMs must be between 0 and 10000 milliseconds");
        }

        Thread.sleep(delayMs);

        return "Delayed for " + delayMs + " ms";
    }
}
