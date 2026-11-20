package bank;

import bank.adapters.JsonStorage;
import bank.application.dto.AccountDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JsonStorageRoundTripTest {

    @TempDir
    Path tempDirectory;

    @Test
    void roundTripRestoresEquivalentAccounts()
            throws IOException {

        // Arrange
        JsonStorage storage =
                new JsonStorage();

        Path path =
                tempDirectory.resolve(
                        "accounts.json"
                );

        List<AccountDto> source =
                List.of(
                        new AccountDto(
                                "1111111111",
                                "Ivan",
                                5000,
                                "DebitAccount"
                        ),
                        new AccountDto(
                                "2222222222",
                                "Petr",
                                10000,
                                "CreditAccount"
                        )
                );

        // Act
        storage.saveAccounts(
                path,
                source
        );

        List<AccountDto> restored =
                storage.loadAccounts(
                        path
                );

        // Assert
        assertEquals(
                source,
                restored
        );
    }

    @Test
    void roundTripWorksWithEmptyList()
            throws IOException {

        // Arrange
        JsonStorage storage =
                new JsonStorage();

        Path path =
                tempDirectory.resolve(
                        "empty-accounts.json"
                );

        List<AccountDto> source =
                List.of();

        // Act
        storage.saveAccounts(
                path,
                source
        );

        List<AccountDto> restored =
                storage.loadAccounts(
                        path
                );

        // Assert
        assertEquals(
                source,
                restored
        );

        assertTrue(
                restored.isEmpty()
        );
    }

    @Test
    void roundTripWorksWithDifferentAccountTypes()
            throws IOException {

        // Arrange
        JsonStorage storage =
                new JsonStorage();

        Path path =
                tempDirectory.resolve(
                        "different-accounts.json"
                );

        List<AccountDto> source =
                List.of(
                        new AccountDto(
                                "1111111111",
                                "Ivan",
                                1000,
                                "DebitAccount"
                        ),
                        new AccountDto(
                                "2222222222",
                                "Petr",
                                5000,
                                "SavingsAccount"
                        ),
                        new AccountDto(
                                "3333333333",
                                "Alex",
                                10000,
                                "CreditAccount"
                        )
                );

        // Act
        storage.saveAccounts(
                path,
                source
        );

        List<AccountDto> restored =
                storage.loadAccounts(
                        path
                );

        // Assert
        assertEquals(
                source,
                restored
        );

        assertEquals(
                3,
                restored.size()
        );

        assertEquals(
                "DebitAccount",
                restored.get(0).type()
        );

        assertEquals(
                "SavingsAccount",
                restored.get(1).type()
        );

        assertEquals(
                "CreditAccount",
                restored.get(2).type()
        );
    }

    @Test
    void loadAccountsRejectsInvalidJson()
            throws IOException {

        // Arrange
        JsonStorage storage =
                new JsonStorage();

        Path path =
                tempDirectory.resolve(
                        "invalid.json"
                );

        Files.writeString(
                path,
                """
                [
                  {
                    "number": "1111111111",
                    "owner": "Ivan",
                    "balance":
                }
                ]
                """,
                StandardCharsets.UTF_8
        );

        // Act + Assert
        assertThrows(
                JsonProcessingException.class,
                () ->
                        storage.loadAccounts(
                                path
                        )
        );
    }

    @Test
    void loadAccountsFailsWhenFileDoesNotExist() {

        // Arrange
        JsonStorage storage =
                new JsonStorage();

        Path path =
                tempDirectory.resolve(
                        "missing.json"
                );

        assertFalse(
                Files.exists(path)
        );

        // Act + Assert
        assertThrows(
                IOException.class,
                () ->
                        storage.loadAccounts(
                                path
                        )
        );
    }
}