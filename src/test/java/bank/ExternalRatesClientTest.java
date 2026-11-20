package bank;

import bank.adapters.ExternalRatesClient;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class ExternalRatesClientTest {

    @Test
    void getReturnsResponseBodyForSuccessfulRequest()
            throws Exception {

        // Arrange
        HttpServer server =
                HttpServer.create(
                        new InetSocketAddress(0),
                        0
                );

        server.createContext(
                "/rates",
                exchange -> {

                    String body =
                            """
                            {
                              "USD": 1.0,
                              "EUR": 0.9
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

            String url =
                    "http://localhost:"
                            + port
                            + "/rates";

            ExternalRatesClient client =
                    new ExternalRatesClient();

            // Act
            String result =
                    client.get(url);

            // Assert
            assertTrue(
                    result.contains(
                            "\"USD\""
                    )
            );

            assertTrue(
                    result.contains(
                            "\"EUR\""
                    )
            );

        } finally {
            server.stop(0);
        }
    }

    @Test
    void getAcceptsAny2xxStatus()
            throws Exception {

        // Arrange
        HttpServer server =
                HttpServer.create(
                        new InetSocketAddress(0),
                        0
                );

        server.createContext(
                "/rates",
                exchange -> {

                    String body =
                            "created";

                    byte[] bytes =
                            body.getBytes(
                                    StandardCharsets.UTF_8
                            );

                    exchange.sendResponseHeaders(
                            201,
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
                            + "/rates";

            ExternalRatesClient client =
                    new ExternalRatesClient();

            // Act
            String result =
                    client.get(url);

            // Assert
            assertEquals(
                    "created",
                    result
            );

        } finally {
            server.stop(0);
        }
    }

    @Test
    void getThrowsFor404Status()
            throws Exception {

        // Arrange
        HttpServer server =
                HttpServer.create(
                        new InetSocketAddress(0),
                        0
                );

        server.createContext(
                "/rates",
                exchange -> {

                    String body =
                            "Not found";

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

            String url =
                    "http://localhost:"
                            + port
                            + "/rates";

            ExternalRatesClient client =
                    new ExternalRatesClient();

            // Act
            IllegalStateException exception =
                    assertThrows(
                            IllegalStateException.class,
                            () ->
                                    client.get(url)
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

    @Test
    void getThrowsFor500Status()
            throws Exception {

        // Arrange
        HttpServer server =
                HttpServer.create(
                        new InetSocketAddress(0),
                        0
                );

        server.createContext(
                "/rates",
                exchange -> {

                    String body =
                            "Server error";

                    byte[] bytes =
                            body.getBytes(
                                    StandardCharsets.UTF_8
                            );

                    exchange.sendResponseHeaders(
                            500,
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
                            + "/rates";

            ExternalRatesClient client =
                    new ExternalRatesClient();

            // Act
            IllegalStateException exception =
                    assertThrows(
                            IllegalStateException.class,
                            () ->
                                    client.get(url)
                    );

            // Assert
            assertEquals(
                    "HTTP error: 500",
                    exception.getMessage()
            );

        } finally {
            server.stop(0);
        }
    }

    @Test
    void getRejectsEmptyUrl() {
        // Arrange
        ExternalRatesClient client =
                new ExternalRatesClient();

        // Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        client.get("")
        );
    }
}