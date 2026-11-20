package bank;

import bank.adapters.JsonStorage;
import bank.application.dto.AccountDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JsonStorageTest {

    @TempDir
    Path tempDirectory;

    @Test
    void saveAccountsCreatesJsonFile()
            throws IOException {

        // Arrange
        JsonStorage storage =
                new JsonStorage();

        Path path =
                tempDirectory
                        .resolve("data")
                        .resolve("accounts.json");

        AccountDto account =
                new AccountDto(
                        "1111111111",
                        "Ivan",
                        5000,
                        "DebitAccount"
                );

        // Act
        storage.saveAccounts(
                path,
                List.of(account)
        );

        // Assert
        assertTrue(
                Files.exists(path)
        );

        assertTrue(
                Files.isRegularFile(path)
        );
    }

    @Test
    void saveAccountsCreatesMissingDirectory()
            throws IOException {

        // Arrange
        JsonStorage storage =
                new JsonStorage();

        Path directory =
                tempDirectory
                        .resolve("storage")
                        .resolve("json");

        Path path =
                directory.resolve(
                        "accounts.json"
                );

        AccountDto account =
                new AccountDto(
                        "1111111111",
                        "Ivan",
                        5000,
                        "DebitAccount"
                );

        assertFalse(
                Files.exists(directory)
        );

        // Act
        storage.saveAccounts(
                path,
                List.of(account)
        );

        // Assert
        assertTrue(
                Files.exists(directory)
        );

        assertTrue(
                Files.exists(path)
        );
    }

    @Test
    void saveAccountsWritesCorrectJson()
            throws IOException {

        // Arrange
        JsonStorage storage =
                new JsonStorage();

        ObjectMapper mapper =
                new ObjectMapper();

        Path path =
                tempDirectory.resolve(
                        "accounts.json"
                );

        AccountDto first =
                new AccountDto(
                        "1111111111",
                        "Ivan",
                        5000,
                        "DebitAccount"
                );

        AccountDto second =
                new AccountDto(
                        "2222222222",
                        "Petr",
                        10000,
                        "CreditAccount"
                );

        // Act
        storage.saveAccounts(
                path,
                List.of(
                        first,
                        second
                )
        );

        JsonNode root =
                mapper.readTree(
                        path.toFile()
                );

        // Assert
        assertTrue(
                root.isArray()
        );

        assertEquals(
                2,
                root.size()
        );

        assertEquals(
                "1111111111",
                root.get(0)
                        .get("number")
                        .asText()
        );

        assertEquals(
                "Ivan",
                root.get(0)
                        .get("owner")
                        .asText()
        );

        assertEquals(
                5000.0,
                root.get(0)
                        .get("balance")
                        .asDouble()
        );

        assertEquals(
                "DebitAccount",
                root.get(0)
                        .get("type")
                        .asText()
        );

        assertEquals(
                "2222222222",
                root.get(1)
                        .get("number")
                        .asText()
        );

        assertEquals(
                "CreditAccount",
                root.get(1)
                        .get("type")
                        .asText()
        );
    }

    @Test
    void saveAccountsWritesEmptyJsonArray()
            throws IOException {

        // Arrange
        JsonStorage storage =
                new JsonStorage();

        ObjectMapper mapper =
                new ObjectMapper();

        Path path =
                tempDirectory.resolve(
                        "accounts.json"
                );

        // Act
        storage.saveAccounts(
                path,
                List.of()
        );

        JsonNode root =
                mapper.readTree(
                        path.toFile()
                );

        // Assert
        assertTrue(
                root.isArray()
        );

        assertEquals(
                0,
                root.size()
        );
    }

    @Test
    void saveAccountsRejectsNullPath() {
        // Arrange
        JsonStorage storage =
                new JsonStorage();

        // Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        storage.saveAccounts(
                                null,
                                List.of()
                        )
        );
    }
}