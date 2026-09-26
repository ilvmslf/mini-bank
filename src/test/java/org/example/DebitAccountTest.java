package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DebitAccountTest {

    @Test
    void initialBalanceIsSaved() {
        // Arrange
        DebitAccount account = new DebitAccount("1", "Ivan", 1000);

        // Act
        double balance = account.getBalance();

        // Assert
        assertEquals(1000, balance);
    }

    @Test
    void depositIncreasesBalance() {
        // Arrange
        DebitAccount account = new DebitAccount("1", "Ivan", 1000);

        // Act
        account.deposit(500);

        // Assert
        assertEquals(1500, account.getBalance());
    }

    @Test
    void zeroDepositDoesNotChangeBalance() {
        // Arrange
        DebitAccount account = new DebitAccount("1", "Ivan", 1000);

        // Act
        account.deposit(0);

        // Assert
        assertEquals(1000, account.getBalance());
    }

    @Test
    void negativeDepositDoesNotChangeBalance() {
        // Arrange
        DebitAccount account = new DebitAccount("1", "Ivan", 1000);

        // Act
        account.deposit(-500);

        // Assert
        assertEquals(1000, account.getBalance());
    }

    @Test
    void withdrawDecreasesBalance() {
        // Arrange
        DebitAccount account = new DebitAccount("1", "Ivan", 1000);

        // Act
        boolean result = account.withdraw(400);

        // Assert
        assertTrue(result);
        assertEquals(600, account.getBalance());
    }

    @Test
    void cannotWithdrawMoreThanBalance() {
        // Arrange
        DebitAccount account = new DebitAccount("1", "Ivan", 1000);

        // Act
        boolean result = account.withdraw(1500);

        // Assert
        assertFalse(result);
        assertEquals(1000, account.getBalance());
    }

    @Test
    void zeroWithdrawIsNotAllowed() {
        // Arrange
        DebitAccount account = new DebitAccount("1", "Ivan", 1000);

        // Act
        boolean result = account.withdraw(0);

        // Assert
        assertFalse(result);
        assertEquals(1000, account.getBalance());
    }

    @Test
    void negativeWithdrawIsNotAllowed() {
        // Arrange
        DebitAccount account = new DebitAccount("1", "Ivan", 1000);

        // Act
        boolean result = account.withdraw(-500);

        // Assert
        assertFalse(result);
        assertEquals(1000, account.getBalance());
    }
}