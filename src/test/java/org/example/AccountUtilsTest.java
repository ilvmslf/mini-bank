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
}