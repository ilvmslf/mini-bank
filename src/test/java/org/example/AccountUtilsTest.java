package org.example;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AccountUtilsTest {

    @Test
    void totalBalanceWorksWithBankAccounts() {
        // Arrange
        List<BankAccount> accounts = new ArrayList<>();

        accounts.add(
                new DebitAccount(
                        new AccountNumber("0000000001"),
                        "Ivan",
                        1000
                )
        );

        accounts.add(
                new SavingsAccount(
                        new AccountNumber("0000000002"),
                        "Petr",
                        2000,
                        500
                )
        );

        // Act
        double total = AccountUtils.totalBalance(accounts);

        // Assert
        assertEquals(3000, total);
    }

    @Test
    void totalBalanceWorksWithDebitAccounts() {
        // Arrange
        List<DebitAccount> accounts = new ArrayList<>();

        accounts.add(
                new DebitAccount(
                        new AccountNumber("0000000001"),
                        "Ivan",
                        1000
                )
        );

        accounts.add(
                new DebitAccount(
                        new AccountNumber("0000000002"),
                        "Petr",
                        2000
                )
        );

        // Act
        double total = AccountUtils.totalBalance(accounts);

        // Assert
        assertEquals(3000, total);
    }

    @Test
    void addDemoDebitAccountsWorksWithDebitAccountList() {
        // Arrange
        List<DebitAccount> accounts = new ArrayList<>();

        // Act
        AccountUtils.addDemoDebitAccounts(accounts);

        // Assert
        assertEquals(2, accounts.size());
    }

    @Test
    void addDemoDebitAccountsWorksWithBankAccountList() {
        // Arrange
        List<BankAccount> accounts = new ArrayList<>();

        // Act
        AccountUtils.addDemoDebitAccounts(accounts);

        // Assert
        assertEquals(2, accounts.size());
        assertTrue(accounts.get(0) instanceof DebitAccount);
        assertTrue(accounts.get(1) instanceof DebitAccount);
    }

    @Test
    void addDemoDebitAccountsWorksWithObjectList() {
        // Arrange
        List<Object> values = new ArrayList<>();

        // Act
        AccountUtils.addDemoDebitAccounts(values);

        // Assert
        assertEquals(2, values.size());
        assertTrue(values.get(0) instanceof DebitAccount);
        assertTrue(values.get(1) instanceof DebitAccount);
    }

    @Test
    void copyDebitAccountsToBankAccounts() {
        // Arrange
        List<DebitAccount> source = new ArrayList<>();

        DebitAccount first = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                1000
        );

        DebitAccount second = new DebitAccount(
                new AccountNumber("0000000002"),
                "Petr",
                2000
        );

        source.add(first);
        source.add(second);

        List<BankAccount> target = new ArrayList<>();

        // Act
        AccountUtils.copy(source, target);

        // Assert
        assertEquals(2, target.size());
        assertSame(first, target.get(0));
        assertSame(second, target.get(1));
    }

    @Test
    void copyDebitAccountsToObjects() {
        // Arrange
        List<DebitAccount> source = new ArrayList<>();

        DebitAccount first = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                1000
        );

        DebitAccount second = new DebitAccount(
                new AccountNumber("0000000002"),
                "Petr",
                2000
        );

        source.add(first);
        source.add(second);

        List<Object> target = new ArrayList<>();

        // Act
        AccountUtils.copy(source, target);

        // Assert
        assertEquals(2, target.size());
        assertSame(first,
                target.get(0));
        assertSame(second, target.get(1));
    }

    @Test
    void copyBankAccountsToBankAccounts() {
        // Arrange
        List<BankAccount> source = new ArrayList<>();

        BankAccount first = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                1000
        );

        BankAccount second = new SavingsAccount(
                new AccountNumber("0000000002"),
                "Petr",
                2000,
                500
        );

        source.add(first);
        source.add(second);

        List<BankAccount> target = new ArrayList<>();

        // Act
        AccountUtils.copy(source, target);

        // Assert
        assertEquals(2, target.size());
        assertSame(first, target.get(0));
        assertSame(second, target.get(1));
    }

    @Test
    void copyPreservesElementOrder() {
        // Arrange
        List<DebitAccount> source = new ArrayList<>();

        DebitAccount first = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                1000
        );

        DebitAccount second = new DebitAccount(
                new AccountNumber("0000000002"),
                "Petr",
                2000
        );

        DebitAccount third = new DebitAccount(
                new AccountNumber("0000000003"),
                "Alex",
                3000
        );

        source.add(first);
        source.add(second);
        source.add(third);

        List<BankAccount> target = new ArrayList<>();

        // Act
        AccountUtils.copy(source, target);

        // Assert
        assertSame(first, target.get(0));
        assertSame(second, target.get(1));
        assertSame(third, target.get(2));
    }

    @Test
    void richestReturnsDebitAccountWithMaximumBalance() {
        // Arrange
        List<DebitAccount> accounts = new ArrayList<>();

        DebitAccount first = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                1000
        );

        DebitAccount second = new DebitAccount(
                new AccountNumber("0000000002"),
                "Petr",
                5000
        );

        DebitAccount third = new DebitAccount(
                new AccountNumber("0000000003"),
                "Alex",
                3000
        );

        accounts.add(first);
        accounts.add(second);
        accounts.add(third);

        // Act
        DebitAccount richest =
                AccountUtils.richest(accounts);

        // Assert
        assertSame(second, richest);
        assertEquals(5000, richest.getBalance());
    }

    @Test
    void richestReturnsNullForEmptyList() {
        // Arrange
        List<DebitAccount> accounts = new ArrayList<>();

        // Act
        DebitAccount richest =
                AccountUtils.richest(accounts);

        // Assert
        assertNull(richest);
    }

    @Test
    void richestReturnsOnlyElement() {
        // Arrange
        List<DebitAccount> accounts = new ArrayList<>();

        DebitAccount account = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                1000
        );

        accounts.add(account);

        // Act
        DebitAccount richest =
                AccountUtils.richest(accounts);

        // Assert
        assertSame(account, richest);
    }

    @Test
    void richestReturnsFirstAccountWhenBalancesAreEqual() {
        // Arrange
        List<DebitAccount> accounts = new ArrayList<>();

        DebitAccount first = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                5000
        );

        DebitAccount second = new DebitAccount(
                new AccountNumber("0000000002"),
                "Petr",
                5000
        );

        accounts.add(first);
        accounts.add(second);

        // Act
        DebitAccount richest =
                AccountUtils.richest(accounts);

        // Assert
        assertSame(first, richest);
    }
}