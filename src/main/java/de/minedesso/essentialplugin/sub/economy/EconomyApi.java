package de.minedesso.essentialplugin.sub.economy;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.minedesso.essentialplugin.sub.economy.dto.in.balance.BalanceDto;
import de.minedesso.essentialplugin.sub.economy.dto.out.pay.PayDto;
import de.minedesso.essentialplugin.sub.economy.dto.out.pay.PayOfflineDto;
import de.minedesso.essentialplugin.sub.economy.dto.out.balance.SetMoneyFlowBalanceOfflineCommand;
import de.minedesso.essentialplugin.sub.economy.dto.out.balance.SetMoneyFlowBalanceOnlineCommand;
import de.minedesso.essentialplugin.sub.economy.dto.transaction.TransactionResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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

    public TransactionResponse pay(PayDto payDto) {
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
            return TransactionResponse.ERROR;
        } catch (Exception e) {
            return TransactionResponse.ERROR;
        }
    }

    public TransactionResponse payOfflinePlayer(PayOfflineDto payOfflineDto) {
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
            return TransactionResponse.ERROR;
        } catch (Exception e) {
            return TransactionResponse.ERROR;
        }
    }

    public BalanceDto getBalance(UUID ownerUuid) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "/money-flow/balance/" + ownerUuid))
                    .header(HEADER_ACCEPT, APPLICATION_JSON)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();

            if (status == 200) {
                return objectMapper.readValue(response.body(), BalanceDto.class);
            }
            return null;
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public TransactionResponse setBalanceOnline(SetMoneyFlowBalanceOnlineCommand command) {
        try {
            String json = objectMapper.writeValueAsString(command);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "/money-flow/balance"))
                    .header(HEADER_CONTENT_TYPE, APPLICATION_JSON)
                    .header(HEADER_ACCEPT, APPLICATION_JSON)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();

            return switch (status) {
                case 200, 201 -> TransactionResponse.SUCCESS;
                case 409 -> TransactionResponse.INSUFFICIENT_FUNDS;
                default -> TransactionResponse.ERROR;
            };
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            return TransactionResponse.ERROR;
        } catch (Exception e) {
            return TransactionResponse.ERROR;
        }
    }

    public TransactionResponse setBalanceOffline(SetMoneyFlowBalanceOfflineCommand command) {
        try {
            String json = objectMapper.writeValueAsString(command);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "/money-flow/balance/offline"))
                    .header(HEADER_CONTENT_TYPE, APPLICATION_JSON)
                    .header(HEADER_ACCEPT, APPLICATION_JSON)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();

            return switch (status) {
                case 200, 201 -> TransactionResponse.SUCCESS;
                case 404 -> TransactionResponse.RECEIVER_NOT_FOUND;
                case 409 -> TransactionResponse.INSUFFICIENT_FUNDS;
                default -> TransactionResponse.ERROR;
            };
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            return TransactionResponse.ERROR;
        } catch (Exception e) {
            return TransactionResponse.ERROR;
        }
    }

    private TransactionResponse getPayResponse(HttpRequest request) throws java.io.IOException, InterruptedException {
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();

        return switch (status) {
            case 200, 201 -> TransactionResponse.SUCCESS;
            case 409 -> TransactionResponse.INSUFFICIENT_FUNDS;
            case 404 -> TransactionResponse.RECEIVER_NOT_FOUND;
            default -> TransactionResponse.ERROR;
        };
    }
}
