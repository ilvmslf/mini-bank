package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DebitAccountTest {

    @Test
    void initialBalanceIsSaved() {
        // Arrange
        DebitAccount account = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                1000
        );

        // Act
        double balance = account.getBalance();

        // Assert
        assertEquals(1000, balance);
    }

    @Test
    void depositIncreasesBalance() {
        // Arrange
        DebitAccount account = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                1000
        );

        // Act
        account.deposit(500);

        // Assert
        assertEquals(1500, account.getBalance());
    }

    @Test
    void zeroDepositThrowsException() {
        // Arrange
        DebitAccount account = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                1000
        );

        // Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> account.deposit(0)
        );
    }

    @Test
    void negativeDepositThrowsException() {
        // Arrange
        DebitAccount account = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                1000
        );

        // Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> account.deposit(-500)
        );
    }

    @Test
    void withdrawDecreasesBalance() {
        // Arrange
        DebitAccount account = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                1000
        );

        // Act
        boolean result = account.withdraw(400);

        // Assert
        assertTrue(result);
        assertEquals(600, account.getBalance());
    }

    @Test
    void cannotWithdrawMoreThanBalance() {
        // Arrange
        DebitAccount account = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                1000
        );

        // Act
        boolean result = account.withdraw(1500);

        // Assert
        assertFalse(result);
        assertEquals(1000, account.getBalance());
    }

    @Test
    void zeroWithdrawIsNotAllowed() {
        // Arrange
        DebitAccount account = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                1000
        );

        // Act
        boolean result = account.withdraw(0);

        // Assert
        assertFalse(result);
        assertEquals(1000, account.getBalance());
    }

    @Test
    void negativeWithdrawIsNotAllowed() {
        // Arrange
        DebitAccount account = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                1000
        );

        // Act
        boolean result = account.withdraw(-500);

        // Assert
        assertFalse(result);
        assertEquals(1000, account.getBalance());
    }
}