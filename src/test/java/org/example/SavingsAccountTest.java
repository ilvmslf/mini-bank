package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SavingsAccountTest {

    @Test
    void canWithdrawIfMinimumBalanceRemains() {
        // Arrange
        SavingsAccount account = new SavingsAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                10000,
                1000
        );

        // Act
        boolean result = account.withdraw(8500);

        // Assert
        assertTrue(result);
        assertEquals(1500, account.getBalance());
    }

    @Test
    void cannotWithdrawBelowMinimumBalance() {
        // Arrange
        SavingsAccount account = new SavingsAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                1500,
                1000
        );

        // Act
        boolean result = account.withdraw(1000);

        // Assert
        assertFalse(result);
        assertEquals(1500, account.getBalance());
    }

    @Test
    void failedWithdrawDoesNotChangeBalance() {
        // Arrange
        SavingsAccount account = new SavingsAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                2000,
                1000
        );

        // Act
        boolean result = account.withdraw(1500);

        // Assert
        assertFalse(result);
        assertEquals(2000, account.getBalance());
    }
}