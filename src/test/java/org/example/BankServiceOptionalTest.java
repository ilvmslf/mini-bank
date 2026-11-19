package org.example;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class BankServiceOptionalTest {

    @Test
    void findFirstRichAccountReturnsFirstMatchingAccount() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BankService bank =
                new BankService(
                        repository,
                        createTransferService()
                );

        BankAccount poor =
                new DebitAccount(
                        new AccountNumber("1111111111"),
                        "Ivan",
                        50000
                );

        BankAccount rich =
                new DebitAccount(
                        new AccountNumber("2222222222"),
                        "Petr",
                        150000
                );

        BankAccount richer =
                new CreditAccount(
                        new AccountNumber("3333333333"),
                        "Alex",
                        300000,
                        50000
                );

        bank.openAccount(poor);
        bank.openAccount(rich);
        bank.openAccount(richer);

        // Act
        Optional<BankAccount> result =
                bank.findFirstRichAccount(
                        100000
                );

        // Assert
        assertTrue(result.isPresent());
        assertSame(rich, result.get());
    }

    @Test
    void findFirstRichAccountReturnsEmptyWhenNothingMatches() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BankService bank =
                new BankService(
                        repository,
                        createTransferService()
                );

        bank.openAccount(
                new DebitAccount(
                        new AccountNumber("1111111111"),
                        "Ivan",
                        5000
                )
        );

        bank.openAccount(
                new DebitAccount(
                        new AccountNumber("2222222222"),
                        "Petr",
                        10000
                )
        );

        // Act
        Optional<BankAccount> result =
                bank.findFirstRichAccount(
                        100000
                );

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void orElseThrowReturnsRichAccount() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BankService bank =
                new BankService(
                        repository,
                        createTransferService()
                );

        BankAccount rich =
                new DebitAccount(
                        new AccountNumber("1111111111"),
                        "Ivan",
                        150000
                );

        bank.openAccount(rich);

        // Act
        BankAccount account =
                bank.findFirstRichAccount(100000)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "No rich account"
                                )
                        );

        // Assert
        assertSame(rich, account);
    }

    @Test
    void orElseThrowThrowsWhenRichAccountDoesNotExist() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BankService bank =
                new BankService(
                        repository,
                        createTransferService()
                );

        bank.openAccount(
                new DebitAccount(
                        new AccountNumber("1111111111"),
                        "Ivan",
                        5000
                )
        );

        // Act
        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> bank
                                .findFirstRichAccount(100000)
                                .orElseThrow(() ->
                                        new IllegalStateException(
                                                "No rich account"
                                        )
                                )
                );

        // Assert
        assertEquals(
                "No rich account",
                exception.getMessage()
        );
    }

    @Test
    void optionalMapCreatesAccountNumberLabel() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BankService bank =
                new BankService(
                        repository,
                        createTransferService()
                );

        bank.openAccount(
                new DebitAccount(
                        new AccountNumber("1111111111"),
                        "Ivan",
                        150000
                )
        );

        // Act
        String label =
                bank.findFirstRichAccount(100000)
                        .map(account ->
                                account.getId().value()
                        )
                        .orElse("not found");

        // Assert
        assertEquals(
                "1111111111",
                label
        );
    }

    @Test
    void optionalMapUsesDefaultWhenAccountDoesNotExist() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BankService bank =
                new BankService(
                        repository,
                        createTransferService()
                );

        bank.openAccount(
                new DebitAccount(
                        new AccountNumber("1111111111"),
                        "Ivan",
                        5000
                )
        );

        // Act
        String label =
                bank.findFirstRichAccount(100000)
                        .map(account ->
                                account.getId().value()
                        )
                        .orElse("not found");

        // Assert
        assertEquals(
                "not found",
                label
        );
    }

    @Test
    void emptyRepositoryReturnsEmptyOptional() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BankService bank =
                new BankService(
                        repository,
                        createTransferService()
                );

        // Act
        Optional<BankAccount> result =
                bank.findFirstRichAccount(
                        100000
                );

        // Assert
        assertTrue(result.isEmpty());
    }

    private TransferService createTransferService() {
        return new TransferService(
                amount -> 0.0,
                message -> {
                },
                new BlockedAccountRegistry(),
                new TransactionJournal()
        );
    }
}