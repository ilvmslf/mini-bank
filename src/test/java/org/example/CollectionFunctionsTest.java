package org.example;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

public class CollectionFunctionsTest {

    @Test
    void filterAccountsWithPositiveBalance() {
        // Arrange
        List<BankAccount> accounts =
                new ArrayList<>();

        BankAccount positive = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                5000
        );

        BankAccount zero = new DebitAccount(
                new AccountNumber("0000000002"),
                "Petr",
                0
        );

        CreditAccount negative = new CreditAccount(
                new AccountNumber("0000000003"),
                "Alex",
                1000,
                5000
        );

        negative.withdraw(2000);

        accounts.add(positive);
        accounts.add(zero);
        accounts.add(negative);

        Predicate<BankAccount> positiveBalance =
                account -> account.getBalance() > 0;

        // Act
        List<BankAccount> result =
                CollectionFunctions.filter(
                        accounts,
                        positiveBalance
                );

        // Assert
        assertEquals(1, result.size());
        assertSame(positive, result.get(0));
    }

    @Test
    void filterRichAccounts() {
        // Arrange
        List<BankAccount> accounts =
                new ArrayList<>();

        BankAccount first = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                50000
        );

        BankAccount second = new DebitAccount(
                new AccountNumber("0000000002"),
                "Petr",
                100000
        );

        BankAccount third = new DebitAccount(
                new AccountNumber("0000000003"),
                "Alex",
                250000
        );

        accounts.add(first);
        accounts.add(second);
        accounts.add(third);

        Predicate<BankAccount> rich =
                account -> account.getBalance() >= 100_000;

        // Act
        List<BankAccount> result =
                CollectionFunctions.filter(
                        accounts,
                        rich
                );

        // Assert
        assertEquals(2, result.size());
        assertSame(second, result.get(0));
        assertSame(third, result.get(1));
    }

    @Test
    void filterDebitAccountsOnly() {
        // Arrange
        List<BankAccount> accounts =
                new ArrayList<>();

        BankAccount debit = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                1000
        );

        BankAccount savings = new SavingsAccount(
                new AccountNumber("0000000002"),
                "Petr",
                2000,
                500
        );

        BankAccount credit = new CreditAccount(
                new AccountNumber("0000000003"),
                "Alex",
                3000,
                5000
        );

        accounts.add(debit);
        accounts.add(savings);
        accounts.add(credit);

        Predicate<BankAccount> debitOnly =
                DebitAccount.class::isInstance;

        // Act
        List<BankAccount> result =
                CollectionFunctions.filter(
                        accounts,
                        debitOnly
                );

        // Assert
        assertEquals(1, result.size());
        assertSame(debit, result.get(0));
    }

    @Test
    void filterReturnsNewList() {
        // Arrange
        List<BankAccount> source =
                new ArrayList<>();

        BankAccount account = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                1000
        );

        source.add(account);

        // Act
        List<BankAccount> result =
                CollectionFunctions.filter(
                        source,
                        value -> true
                );

        result.clear();

        // Assert
        assertEquals(1, source.size());
    }
}