package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SavingsAccountTest {

    @Test
    void canWithdrawIfMinimumBalanceRemains() {
        SavingsAccount account = new SavingsAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                10000,
                1000
        );

        account.withdraw(8500);

        assertEquals(1500, account.getBalance());
    }

    @Test
    void cannotWithdrawBelowMinimumBalance() {
        SavingsAccount account = new SavingsAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                1500,
                1000
        );

        assertThrows(
                InsufficientFundsException.class,
                () -> account.withdraw(1000)
        );
    }

    @Test
    void failedWithdrawDoesNotChangeBalance() {
        SavingsAccount account = new SavingsAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                2000,
                1000
        );

        assertThrows(
                InsufficientFundsException.class,
                () -> account.withdraw(1500)
        );

        assertEquals(2000, account.getBalance());
    }
}