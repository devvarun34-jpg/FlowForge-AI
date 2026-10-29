
package com.flowforge.backend.execution.action;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class HttpAction implements WorkflowAction {

    private static final Set<String> ALLOWED_METHODS =
            Set.of("GET", "POST", "PUT", "DELETE");

    private final ObjectMapper objectMapper;

    public HttpAction(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String execute(String configuration) throws Exception {
        if (configuration == null || configuration.isBlank()) {
            throw new IllegalArgumentException(
                    "HTTP configuration cannot be empty");
        }

        JsonNode config = objectMapper.readTree(configuration);

        String url = config.path("url").asText("");
        String method = config.path("method").asText("GET")
                .toUpperCase(Locale.ROOT);

        if (url.isBlank()) {
            throw new IllegalArgumentException(
                    "HTTP configuration must contain a URL");
        }

        if (!ALLOWED_METHODS.contains(method)) {
            throw new IllegalArgumentException(
                    "Supported HTTP methods: GET, POST, PUT, DELETE");
        }

        URI uri;
        try {
            uri = URI.create(url);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid HTTP URL");
        }

        validatePublicDestination(uri);

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(5))
                .header("Accept", "application/json, text/plain, */*");

        if ("GET".equals(method) || "DELETE".equals(method)) {
            requestBuilder.method(method, HttpRequest.BodyPublishers.noBody());
        } else {
            String body = config.has("body")
                    ? config.get("body").toString()
                    : "";

            requestBuilder.header("Content-Type", "application/json");
            requestBuilder.method(
                    method,
                    HttpRequest.BodyPublishers.ofString(body));
        }

        HttpResponse<String> response = client.send(
                requestBuilder.build(),
                HttpResponse.BodyHandlers.ofString());

        String responseBody = response.body();

        // Keep the result manageable for the initial MVP.
        if (responseBody.length() > 4000) {
            responseBody = responseBody.substring(0, 4000)
                    + "... [response truncated]";
        }

        String result = "HTTP " + response.statusCode()
                + " from " + uri.getHost()
                + "\n" + responseBody;

        if (response.statusCode() >= 400) {
            throw new IOException(
                    "HTTP request failed with status "
                    + response.statusCode());
        }

        return result;
    }

    private void validatePublicDestination(URI uri)
            throws UnknownHostException {

        String scheme = uri.getScheme();

        if (scheme == null
                || !(scheme.equalsIgnoreCase("http")
                || scheme.equalsIgnoreCase("https"))) {
            throw new IllegalArgumentException(
                    "Only HTTP and HTTPS URLs are allowed");
        }

        if (uri.getHost() == null || uri.getUserInfo() != null) {
            throw new IllegalArgumentException(
                    "The URL must contain a valid public hostname");
        }

        if (uri.getPort() != -1
                && uri.getPort() != 80
                && uri.getPort() != 443) {
            throw new IllegalArgumentException(
                    "Only ports 80 and 443 are allowed");
        }

        InetAddress[] addresses = InetAddress.getAllByName(uri.getHost());

        if (addresses.length == 0) {
            throw new IllegalArgumentException(
                    "The hostname did not resolve to an address");
        }

        for (InetAddress address : addresses) {
            if (address.isAnyLocalAddress()
                    || address.isLoopbackAddress()
                    || address.isLinkLocalAddress()
                    || address.isSiteLocalAddress()
                    || address.isMulticastAddress()) {
                throw new IllegalArgumentException(
                        "Requests to private or local network addresses are blocked");
            }
        }
    }
}
