package org.example;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BankServiceSortingStreamTest {

    @Test
    void richestFirstSortsByBalanceDescending() {
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
                new DebitAccount(
                        new AccountNumber("2222222222"),
                        "Petr",
                        10000
                );

        BankAccount middle =
                new CreditAccount(
                        new AccountNumber("3333333333"),
                        "Alex",
                        5000,
                        10000
                );

        bank.openAccount(poor);
        bank.openAccount(richest);
        bank.openAccount(middle);

        // Act
        List<BankAccount> result =
                bank.richestFirst();

        // Assert
        assertEquals(3, result.size());

        assertSame(richest, result.get(0));
        assertSame(middle, result.get(1));
        assertSame(poor, result.get(2));
    }

    @Test
    void accountsByNumberSortsByAccountNumber() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BankService bank =
                new BankService(
                        repository,
                        createTransferService()
                );

        BankAccount third =
                new DebitAccount(
                        new AccountNumber("3333333333"),
                        "Ivan",
                        1000
                );

        BankAccount first =
                new DebitAccount(
                        new AccountNumber("1111111111"),
                        "Petr",
                        2000
                );

        BankAccount second =
                new DebitAccount(
                        new AccountNumber("2222222222"),
                        "Alex",
                        3000
                );

        bank.openAccount(third);
        bank.openAccount(first);
        bank.openAccount(second);

        // Act
        List<BankAccount> result =
                bank.accountsByNumber();

        // Assert
        assertEquals(3, result.size());

        assertEquals(
                "1111111111",
                result.get(0).getId().value()
        );

        assertEquals(
                "2222222222",
                result.get(1).getId().value()
        );

        assertEquals(
                "3333333333",
                result.get(2).getId().value()
        );
    }

    @Test
    void accountsByTypeThenBalanceSortsCorrectly() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BankService bank =
                new BankService(
                        repository,
                        createTransferService()
                );

        BankAccount debitHigh =
                new DebitAccount(
                        new AccountNumber("1111111111"),
                        "Ivan",
                        5000
                );

        BankAccount debitLow =
                new DebitAccount(
                        new AccountNumber("2222222222"),
                        "Petr",
                        1000
                );

        BankAccount credit =
                new CreditAccount(
                        new AccountNumber("3333333333"),
                        "Alex",
                        3000,
                        5000
                );

        BankAccount savings =
                new SavingsAccount(
                        new AccountNumber("4444444444"),
                        "Max",
                        2000,
                        500
                );

        bank.openAccount(debitHigh);
        bank.openAccount(debitLow);
        bank.openAccount(credit);
        bank.openAccount(savings);

        // Act
        List<BankAccount> result =
                bank.accountsByTypeThenBalance();

        // Assert
        assertEquals(4, result.size());

        assertSame(
                credit,
                result.get(0)
        );

        assertSame(
                debitLow,
                result.get(1)
        );

        assertSame(
                debitHigh,
                result.get(2)
        );

        assertSame(
                savings,
                result.get(3)
        );
    }

    @Test
    void accountsByOwnerThenNumberSortsCorrectly() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BankService bank =
                new BankService(
                        repository,
                        createTransferService()
                );

        BankAccount ivanSecond =
                new DebitAccount(
                        new AccountNumber("2222222222"),
                        "Ivan",
                        1000
                );

        BankAccount petr =
                new DebitAccount(
                        new AccountNumber("3333333333"),
                        "Petr",
                        2000
                );

        BankAccount ivanFirst =
                new DebitAccount(
                        new AccountNumber("1111111111"),
                        "Ivan",
                        3000
                );

        BankAccount alex =
                new DebitAccount(
                        new AccountNumber("4444444444"),
                        "Alex",
                        4000
                );

        bank.openAccount(ivanSecond);
        bank.openAccount(petr);
        bank.openAccount(ivanFirst);
        bank.openAccount(alex);

        // Act
        List<BankAccount> result =
                bank.accountsByOwnerThenNumber();

        // Assert
        assertEquals(4, result.size());

        assertSame(
                alex,
                result.get(0)
        );

        assertSame(
                ivanFirst,
                result.get(1)
        );

        assertSame(
                ivanSecond,
                result.get(2)
        );

        assertSame(
                petr,
                result.get(3)
        );
    }

    @Test
    void sortingDoesNotChangeRepositoryOrder() {
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
                        3000
                );

        BankAccount second =
                new DebitAccount(
                        new AccountNumber("2222222222"),
                        "Petr",
                        1000
                );

        BankAccount third =
                new DebitAccount(
                        new AccountNumber("3333333333"),
                        "Alex",
                        2000
                );

        bank.openAccount(first);
        bank.openAccount(second);
        bank.openAccount(third);

        List<BankAccount> beforeSorting =
                repository.findAll();

        // Act
        bank.richestFirst();

        List<BankAccount> afterSorting =
                repository.findAll();

        // Assert
        assertEquals(
                beforeSorting,
                afterSorting
        );

        assertEquals(
                3,
                repository.size()
        );
    }

    @Test
    void emptyRepositoryReturnsEmptySortedLists() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BankService bank =
                new BankService(
                        repository,
                        createTransferService()
                );

        // Act + Assert
        assertTrue(
                bank.richestFirst().isEmpty()
        );

        assertTrue(
                bank.accountsByNumber().isEmpty()
        );

        assertTrue(
                bank.accountsByTypeThenBalance().isEmpty()
        );

        assertTrue(
                bank.accountsByOwnerThenNumber().isEmpty()
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