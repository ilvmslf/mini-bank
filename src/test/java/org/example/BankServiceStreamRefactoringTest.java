package org.example;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class BankServiceStreamRefactoringTest {

    @Test
    void findRichAccountsReturnsZeroMatches() {
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
                        10000
                )
        );

        bank.openAccount(
                new DebitAccount(
                        new AccountNumber("2222222222"),
                        "Petr",
                        50000
                )
        );

        // Act
        List<BankAccount> result =
                bank.findRichAccounts(100000);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void findRichAccountsReturnsOneMatch() {
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

        bank.openAccount(poor);
        bank.openAccount(rich);

        // Act
        List<BankAccount> result =
                bank.findRichAccounts(100000);

        // Assert
        assertEquals(1, result.size());
        assertSame(rich, result.get(0));
    }

    @Test
    void findRichAccountsReturnsSeveralMatches() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BankService bank =
                new BankService(
                        repository,
                        createTransferService()
                );

        BankAccount firstRich =
                new DebitAccount(
                        new AccountNumber("1111111111"),
                        "Ivan",
                        100000
                );

        BankAccount secondRich =
                new CreditAccount(
                        new AccountNumber("2222222222"),
                        "Petr",
                        250000,
                        50000
                );

        BankAccount poor =
                new DebitAccount(
                        new AccountNumber("3333333333"),
                        "Alex",
                        5000
                );

        bank.openAccount(firstRich);
        bank.openAccount(secondRich);
        bank.openAccount(poor);

        // Act
        List<BankAccount> result =
                bank.findRichAccounts(100000);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(firstRich));
        assertTrue(result.contains(secondRich));
        assertFalse(result.contains(poor));
    }

    @Test
    void getAccountSummariesConvertsTypeAndBalance() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BankService bank =
                new BankService(
                        repository,
                        createTransferService()
                );

        BankAccount account =
                new CreditAccount(
                        new AccountNumber("1111111111"),
                        "Ivan",
                        7500,
                        5000
                );

        bank.openAccount(account);

        // Act
        List<AccountSummary> summaries =
                bank.getAccountSummaries();

        // Assert
        assertEquals(1, summaries.size());

        AccountSummary summary =
                summaries.get(0);

        assertEquals(
                account.getId(),
                summary.number()
        );

        assertEquals(
                "CreditAccount",
                summary.type()
        );

        assertEquals(
                7500,
                summary.balance()
        );
    }

    @Test
    void richestFirstSortsWithoutChangingRepository() {
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
                        10000
                );

        BankAccount third =
                new DebitAccount(
                        new AccountNumber("3333333333"),
                        "Alex",
                        5000
                );

        bank.openAccount(first);
        bank.openAccount(second);
        bank.openAccount(third);

        List<BankAccount> before =
                repository.findAll();

        // Act
        List<BankAccount> sorted =
                bank.richestFirst();

        List<BankAccount> after =
                repository.findAll();

        // Assert
        assertSame(second, sorted.get(0));
        assertSame(third, sorted.get(1));
        assertSame(first, sorted.get(2));

        assertEquals(
                before,
                after
        );
    }

    @Test
    void hasBlockedAccountsReturnsTrue() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BlockedAccountRegistry blockedAccounts =
                new BlockedAccountRegistry();

        BankService bank =
                new BankService(
                        repository,
                        createTransferService(
                                blockedAccounts
                        )
                );

        BankAccount account =
                new DebitAccount(
                        new AccountNumber("1111111111"),
                        "Ivan",
                        1000
                );

        bank.openAccount(account);

        blockedAccounts.block(
                account.getId()
        );

        // Act
        boolean result =
                bank.hasBlockedAccounts();

        // Assert
        assertTrue(result);
    }

    @Test
    void hasBlockedAccountsReturnsFalse() {
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

        // Act
        boolean result =
                bank.hasBlockedAccounts();

        // Assert
        assertFalse(result);
    }

    @Test
    void anyMatchUsesShortCircuit() {
        // Arrange
        List<BankAccount> accounts =
                List.of(
                        new DebitAccount(
                                new AccountNumber("1111111111"),
                                "Ivan",
                                200000
                        ),
                        new DebitAccount(
                                new AccountNumber("2222222222"),
                                "Petr",
                                5000
                        ),
                        new DebitAccount(
                                new AccountNumber("3333333333"),
                                "Alex",
                                3000
                        )
                );

        AtomicInteger calls =
                new AtomicInteger();

        // Act
        boolean result =
                accounts.stream()
                        .anyMatch(account -> {
                            calls.incrementAndGet();

                            return account.getBalance()
                                    >= 100000;
                        });

        // Assert
        assertTrue(result);

        assertEquals(
                1,
                calls.get()
        );
    }

    @Test
    void findFirstRichAccountReturnsResult() {
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
                        5000
                );

        BankAccount rich =
                new DebitAccount(
                        new AccountNumber("2222222222"),
                        "Petr",
                        150000
                );

        bank.openAccount(poor);
        bank.openAccount(rich);

        // Act
        Optional<BankAccount> result =
                bank.findFirstRichAccount(100000);

        // Assert
        assertTrue(result.isPresent());
        assertSame(rich, result.get());
    }

    @Test
    void findFirstRichAccountReturnsEmpty() {
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
        Optional<BankAccount> result =
                bank.findFirstRichAccount(100000);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void totalBalanceReturnsZeroForEmptyBank() {
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
                0.0,
                result
        );
    }

    @Test
    void totalBalanceReturnsSumForNonEmptyBank() {
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
                new DebitAccount(
                        new AccountNumber("3333333333"),
                        "Alex",
                        3000
                )
        );

        // Act
        double result =
                bank.totalBalance();

        // Assert
        assertEquals(
                6000,
                result
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
    void richestAccountReturnsMaximumAccount() {
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
                        15000,
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

    private TransferService createTransferService() {
        return createTransferService(
                new BlockedAccountRegistry()
        );
    }

    private TransferService createTransferService(
            BlockedAccountRegistry blockedAccounts) {

        return new TransferService(
                amount -> 0.0,
                message -> {
                },
                blockedAccounts,
                new TransactionJournal()
        );
    }
}