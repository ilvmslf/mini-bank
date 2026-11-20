package bank;

import bank.adapters.ExternalRatesClient;
import bank.application.dto.RateDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class ExternalRatesClientJsonTest {

    @Test
    void getRateConvertsJsonResponseToDto()
            throws Exception {

        // Arrange
        HttpServer server =
                HttpServer.create(
                        new InetSocketAddress(0),
                        0
                );

        server.createContext(
                "/rate",
                exchange -> {

                    String body =
                            """
                            {
                              "currency": "USD",
                              "value": 92.5
                            }
                            """;

                    byte[] bytes =
                            body.getBytes(
                                    StandardCharsets.UTF_8
                            );

                    exchange.getResponseHeaders()
                            .add(
                                    "Content-Type",
                                    "application/json"
                            );

                    exchange.sendResponseHeaders(
                            200,
                            bytes.length
                    );

                    exchange.getResponseBody()
                            .write(bytes);

                    exchange.close();
                }
        );

        server.start();

        try {
            int port =
                    server.getAddress()
                            .getPort();

            String url =
                    "http://localhost:"
                            + port
                            + "/rate";

            ExternalRatesClient client =
                    new ExternalRatesClient();

            // Act
            RateDto rate =
                    client.getRate(url);

            // Assert
            assertEquals(
                    "USD",
                    rate.currency()
            );

            assertEquals(
                    92.5,
                    rate.value()
            );

        } finally {
            server.stop(0);
        }
    }

    @Test
    void getRateWorksWithAnotherCurrency()
            throws Exception {

        // Arrange
        HttpServer server =
                HttpServer.create(
                        new InetSocketAddress(0),
                        0
                );

        server.createContext(
                "/rate",
                exchange -> {

                    String body =
                            """
                            {
                              "currency": "EUR",
                              "value": 0.92
                            }
                            """;

                    byte[] bytes =
                            body.getBytes(
                                    StandardCharsets.UTF_8
                            );

                    exchange.sendResponseHeaders(
                            200,
                            bytes.length
                    );

                    exchange.getResponseBody()
                            .write(bytes);

                    exchange.close();
                }
        );

        server.start();

        try {
            int port =
                    server.getAddress()
                            .getPort();

            ExternalRatesClient client =
                    new ExternalRatesClient();

            // Act
            RateDto rate =
                    client.getRate(
                            "http://localhost:"
                                    + port
                                    + "/rate"
                    );

            // Assert
            assertEquals(
                    "EUR",
                    rate.currency()
            );

            assertEquals(
                    0.92,
                    rate.value()
            );

        } finally {
            server.stop(0);
        }
    }

    @Test
    void getRateRejectsInvalidJson()
            throws Exception {

        // Arrange
        HttpServer server =
                HttpServer.create(
                        new InetSocketAddress(0),
                        0
                );

        server.createContext(
                "/rate",
                exchange -> {

                    String body =
                            """
                            {
                              "currency": "USD",
                              "value":
                            }
                            """;

                    byte[] bytes =
                            body.getBytes(
                                    StandardCharsets.UTF_8
                            );

                    exchange.sendResponseHeaders(
                            200,
                            bytes.length
                    );

                    exchange.getResponseBody()
                            .write(bytes);

                    exchange.close();
                }
        );

        server.start();

        try {
            int port =
                    server.getAddress()
                            .getPort();

            ExternalRatesClient client =
                    new ExternalRatesClient();

            // Act + Assert
            assertThrows(
                    JsonProcessingException.class,
                    () ->
                            client.getRate(
                                    "http://localhost:"
                                            + port
                                            + "/rate"
                            )
            );

        } finally {
            server.stop(0);
        }
    }

    @Test
    void getRateChecksHttpStatusBeforeParsingJson()
            throws Exception {

        // Arrange
        HttpServer server =
                HttpServer.create(
                        new InetSocketAddress(0),
                        0
                );

        server.createContext(
                "/rate",
                exchange -> {

                    String body =
                            """
                            {
                              "error": "Not found"
                            }
                            """;

                    byte[] bytes =
                            body.getBytes(
                                    StandardCharsets.UTF_8
                            );

                    exchange.sendResponseHeaders(
                            404,
                            bytes.length
                    );

                    exchange.getResponseBody()
                            .write(bytes);

                    exchange.close();
                }
        );

        server.start();

        try {
            int port =
                    server.getAddress()
                            .getPort();

            ExternalRatesClient client =
                    new ExternalRatesClient();

            // Act
            IllegalStateException exception =
                    assertThrows(
                            IllegalStateException.class,
                            () ->
                                    client.getRate(
                                            "http://localhost:"
                                                    + port
                                                    + "/rate"
                                    )
                    );

            // Assert
            assertEquals(
                    "HTTP error: 404",
                    exception.getMessage()
            );

        } finally {
            server.stop(0);
        }
    }
}