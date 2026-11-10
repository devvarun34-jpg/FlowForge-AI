
package com.flowforge.backend.ai.validation;

import java.net.URI;
import java.util.List;

import org.springframework.stereotype.Component;

import tools.jackson.databind.JsonNode;
import com.flowforge.backend.ai.dto.GeneratedEdge;
import com.flowforge.backend.ai.dto.GeneratedNode;
import com.flowforge.backend.ai.dto.WorkflowGenerationResponse;
import com.flowforge.backend.workflow.entity.NodeType;

@Component
public class GeneratedWorkflowValidator {

    private static final int MAX_NODES = 10;
    private static final int MAX_SUMMARY_LENGTH = 500;
    private static final int MAX_LOG_MESSAGE_LENGTH = 1000;
    private static final int MAX_DELAY_MS = 10_000;
    private static final double MAX_POSITION = 10_000;

    public void validate(WorkflowGenerationResponse response) {

        if (response == null) {
            throw new IllegalArgumentException(
                    "Generated workflow cannot be null.");
        }

        validateSummary(response.summary());
        validateNodes(response.nodes());
        validateEdges(response.nodes(), response.edges());
    }

    private void validateSummary(String summary) {

        if (summary == null || summary.isBlank()) {
            throw new IllegalArgumentException(
                    "Workflow summary cannot be empty.");
        }

        if (summary.length() > MAX_SUMMARY_LENGTH) {
            throw new IllegalArgumentException(
                    "Workflow summary is too long.");
        }
    }

    private void validateNodes(List<GeneratedNode> nodes) {

        if (nodes == null || nodes.isEmpty()) {
            throw new IllegalArgumentException(
                    "Workflow must contain at least one node.");
        }

        if (nodes.size() > MAX_NODES) {
            throw new IllegalArgumentException(
                    "Workflow cannot contain more than "
                    + MAX_NODES + " nodes.");
        }

        for (int i = 0; i < nodes.size(); i++) {

            GeneratedNode node = nodes.get(i);

            if (node == null) {
                throw new IllegalArgumentException(
                        "Node at index " + i + " cannot be null.");
            }

            validatePosition(node, i);
            validateNodeConfiguration(node, i);
        }
    }

    private void validatePosition(GeneratedNode node, int index) {

        Double x = node.positionX();
        Double y = node.positionY();

        if (x == null || y == null
                || !Double.isFinite(x)
                || !Double.isFinite(y)
                || x < 0 || y < 0
                || x > MAX_POSITION
                || y > MAX_POSITION) {

            throw new IllegalArgumentException(
                    "Node " + index
                    + " has invalid position coordinates.");
        }
    }

    private void validateNodeConfiguration(
            GeneratedNode node, int index) {

        NodeType type = node.type();
        JsonNode config = node.configuration();

        if (type == null) {
            throw new IllegalArgumentException(
                    "Node " + index + " has no node type.");
        }

        if (config == null || !config.isObject()) {
            throw new IllegalArgumentException(
                    "Node " + index
                    + " must have an object configuration.");
        }

        switch (type) {

            case HTTP -> validateHttpConfiguration(config, index);

            case DELAY -> validateDelayConfiguration(config, index);

            case LOG -> validateLogConfiguration(config, index);

            default -> throw new IllegalArgumentException(
                    "Unsupported node type: " + type);
        }
    }

    private void validateHttpConfiguration(
            JsonNode config, int index) {

        JsonNode methodNode = config.get("method");
        JsonNode urlNode = config.get("url");

        if (methodNode == null || !methodNode.isTextual()
                || urlNode == null || !urlNode.isTextual()) {

            throw new IllegalArgumentException(
                    "HTTP node " + index
                    + " requires textual method and url fields.");
        }

        String method = methodNode.asText().toUpperCase();

        if (!List.of("GET", "POST", "PUT", "DELETE")
                .contains(method)) {

            throw new IllegalArgumentException(
                    "HTTP node " + index
                    + " uses an unsupported method.");
        }

        String url = urlNode.asText();

        if (url.isBlank()) {
            throw new IllegalArgumentException(
                    "HTTP node " + index + " has an empty URL.");
        }

        try {
            URI uri = URI.create(url);

            String scheme = uri.getScheme();
            String host = uri.getHost();

            if (scheme == null
                    || !(scheme.equalsIgnoreCase("http")
                    || scheme.equalsIgnoreCase("https"))) {

                throw new IllegalArgumentException(
                        "Only HTTP and HTTPS URLs are allowed.");
            }

            if (host == null || uri.getUserInfo() != null) {
                throw new IllegalArgumentException(
                        "HTTP URL must contain a valid hostname "
                        + "without user information.");
            }

            if (uri.getPort() != -1
                    && uri.getPort() != 80
                    && uri.getPort() != 443) {

                throw new IllegalArgumentException(
                        "Only ports 80 and 443 are allowed.");
            }

            String normalizedHost = host.toLowerCase();

            if (normalizedHost.equals("localhost")
                    || normalizedHost.endsWith(".localhost")
                    || normalizedHost.endsWith(".local")) {

                throw new IllegalArgumentException(
                        "Local hostnames are not allowed.");
            }

        } catch (IllegalArgumentException exception) {

            throw new IllegalArgumentException(
                    "HTTP node " + index
                    + " contains an invalid or disallowed URL.",
                    exception);
        }

        if (config.has("body") && config.get("body").isMissingNode()) {
            throw new IllegalArgumentException(
                    "HTTP request body is invalid.");
        }
    }

    private void validateDelayConfiguration(
            JsonNode config, int index) {

        JsonNode delayNode = config.get("delayMs");

        if (delayNode == null
                || !delayNode.isIntegralNumber()
                || !delayNode.canConvertToInt()) {

            throw new IllegalArgumentException(
                    "DELAY node " + index
                    + " requires an integer delayMs value.");
        }

        int delayMs = delayNode.intValue();

        if (delayMs < 0 || delayMs > MAX_DELAY_MS) {
            throw new IllegalArgumentException(
                    "DELAY node " + index
                    + " must have delayMs between 0 and "
                    + MAX_DELAY_MS + ".");
        }
    }

    private void validateLogConfiguration(
            JsonNode config, int index) {

        JsonNode messageNode = config.get("message");

        if (messageNode == null || !messageNode.isTextual()) {
            throw new IllegalArgumentException(
                    "LOG node " + index
                    + " requires a textual message.");
        }

        String message = messageNode.asText();

        if (message.isBlank()
                || message.length() > MAX_LOG_MESSAGE_LENGTH) {

            throw new IllegalArgumentException(
                    "LOG node " + index
                    + " must have a non-empty message "
                    + "of at most "
                    + MAX_LOG_MESSAGE_LENGTH + " characters.");
        }
    }

    private void validateEdges(
            List<GeneratedNode> nodes,
            List<GeneratedEdge> edges) {

        int nodeCount = nodes.size();

        if (edges == null || edges.size() != nodeCount - 1) {
            throw new IllegalArgumentException(
                    "A linear workflow must contain exactly "
                    + (nodeCount - 1) + " edges.");
        }

        int[] incoming = new int[nodeCount];
        int[] outgoing = new int[nodeCount];
        int[] nextNode = new int[nodeCount];

        java.util.Arrays.fill(nextNode, -1);

        for (GeneratedEdge edge : edges) {

            if (edge == null) {
                throw new IllegalArgumentException(
                        "Workflow edges cannot be null.");
            }

            int source = edge.sourceNodeIndex();
            int target = edge.targetNodeIndex();

            if (source < 0 || source >= nodeCount
                    || target < 0 || target >= nodeCount) {

                throw new IllegalArgumentException(
                        "Edge contains an invalid node index.");
            }

            if (source == target) {
                throw new IllegalArgumentException(
                        "A node cannot connect to itself.");
            }

            outgoing[source]++;
            incoming[target]++;

            if (outgoing[source] > 1 || incoming[target] > 1) {
                throw new IllegalArgumentException(
                        "Branching and merging are not supported.");
            }

            nextNode[source] = target;
        }

        int startNode = -1;

        for (int i = 0; i < nodeCount; i++) {

            if (incoming[i] == 0) {

                if (startNode != -1) {
                    throw new IllegalArgumentException(
                            "Workflow must have exactly one starting node.");
                }

                startNode = i;
            }
        }

        if (startNode == -1) {
            throw new IllegalArgumentException(
                    "Workflow contains a cycle.");
        }

        boolean[] visited = new boolean[nodeCount];
        int current = startNode;
        int visitedCount = 0;

        while (current != -1) {

            if (visited[current]) {
                throw new IllegalArgumentException(
                        "Workflow contains a cycle.");
            }

            visited[current] = true;
            visitedCount++;

            current = nextNode[current];
        }

        if (visitedCount != nodeCount) {
            throw new IllegalArgumentException(
                    "Workflow contains disconnected nodes.");
        }
    }
}
