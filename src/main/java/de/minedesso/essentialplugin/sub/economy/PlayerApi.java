package de.minedesso.essentialplugin.sub.economy;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

public class PlayerApi {

    private static final String HEADER_ACCEPT = "Accept";
    private static final String APPLICATION_JSON = "application/json";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String apiUrl;

    private static PlayerApi instance;

    public static PlayerApi getInstance() {
        if (instance == null) {
            instance = new PlayerApi();
        }
        return instance;
    }

    private PlayerApi() {
        String env = System.getenv("API_URL");
        this.apiUrl = env != null && !env.isBlank() ? env : "http://localhost:8080/api";
    }

    public Optional<UUID> resolvePlayerUuid(String playerName) {
        try {
            String encoded = URLEncoder.encode(playerName, StandardCharsets.UTF_8);
            encoded = encoded.replace("+", "%20");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "/minecraft-player/" + encoded))
                    .GET()
                    .header(HEADER_ACCEPT, APPLICATION_JSON)
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return Optional.empty();
            }

            // Expecting a JSON response with UUID
            // Assuming response body is just the UUID string: "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
            String responseBody = response.body().trim();
            UUID uuid = UUID.fromString(responseBody);
            return Optional.of(uuid);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
