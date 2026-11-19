package org.example;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.OptionalDouble;

import static org.junit.jupiter.api.Assertions.*;

public class BankServiceNumericStreamTest {

    @Test
    void totalBalanceReturnsSumOfAllBalances() {
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
                        1000
                )
        );

        bank.openAccount(
                new DebitAccount(
                        new AccountNumber("2222222222"),
                        "Petr",
                        2000
                )
        );

        bank.openAccount(
                new CreditAccount(
                        new AccountNumber("3333333333"),
                        "Alex",
                        5000,
                        10000
                )
        );

        // Act
        double result =
                bank.totalBalance();

        // Assert
        assertEquals(
                8000,
                result
        );
    }

    @Test
    void totalBalanceOfEmptyBankIsZero() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BankService bank =
                new BankService(
                        repository,
                        createTransferService()
                );

        // Act
        double result =
                bank.totalBalance();

        // Assert
        assertEquals(
                0,
                result
        );
    }

    @Test
    void averageBalanceReturnsAverageOfAllBalances() {
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
                        1000
                )
        );

        bank.openAccount(
                new DebitAccount(
                        new AccountNumber("2222222222"),
                        "Petr",
                        3000
                )
        );

        bank.openAccount(
                new DebitAccount(
                        new AccountNumber("3333333333"),
                        "Alex",
                        5000
                )
        );

        // Act
        OptionalDouble result =
                bank.averageBalance();

        // Assert
        assertTrue(result.isPresent());

        assertEquals(
                3000,
                result.getAsDouble()
        );
    }

    @Test
    void averageBalanceReturnsEmptyForEmptyBank() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BankService bank =
                new BankService(
                        repository,
                        createTransferService()
                );

        // Act
        OptionalDouble result =
                bank.averageBalance();

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void richestAccountReturnsAccountWithMaximumBalance() {
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
                        1000
                );

        BankAccount richest =
                new CreditAccount(
                        new AccountNumber("2222222222"),
                        "Petr",
                        10000,
                        50000
                );

        BankAccount middle =
                new DebitAccount(
                        new AccountNumber("3333333333"),
                        "Alex",
                        5000
                );

        bank.openAccount(poor);
        bank.openAccount(richest);
        bank.openAccount(middle);

        // Act
        Optional<BankAccount> result =
                bank.richestAccount();

        // Assert
        assertTrue(result.isPresent());
        assertSame(richest, result.get());

        assertEquals(
                10000,
                result.get().getBalance()
        );
    }

    @Test
    void richestAccountReturnsEmptyForEmptyBank() {
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
                bank.richestAccount();

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void numericOperationsDoNotChangeRepository() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BankService bank =
                new BankService(
                        repository,
                        createTransferService()
                );

        BankAccount first =
                new DebitAccount(
                        new AccountNumber("1111111111"),
                        "Ivan",
                        1000
                );

        BankAccount second =
                new DebitAccount(
                        new AccountNumber("2222222222"),
                        "Petr",
                        3000
                );

        bank.openAccount(first);
        bank.openAccount(second);

        int sizeBefore =
                repository.size();

        // Act
        bank.totalBalance();
        bank.averageBalance();
        bank.richestAccount();

        // Assert
        assertEquals(
                sizeBefore,
                repository.size()
        );

        assertSame(
                first,
                repository.findById(first.getId())
        );

        assertSame(
                second,
                repository.findById(second.getId())
        );
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