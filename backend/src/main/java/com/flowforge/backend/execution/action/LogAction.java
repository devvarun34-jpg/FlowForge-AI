
package com.flowforge.backend.execution.action;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class LogAction implements WorkflowAction {

    private final ObjectMapper objectMapper;

    public LogAction(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String execute(String configuration) throws Exception {
        if (configuration == null || configuration.isBlank()) {
            throw new IllegalArgumentException(
                    "Log configuration cannot be empty");
        }

        JsonNode config = objectMapper.readTree(configuration);
        JsonNode messageNode = config.get("message");

        if (messageNode == null || !messageNode.isTextual()) {
            throw new IllegalArgumentException(
                    "Log configuration must contain a text message");
        }

        String message = messageNode.asText();

        System.out.println("[FlowForge LOG] " + message);

        return message;
    }
}
