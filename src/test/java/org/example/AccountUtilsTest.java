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
    void totalBalanceWorksWithSavingsAccounts() {
        // Arrange
        List<SavingsAccount> accounts = new ArrayList<>();

        accounts.add(
                new SavingsAccount(
                        new AccountNumber("0000000001"),
                        "Ivan",
                        1500,
                        500
                )
        );

        accounts.add(
                new SavingsAccount(
                        new AccountNumber("0000000002"),
                        "Petr",
                        2500,
                        500
                )
        );

        // Act
        double total = AccountUtils.totalBalance(accounts);

        // Assert
        assertEquals(4000, total);
    }

    @Test
    void totalBalanceWorksWithCreditAccounts() {
        // Arrange
        List<CreditAccount> accounts = new ArrayList<>();

        accounts.add(
                new CreditAccount(
                        new AccountNumber("0000000001"),
                        "Ivan",
                        1000,
                        5000
                )
        );

        accounts.add(
                new CreditAccount(
                        new AccountNumber("0000000002"),
                        "Petr",
                        2000,
                        5000
                )
        );

        // Act
        double total = AccountUtils.totalBalance(accounts);

        // Assert
        assertEquals(3000, total);
    }
}