package de.minedesso.essentialplugin.sub.warp;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class WarpApi {

    private static final String HEADER_ACCEPT = "Accept";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String apiUrl;

    private static WarpApi instance;

    public static WarpApi getInstance() {
        if (instance == null) {
            instance = new WarpApi();
        }
        return instance;
    }

    private WarpApi() {
        String env = System.getenv("API_URL");
        this.apiUrl = env != null && !env.isBlank() ? env : "http://localhost:8080/api";
    }

    public List<WarpDto> fetchWarps() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "/warp/all"))
                    .GET()
                    .header(HEADER_ACCEPT, APPLICATION_JSON)
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return Collections.emptyList();
            }

            // JSON -> List<Warp>
            return objectMapper.readValue(response.body(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, WarpDto.class));
        } catch (InterruptedException ie) {
            // Restore interrupt status and propagate a neutral value
            Thread.currentThread().interrupt();
            return Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public WarpDto fetchWarpByName(String name) {
        try {
            String encoded = URLEncoder.encode(name, StandardCharsets.UTF_8);
            // URLEncoder encodes spaces as '+', replace with %20 for path segments
            encoded = encoded.replace("+", "%20");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "/warp/" + encoded))
                    .GET()
                    .header(HEADER_ACCEPT, APPLICATION_JSON)
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return null;
            }

            // JSON -> Warp
            return objectMapper.readValue(response.body(), WarpDto.class);
        } catch (InterruptedException ie) {
            // Restore interrupt status and propagate a neutral value
            Thread.currentThread().interrupt();
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean saveWarp(WarpDto warpDto) {
        try {
            String json = objectMapper.writeValueAsString(warpDto);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "/warp"))
                    .header(HEADER_CONTENT_TYPE, APPLICATION_JSON)
                    .header(HEADER_ACCEPT, APPLICATION_JSON)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();
            return status == 200 || status == 201;
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteWarp(String name) {
        try {
            String encoded = URLEncoder.encode(name, StandardCharsets.UTF_8);
            // URLEncoder encodes spaces as '+', replace with %20 for path segments
            encoded = encoded.replace("+", "%20");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "/warp/" + encoded))
                    .header(HEADER_ACCEPT, APPLICATION_JSON)
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();
            return status == 200 || status == 204;
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
