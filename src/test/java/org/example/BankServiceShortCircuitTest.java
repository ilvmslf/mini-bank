package org.example;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class BankServiceShortCircuitTest {

    @Test
    void hasBlockedAccountsReturnsTrueWhenAccountIsBlocked() {
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
                        2000
                );

        bank.openAccount(first);
        bank.openAccount(second);

        blockedAccounts.block(
                second.getId()
        );

        // Act
        boolean result =
                bank.hasBlockedAccounts();

        // Assert
        assertTrue(result);
    }

    @Test
    void hasBlockedAccountsReturnsFalseWhenNothingIsBlocked() {
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

        // Act
        boolean result =
                bank.hasBlockedAccounts();

        // Assert
        assertFalse(result);
    }

    @Test
    void allAccountsPositiveReturnsTrueWhenAllBalancesArePositive() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BankService bank =
                new BankService(
                        repository,
                        createTransferService(
                                new BlockedAccountRegistry()
                        )
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

        // Act
        boolean result =
                bank.allAccountsPositive();

        // Assert
        assertTrue(result);
    }

    @Test
    void allAccountsPositiveReturnsFalseWhenAccountHasZeroBalance() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BankService bank =
                new BankService(
                        repository,
                        createTransferService(
                                new BlockedAccountRegistry()
                        )
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
                        0
                )
        );

        // Act
        boolean result =
                bank.allAccountsPositive();

        // Assert
        assertFalse(result);
    }

    @Test
    void noCreditAccountsReturnsTrueWhenThereAreNoCreditAccounts() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BankService bank =
                new BankService(
                        repository,
                        createTransferService(
                                new BlockedAccountRegistry()
                        )
                );

        bank.openAccount(
                new DebitAccount(
                        new AccountNumber("1111111111"),
                        "Ivan",
                        1000
                )
        );

        bank.openAccount(
                new SavingsAccount(
                        new AccountNumber("2222222222"),
                        "Petr",
                        2000,
                        500
                )
        );

        // Act
        boolean result =
                bank.noCreditAccounts();

        // Assert
        assertTrue(result);
    }

    @Test
    void noCreditAccountsReturnsFalseWhenCreditAccountExists() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BankService bank =
                new BankService(
                        repository,
                        createTransferService(
                                new BlockedAccountRegistry()
                        )
                );

        bank.openAccount(
                new DebitAccount(
                        new AccountNumber("1111111111"),
                        "Ivan",
                        1000
                )
        );

        bank.openAccount(
                new CreditAccount(
                        new AccountNumber("2222222222"),
                        "Petr",
                        2000,
                        5000
                )
        );

        // Act
        boolean result =
                bank.noCreditAccounts();

        // Assert
        assertFalse(result);
    }

    @Test
    void anyMatchStopsAfterFirstMatchingElement() {
        // Arrange
        List<BankAccount> accounts = List.of(
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

        AtomicInteger predicateCalls =
                new AtomicInteger();

        // Act
        boolean result =
                accounts.stream()
                        .anyMatch(account -> {
                            predicateCalls.incrementAndGet();

                            return account.getBalance()
                                    >= 100000;
                        });

        // Assert
        assertTrue(result);

        assertEquals(
                1,
                predicateCalls.get()
        );
    }

    @Test
    void emptyRepositoryUsesDefaultMatchResults() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BankService bank =
                new BankService(
                        repository,
                        createTransferService(
                                new BlockedAccountRegistry()
                        )
                );

        // Act + Assert
        assertFalse(
                bank.hasBlockedAccounts()
        );

        assertTrue(
                bank.allAccountsPositive()
        );

        assertTrue(
                bank.noCreditAccounts()
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