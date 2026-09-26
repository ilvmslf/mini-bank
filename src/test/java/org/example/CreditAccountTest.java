package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CreditAccountTest {

    @Test
    void canUseCreditLimit() {
        // Arrange
        CreditAccount account =
                new CreditAccount(
                        "1",
                        "Ivan",
                        1000,
                        5000
                );

        // Act
        boolean result = account.withdraw(4000);

        // Assert
        assertTrue(result);
        assertEquals(-3000, account.getBalance());
    }

    @Test
    void cannotExceedCreditLimit() {
        // Arrange
        CreditAccount account =
                new CreditAccount(
                        "1",
                        "Ivan",
                        1000,
                        5000
                );

        account.withdraw(4000);

        // Act
        boolean result = account.withdraw(3000);

        // Assert
        assertFalse(result);
        assertEquals(-3000, account.getBalance());
    }
}