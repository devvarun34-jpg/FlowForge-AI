
package com.flowforge.backend.ai.provider;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class GeminiProvider implements AIProvider {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;

    
public GeminiProvider(
        ObjectMapper objectMapper,
        @Value("${gemini.api.key}") String apiKey,
        @Value("${gemini.model}") String model) {

    this.objectMapper = objectMapper;

    String cleanApiKey = apiKey == null ? "" : apiKey.strip();

    if (cleanApiKey.isBlank()
            || cleanApiKey.contains("\r")
            || cleanApiKey.contains("\n")) {
        throw new IllegalArgumentException(
                "Gemini API key is missing or contains invalid whitespace."
        );
    }

    this.apiKey = cleanApiKey;
    this.model = model;

    this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();
}


    @Override
    public String generate(String prompt) {

        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException(
                    "Prompt cannot be empty."
            );
        }

        try {
            String endpoint =
                    "https://generativelanguage.googleapis.com/v1beta/models/"
                    + model + ":generateContent";

            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of(
                                    "parts", List.of(
                                            Map.of("text", prompt)
                                    )
                            )
                    )
            );

            String jsonBody =
                    objectMapper.writeValueAsString(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .timeout(Duration.ofSeconds(120))
                    .header("Content-Type", "application/json")
                    .header("x-goog-api-key", apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() < 200
                    || response.statusCode() >= 300) {

                throw new IllegalStateException(
                        "Gemini API request failed. HTTP status: "
                        + response.statusCode()
                        + ". Response: "
                        + response.body()
                );
            }

            JsonNode responseJson =
                    objectMapper.readTree(response.body());

            JsonNode textNode = responseJson
                    .path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text");

            if (textNode.isMissingNode() || textNode.isNull()) {
                throw new IllegalStateException(
                        "Gemini returned no text content."
                );
            }

            return textNode.asText();

        } catch (InterruptedException exception) {

            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "Gemini API request was interrupted.",
                    exception
            );

        } catch (IOException exception) {

            throw new IllegalStateException(
                    "Failed to communicate with Gemini API.",
                    exception
            );
        }
    }
}
