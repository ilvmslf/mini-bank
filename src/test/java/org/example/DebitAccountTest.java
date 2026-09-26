package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DebitAccountTest {

    @Test
    void cannotWithdrawMoreThanBalance() {
        // Arrange
        DebitAccount account =
                new DebitAccount("1", "Ivan", 2000);

        // Act
        boolean result = account.withdraw(3000);

        // Assert
        assertFalse(result);
        assertEquals(2000, account.getBalance());
    }
}