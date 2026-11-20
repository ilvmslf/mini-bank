package bank.adapters;

import bank.application.dto.RateDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ExternalRatesClient {

    private final HttpClient client =
            HttpClient.newHttpClient();

    private final ObjectMapper mapper =
            new ObjectMapper();

    public String get(String url)
            throws IOException,
            InterruptedException {

        HttpResponse<String> response =
                sendGet(url);

        return response.body();
    }

    public RateDto getRate(String url)
            throws IOException,
            InterruptedException {

        HttpResponse<String> response =
                sendGet(url);

        String body =
                response.body();

        return mapper.readValue(
                body,
                RateDto.class
        );
    }

    private HttpResponse<String> sendGet(
            String url)
            throws IOException,
            InterruptedException {

        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException(
                    "URL must not be empty"
            );
        }

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(url)
                        )
                        .GET()
                        .build();

        HttpResponse<String> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers
                                .ofString()
                );

        if (response.statusCode() / 100 != 2) {
            throw new IllegalStateException(
                    "HTTP error: "
                            + response.statusCode()
            );
        }

        return response;
    }
}