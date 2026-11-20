package org.example;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class AdapterTest {

    @Test
    void inMemoryAdapterSavesAndFindsAccount() {
        // Arrange
        AccountRepository repository =
                new InMemoryAccountRepository();

        BankAccount account =
                new DebitAccount(
                        new AccountNumber("1111111111"),
                        "Ivan",
                        1000
                );

        // Act
        repository.save(account);

        // Assert
        assertTrue(
                repository
                        .findById(account.getId())
                        .isPresent()
        );

        assertSame(
                account,
                repository
                        .findById(account.getId())
                        .orElseThrow()
        );
    }

    @Test
    void inMemoryAdapterReturnsEmptyForUnknownAccount() {
        // Arrange
        AccountRepository repository =
                new InMemoryAccountRepository();

        // Act + Assert
        assertTrue(
                repository
                        .findById(
                                new AccountNumber(
                                        "1111111111"
                                )
                        )
                        .isEmpty()
        );
    }

    @Test
    void inMemoryAdapterDeletesAccount() {
        // Arrange
        AccountRepository repository =
                new InMemoryAccountRepository();

        BankAccount account =
                new DebitAccount(
                        new AccountNumber("1111111111"),
                        "Ivan",
                        1000
                );

        repository.save(account);

        // Act
        boolean deleted =
                repository.deleteById(
                        account.getId()
                );

        // Assert
        assertTrue(deleted);

        assertTrue(
                repository
                        .findById(account.getId())
                        .isEmpty()
        );
    }

    @Test
    void consoleNotificationAdapterImplementsNotificationPort() {
        // Arrange
        NotificationService adapter =
                new ConsoleNotificationAdapter();

        // Assert
        assertTrue(
                adapter instanceof
                        ConsoleNotificationAdapter
        );
    }

    @Test
    void consoleNotificationAdapterPrintsMessage() {
        // Arrange
        ByteArrayOutputStream output =
                new ByteArrayOutputStream();

        PrintStream oldOutput =
                System.out;

        NotificationService adapter =
                new ConsoleNotificationAdapter();

        try {
            System.setOut(
                    new PrintStream(output)
            );

            // Act
            adapter.notify(
                    "Transfer completed"
            );
        } finally {
            System.setOut(oldOutput);
        }

        // Assert
        assertEquals(
                "Transfer completed"
                        + System.lineSeparator(),
                output.toString()
        );
    }
}