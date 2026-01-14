package de.minedesso.essentialplugin.sub.home;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.minedesso.essentialplugin.exception.CouldNotCreateException;
import de.minedesso.essentialplugin.exception.CouldNotDeleteException;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class HomeApi {

    private static final String HEADER_ACCEPT = "Accept";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String apiUrl;

    private static HomeApi instance;

    public static HomeApi getInstance() {
        if (instance == null) {
            instance = new HomeApi();
        }
        return instance;
    }

    private HomeApi() {
        String env = System.getenv("API_URL");
        this.apiUrl = env != null && !env.isBlank() ? env : "http://localhost:8080/api";
    }

    public void saveHome(HomeDto homeDto) {
        try {
            String json = objectMapper.writeValueAsString(homeDto);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "/home"))
                    .header(HEADER_CONTENT_TYPE, APPLICATION_JSON)
                    .header(HEADER_ACCEPT, APPLICATION_JSON)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();
            if (!(status == 200 || status == 201)) {
                throw new CouldNotCreateException("Failed to create home: HTTP " + status + " - " + response.body());
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new CouldNotCreateException("Thread interrupted while creating home");
        } catch (Exception e) {
            throw new CouldNotCreateException("Failed to create home: " + e.getMessage());
        }
    }

    public void deleteHome(HomeDto homeDto) {
        try {
            String owner = URLEncoder.encode(homeDto.getOwnerUuid().toString(), StandardCharsets.UTF_8);
            owner = owner.replace("+", "%20");
            String name = URLEncoder.encode(homeDto.getName(), StandardCharsets.UTF_8);
            name = name.replace("+", "%20");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "/home/" + owner + "/" + name))
                    .header(HEADER_ACCEPT, APPLICATION_JSON)
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();
            if (!(status == 200 || status == 204)) {
                throw new CouldNotDeleteException("Failed to delete home: HTTP " + status + " - " + response.body());
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new CouldNotDeleteException("Thread interrupted while deleting home");
        } catch (Exception e) {
            throw new CouldNotDeleteException("Failed to delete home: " + e.getMessage());
        }
    }

    public Optional<HomeDto> fetchHomeByNameAndOwnerUuid(String name, UUID ownerUuid) {
        try {
            String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8).replace("+", "%20");
            String owner = URLEncoder.encode(ownerUuid.toString(), StandardCharsets.UTF_8).replace("+", "%20");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "/home/" + owner + "/" + encodedName))
                    .GET()
                    .header(HEADER_ACCEPT, APPLICATION_JSON)
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return Optional.empty();
            }

            HomeDto dto = objectMapper.readValue(response.body(), HomeDto.class);
            return Optional.ofNullable(dto);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<HomeDto> fetchHomesByOwnerUuid(UUID ownerUuid) {
        try {
            String owner = URLEncoder.encode(ownerUuid.toString(), StandardCharsets.UTF_8).replace("+", "%20");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "/home/" + owner + "/all"))
                    .GET()
                    .header(HEADER_ACCEPT, APPLICATION_JSON)
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return Collections.emptyList();
            }

            return objectMapper.readValue(response.body(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, HomeDto.class));
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            return Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

}
