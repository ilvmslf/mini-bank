package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TransferServiceTest {

    @Test
    void successfulTransferChangesBalances() {
        // Arrange
        BankAccount from =
                new DebitAccount("1", "Ivan", 10000);

        BankAccount to =
                new DebitAccount("2", "Petr", 2000);

        TransferService service =
                new TransferService(
                        new NoCommission(),
                        new ConsoleNotificationService()
                );

        // Act
        boolean result =
                service.transfer(from, to, 3000);

        // Assert
        assertTrue(result);
        assertEquals(7000, from.getBalance());
        assertEquals(5000, to.getBalance());
    }

    @Test
    void failedTransferDoesNotChangeBalances() {
        // Arrange
        BankAccount from =
                new DebitAccount("1", "Ivan", 1000);

        BankAccount to =
                new DebitAccount("2", "Petr", 2000);

        TransferService service =
                new TransferService(
                        new NoCommission(),
                        new ConsoleNotificationService()
                );

        // Act
        boolean result =
                service.transfer(from, to, 3000);

        // Assert
        assertFalse(result);
        assertEquals(1000, from.getBalance());
        assertEquals(2000, to.getBalance());
    }
}