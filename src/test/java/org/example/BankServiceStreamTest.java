package org.example;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BankServiceStreamTest {

    @Test
    void findRichAccountsReturnsAccountsWithEnoughBalance() {
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
                        100000
                );

        BankAccount veryRich =
                new CreditAccount(
                        new AccountNumber("3333333333"),
                        "Alex",
                        200000,
                        50000
                );

        bank.openAccount(poor);
        bank.openAccount(rich);
        bank.openAccount(veryRich);

        // Act
        List<BankAccount> result =
                bank.findRichAccounts(100000);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(rich));
        assertTrue(result.contains(veryRich));
        assertFalse(result.contains(poor));
    }

    @Test
    void findDebitAccountsReturnsOnlyDebitAccounts() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BankService bank =
                new BankService(
                        repository,
                        createTransferService()
                );

        BankAccount debit =
                new DebitAccount(
                        new AccountNumber("1111111111"),
                        "Ivan",
                        1000
                );

        BankAccount savings =
                new SavingsAccount(
                        new AccountNumber("2222222222"),
                        "Petr",
                        2000,
                        500
                );

        BankAccount credit =
                new CreditAccount(
                        new AccountNumber("3333333333"),
                        "Alex",
                        3000,
                        5000
                );

        bank.openAccount(debit);
        bank.openAccount(savings);
        bank.openAccount(credit);

        // Act
        List<BankAccount> result =
                bank.findDebitAccounts();

        // Assert
        assertEquals(1, result.size());
        assertSame(debit, result.get(0));
    }

    @Test
    void findAccountsByOwnerReturnsMatchingAccounts() {
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
                new CreditAccount(
                        new AccountNumber("2222222222"),
                        "Petr",
                        2000,
                        5000
                );

        BankAccount third =
                new SavingsAccount(
                        new AccountNumber("3333333333"),
                        "Ivan",
                        3000,
                        500
                );

        bank.openAccount(first);
        bank.openAccount(second);
        bank.openAccount(third);

        // Act
        List<BankAccount> result =
                bank.findAccountsByOwner("Ivan");

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(first));
        assertTrue(result.contains(third));
        assertFalse(result.contains(second));
    }

    @Test
    void emptyRepositoryReturnsEmptyLists() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BankService bank =
                new BankService(
                        repository,
                        createTransferService()
                );

        // Act
        List<BankAccount> rich =
                bank.findRichAccounts(100000);

        List<BankAccount> debit =
                bank.findDebitAccounts();

        List<BankAccount> byOwner =
                bank.findAccountsByOwner("Ivan");

        // Assert
        assertTrue(rich.isEmpty());
        assertTrue(debit.isEmpty());
        assertTrue(byOwner.isEmpty());
    }

    @Test
    void streamFilteringDoesNotChangeRepository() {
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
                        50000
                );

        BankAccount second =
                new DebitAccount(
                        new AccountNumber("2222222222"),
                        "Petr",
                        150000
                );

        bank.openAccount(first);
        bank.openAccount(second);

        int sizeBefore =
                repository.size();

        // Act
        List<BankAccount> result =
                bank.findRichAccounts(100000);

        // Assert
        assertEquals(1, result.size());
        assertEquals(sizeBefore, repository.size());

        assertSame(
                first,
                repository.findById(first.getId())
        );

        assertSame(
                second,
                repository.findById(second.getId())
        );
    }

    @Test
    void filterPreservesSourceOrder() {
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
                        200000
                );

        BankAccount second =
                new DebitAccount(
                        new AccountNumber("2222222222"),
                        "Petr",
                        1000
                );

        BankAccount third =
                new CreditAccount(
                        new AccountNumber("3333333333"),
                        "Alex",
                        300000,
                        50000
                );

        bank.openAccount(first);
        bank.openAccount(second);
        bank.openAccount(third);

        List<BankAccount> source =
                repository.findAll();

        List<BankAccount> expected =
                source.stream()
                        .filter(account ->
                                account.getBalance() >= 100000
                        )
                        .toList();

        // Act
        List<BankAccount> actual =
                bank.findRichAccounts(100000);

        // Assert
        assertEquals(expected, actual);
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