package org.example;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class CollectionFunctionsMapTest {

    @Test
    void mapAccountsToSummaries() {
        // Arrange
        List<BankAccount> accounts =
                new ArrayList<>();

        BankAccount first =
                new DebitAccount(
                        new AccountNumber("1111111111"),
                        "Ivan",
                        5000
                );

        BankAccount second =
                new CreditAccount(
                        new AccountNumber("2222222222"),
                        "Petr",
                        10000,
                        5000
                );

        accounts.add(first);
        accounts.add(second);

        Function<BankAccount, AccountSummary> toSummary =
                account -> new AccountSummary(
                        account.getId(),
                        account.getClass().getSimpleName(),
                        account.getBalance()
                );

        // Act
        List<AccountSummary> summaries =
                CollectionFunctions.map(
                        accounts,
                        toSummary
                );

        // Assert
        assertEquals(2, summaries.size());

        AccountSummary firstSummary =
                summaries.get(0);

        assertEquals(
                first.getId(),
                firstSummary.number()
        );

        assertEquals(
                "DebitAccount",
                firstSummary.type()
        );

        assertEquals(
                5000,
                firstSummary.balance()
        );

        AccountSummary secondSummary =
                summaries.get(1);

        assertEquals(
                second.getId(),
                secondSummary.number()
        );

        assertEquals(
                "CreditAccount",
                secondSummary.type()
        );

        assertEquals(
                10000,
                secondSummary.balance()
        );
    }

    @Test
    void mapAccountsToLabels() {
        // Arrange
        List<BankAccount> accounts =
                new ArrayList<>();

        BankAccount first =
                new DebitAccount(
                        new AccountNumber("1111111111"),
                        "Ivan",
                        5000
                );

        BankAccount second =
                new DebitAccount(
                        new AccountNumber("2222222222"),
                        "Petr",
                        2500
                );

        accounts.add(first);
        accounts.add(second);

        Function<BankAccount, String> label =
                account ->
                        account.getId().value()
                                + " : "
                                + account.getBalance();

        // Act
        List<String> labels =
                CollectionFunctions.map(
                        accounts,
                        label
                );

        // Assert
        assertEquals(2, labels.size());

        assertEquals(
                "1111111111 : 5000.0",
                labels.get(0)
        );

        assertEquals(
                "2222222222 : 2500.0",
                labels.get(1)
        );
    }

    @Test
    void mapPreservesOrder() {
        // Arrange
        List<BankAccount> accounts =
                new ArrayList<>();

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

        accounts.add(first);
        accounts.add(second);

        Function<BankAccount, AccountNumber> toNumber =
                BankAccount::getId;

        // Act
        List<AccountNumber> numbers =
                CollectionFunctions.map(
                        accounts,
                        toNumber
                );

        // Assert
        assertEquals(
                first.getId(),
                numbers.get(0)
        );

        assertEquals(
                second.getId(),
                numbers.get(1)
        );
    }

    @Test
    void mapReturnsNewList() {
        // Arrange
        List<BankAccount> accounts =
                new ArrayList<>();

        accounts.add(
                new DebitAccount(
                        new AccountNumber("1111111111"),
                        "Ivan",
                        1000
                )
        );

        // Act
        List<AccountSummary> summaries =
                CollectionFunctions.map(
                        accounts,
                        account ->
                                new AccountSummary(
                                        account.getId(),
                                        account.getClass()
                                                .getSimpleName(),
                                        account.getBalance()
                                )
                );

        summaries.clear();

        // Assert
        assertEquals(1, accounts.size());
    }

    @Test
    void forEachPerformsActionForEverySummary() {
        // Arrange
        List<AccountSummary> summaries =
                new ArrayList<>();

        AccountSummary first =
                new AccountSummary(
                        new AccountNumber("1111111111"),
                        "DebitAccount",
                        1000
                );

        AccountSummary second =
                new AccountSummary(
                        new AccountNumber("2222222222"),
                        "CreditAccount",
                        2000
                );

        summaries.add(first);
        summaries.add(second);

        List<AccountSummary> processed =
                new ArrayList<>();

        Consumer<AccountSummary> action =
                summary -> processed.add(summary);

        // Act
        CollectionFunctions.forEach(
                summaries,
                action
        );

        // Assert
        assertEquals(2, processed.size());
        assertSame(first, processed.get(0));
        assertSame(second, processed.get(1));
    }

    @Test
    void forEachWorksWithLambdaPrinter() {
        // Arrange
        List<AccountSummary> summaries =
                new ArrayList<>();

        summaries.add(
                new AccountSummary(
                        new AccountNumber("1111111111"),
                        "DebitAccount",
                        1000
                )
        );

        Consumer<AccountSummary> printer =
                summary -> System.out.println(summary);

        // Act + Assert
        assertDoesNotThrow(
                () -> CollectionFunctions.forEach(
                        summaries,
                        printer
                )
        );
    }

    @Test
    void forEachWorksWithMethodReference() {
        // Arrange
        List<AccountSummary> summaries =
                new ArrayList<>();

        summaries.add(
                new AccountSummary(
                        new AccountNumber("1111111111"),
                        "DebitAccount",
                        1000
                )
        );

        summaries.add(
                new AccountSummary(
                        new AccountNumber("2222222222"),
                        "CreditAccount",
                        2000
                )
        );

        // Act + Assert
        assertDoesNotThrow(
                () -> CollectionFunctions.forEach(
                        summaries,
                        System.out::println
                )
        );
    }
}