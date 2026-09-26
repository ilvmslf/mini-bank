package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BankAccountTest {

    @Test
    void accountsWithSameNumberAreEqual() {
        // Arrange
        BankAccount account1 = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                10000
        );

        BankAccount account2 = new SavingsAccount(
                new AccountNumber("0000000001"),
                "Petr",
                5000,
                1000
        );

        // Act
        boolean result = account1.equals(account2);

        // Assert
        assertTrue(result);
    }

    @Test
    void accountsWithDifferentNumbersAreNotEqual() {
        // Arrange
        BankAccount account1 = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                10000
        );

        BankAccount account2 = new DebitAccount(
                new AccountNumber("0000000002"),
                "Ivan",
                10000
        );

        // Act
        boolean result = account1.equals(account2);

        // Assert
        assertFalse(result);
    }

    @Test
    void accountEqualsItself() {
        // Arrange
        BankAccount account = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                10000
        );

        // Act
        boolean result = account.equals(account);

        // Assert
        assertTrue(result);
    }

    @Test
    void accountDoesNotEqualNull() {
        // Arrange
        BankAccount account = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                10000
        );

        // Act
        boolean result = account.equals(null);

        // Assert
        assertFalse(result);
    }

    @Test
    void equalAccountsHaveSameHashCode() {
        // Arrange
        BankAccount account1 = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                10000
        );

        BankAccount account2 = new CreditAccount(
                new AccountNumber("0000000001"),
                "Petr",
                5000,
                10000
        );

        // Act
        int hash1 = account1.hashCode();
        int hash2 = account2.hashCode();

        // Assert
        assertEquals(hash1, hash2);
    }
}