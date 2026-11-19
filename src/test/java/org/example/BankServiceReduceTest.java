package org.example;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BankServiceReduceTest {

    @Test
    void reduceCalculatesTotalBalance() {
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
                bank.totalBalanceWithReduce();

        // Assert
        assertEquals(
                8000,
                result
        );
    }

    @Test
    void reduceAndManualLoopReturnSameResult() {
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
                        1500
                )
        );

        bank.openAccount(
                new DebitAccount(
                        new AccountNumber("2222222222"),
                        "Petr",
                        2500
                )
        );

        bank.openAccount(
                new DebitAccount(
                        new AccountNumber("3333333333"),
                        "Alex",
                        6000
                )
        );

        List<BankAccount> accounts =
                repository.findAll();

        double manualTotal = 0;

        for (BankAccount account : accounts) {
            manualTotal += account.getBalance();
        }

        // Act
        double reduceTotal =
                bank.totalBalanceWithReduce();

        // Assert
        assertEquals(
                manualTotal,
                reduceTotal
        );

        assertEquals(
                10000,
                reduceTotal
        );
    }

    @Test
    void reduceAndSumReturnSameResult() {
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
                        3000
                )
        );

        bank.openAccount(
                new DebitAccount(
                        new AccountNumber("2222222222"),
                        "Petr",
                        7000
                )
        );

        // Act
        double sumResult =
                bank.totalBalance();

        double reduceResult =
                bank.totalBalanceWithReduce();

        // Assert
        assertEquals(
                sumResult,
                reduceResult
        );
    }

    @Test
    void reduceReturnsZeroForEmptyRepository() {
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
                bank.totalBalanceWithReduce();

        // Assert
        assertEquals(
                0.0,
                result
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