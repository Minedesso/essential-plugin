package de.minedesso.essentialplugin.sub.economy;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.minedesso.essentialplugin.sub.economy.dto.PayDto;
import de.minedesso.essentialplugin.sub.economy.dto.PayOfflineDto;
import de.minedesso.essentialplugin.sub.economy.dto.PayResponse;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

public class EconomyApi {

    private static final String HEADER_ACCEPT = "Accept";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String apiUrl;

    private static EconomyApi instance;

    public static EconomyApi getInstance() {
        if (instance == null) {
            instance = new EconomyApi();
        }
        return instance;
    }

    private EconomyApi() {
        String env = System.getenv("API_URL");
        this.apiUrl = env != null && !env.isBlank() ? env : "http://localhost:8080/api";
    }

    public PayResponse pay(PayDto payDto) {
        try {
            String json = objectMapper.writeValueAsString(payDto);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "/pay"))
                    .header(HEADER_CONTENT_TYPE, APPLICATION_JSON)
                    .header(HEADER_ACCEPT, APPLICATION_JSON)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            return getPayResponse(request);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            return PayResponse.ERROR;
        } catch (Exception e) {
            return PayResponse.ERROR;
        }
    }

    public PayResponse payOfflinePlayer(PayOfflineDto payOfflineDto) {
        try {
            String json = objectMapper.writeValueAsString(payOfflineDto);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "/pay/offline"))
                    .header(HEADER_CONTENT_TYPE, APPLICATION_JSON)
                    .header(HEADER_ACCEPT, APPLICATION_JSON)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            return getPayResponse(request);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            return PayResponse.ERROR;
        } catch (Exception e) {
            return PayResponse.ERROR;
        }
    }

    private PayResponse getPayResponse(HttpRequest request) throws java.io.IOException, InterruptedException {
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();

        return switch (status) {
            case 200, 201 -> PayResponse.SUCCESS;
            case 409 -> PayResponse.INSUFFICIENT_FUNDS;
            case 404 -> PayResponse.RECEIVER_NOT_FOUND;
            default -> PayResponse.ERROR;
        };
    }
}
