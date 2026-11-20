package bank;

import bank.adapters.JsonStorage;
import bank.application.dto.AccountDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JsonStorageReadTest {

    @TempDir
    Path tempDirectory;

    @Test
    void loadAccountsReadsJsonFile()
            throws IOException {

        // Arrange
        Path path =
                tempDirectory.resolve(
                        "accounts.json"
                );

        Files.writeString(
                path,
                """
                [
                  {
                    "number": "1111111111",
                    "owner": "Ivan",
                    "balance": 5000.0,
                    "type": "DebitAccount"
                  },
                  {
                    "number": "2222222222",
                    "owner": "Petr",
                    "balance": 10000.0,
                    "type": "CreditAccount"
                  }
                ]
                """,
                StandardCharsets.UTF_8
        );

        JsonStorage storage =
                new JsonStorage();

        // Act
        List<AccountDto> accounts =
                storage.loadAccounts(path);

        // Assert
        assertEquals(
                2,
                accounts.size()
        );
    }

    @Test
    void loadAccountsReadsFirstAccountCorrectly()
            throws IOException {

        // Arrange
        Path path =
                tempDirectory.resolve(
                        "accounts.json"
                );

        Files.writeString(
                path,
                """
                [
                  {
                    "number": "1111111111",
                    "owner": "Ivan",
                    "balance": 5000.0,
                    "type": "DebitAccount"
                  }
                ]
                """,
                StandardCharsets.UTF_8
        );

        JsonStorage storage =
                new JsonStorage();

        // Act
        List<AccountDto> accounts =
                storage.loadAccounts(path);

        AccountDto account =
                accounts.get(0);

        // Assert
        assertEquals(
                "1111111111",
                account.number()
        );

        assertEquals(
                "Ivan",
                account.owner()
        );

        assertEquals(
                5000.0,
                account.balance()
        );

        assertEquals(
                "DebitAccount",
                account.type()
        );
    }

    @Test
    void loadAccountsReadsEmptyJsonArray()
            throws IOException {

        // Arrange
        Path path =
                tempDirectory.resolve(
                        "accounts.json"
                );

        Files.writeString(
                path,
                "[]",
                StandardCharsets.UTF_8
        );

        JsonStorage storage =
                new JsonStorage();

        // Act
        List<AccountDto> accounts =
                storage.loadAccounts(path);

        // Assert
        assertTrue(
                accounts.isEmpty()
        );
    }

    @Test
    void savedAccountsCanBeLoadedAgain()
            throws IOException {

        // Arrange
        JsonStorage storage =
                new JsonStorage();

        Path path =
                tempDirectory.resolve(
                        "accounts.json"
                );

        List<AccountDto> original =
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
                original
        );

        List<AccountDto> loaded =
                storage.loadAccounts(
                        path
                );

        // Assert
        assertEquals(
                original,
                loaded
        );
    }

    @Test
    void loadAccountsRejectsNullPath() {
        // Arrange
        JsonStorage storage =
                new JsonStorage();

        // Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        storage.loadAccounts(
                                null
                        )
        );
    }
}