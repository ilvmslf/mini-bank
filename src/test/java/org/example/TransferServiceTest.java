package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TransferServiceTest {

    @Test
    void successfulTransferChangesBothBalances() {
        // Arrange
        BankAccount from = new DebitAccount("1", "Ivan", 10000);
        BankAccount to = new DebitAccount("2", "Petr", 2000);

        TransferService service = new TransferService(
                new NoCommission(),
                new ConsoleNotificationService()
        );

        // Act
        boolean result = service.transfer(from, to, 3000);

        // Assert
        assertTrue(result);
        assertEquals(7000, from.getBalance());
        assertEquals(5000, to.getBalance());
    }

    @Test
    void failedTransferDoesNotChangeBalances() {
        // Arrange
        BankAccount from = new DebitAccount("1", "Ivan", 1000);
        BankAccount to = new DebitAccount("2", "Petr", 2000);

        TransferService service = new TransferService(
                new NoCommission(),
                new ConsoleNotificationService()
        );

        // Act
        boolean result = service.transfer(from, to, 3000);

        // Assert
        assertFalse(result);
        assertEquals(1000, from.getBalance());
        assertEquals(2000, to.getBalance());
    }

    @Test
    void cannotTransferNegativeAmount() {
        // Arrange
        BankAccount from = new DebitAccount("1", "Ivan", 1000);
        BankAccount to = new DebitAccount("2", "Petr", 1000);

        TransferService service = new TransferService(
                new NoCommission(),
                new ConsoleNotificationService()
        );

        // Act
        boolean result = service.transfer(from, to, -500);

        // Assert
        assertFalse(result);
        assertEquals(1000, from.getBalance());
        assertEquals(1000, to.getBalance());
    }

    @Test
    void cannotTransferZeroAmount() {
        // Arrange
        BankAccount from = new DebitAccount("1", "Ivan", 1000);
        BankAccount to = new DebitAccount("2", "Petr", 1000);

        TransferService service = new TransferService(
                new NoCommission(),
                new ConsoleNotificationService()
        );

        // Act
        boolean result = service.transfer(from, to, 0);

        // Assert
        assertFalse(result);
        assertEquals(1000, from.getBalance());
        assertEquals(1000, to.getBalance());
    }

    @Test
    void cannotTransferToSameAccount() {
        // Arrange
        BankAccount account = new DebitAccount("1", "Ivan", 1000);

        TransferService service = new TransferService(
                new NoCommission(),
                new ConsoleNotificationService()
        );

        // Act
        boolean result = service.transfer(account, account, 500);

        // Assert
        assertFalse(result);
        assertEquals(1000, account.getBalance());
    }

    @Test
    void commissionIsWithdrawnFromSender() {
        // Arrange
        BankAccount from = new DebitAccount("1", "Ivan", 20000);
        BankAccount to = new DebitAccount("2", "Petr", 0);

        TransferService service = new TransferService(
                new PercentCommission(1),
                new ConsoleNotificationService()
        );

        // Act
        boolean result = service.transfer(from, to, 10000);

        // Assert
        assertTrue(result);
        assertEquals(9900, from.getBalance());
    }

    @Test
    void receiverGetsExactTransferAmount() {
        // Arrange
        BankAccount from = new DebitAccount("1", "Ivan", 20000);
        BankAccount to = new DebitAccount("2", "Petr", 0);

        TransferService service = new TransferService(
                new PercentCommission(1),
                new ConsoleNotificationService()
        );

        // Act
        boolean result = service.transfer(from, to, 10000);

        // Assert
        assertTrue(result);
        assertEquals(10000, to.getBalance());
    }

    @Test
    void transferFailsIfBalanceIsNotEnoughWithCommission() {
        // Arrange
        BankAccount from = new DebitAccount("1", "Ivan", 10000);
        BankAccount to = new DebitAccount("2", "Petr", 2000);

        TransferService service = new TransferService(
                new PercentCommission(1),
                new ConsoleNotificationService()
        );

        // Act
        boolean result = service.transfer(from, to, 10000);

        // Assert
        assertFalse(result);
        assertEquals(10000, from.getBalance());
        assertEquals(2000, to.getBalance());
    }

    @Test
    void debitToDebitTransferWorks() {
        // Arrange
        BankAccount from = new DebitAccount("1", "Ivan", 5000);
        BankAccount to = new DebitAccount("2", "Petr", 1000);

        TransferService service = new TransferService(
                new NoCommission(),
                new ConsoleNotificationService()
        );

        // Act
        boolean result = service.transfer(from, to, 2000);

        // Assert
        assertTrue(result);
        assertEquals(3000, from.getBalance());
        assertEquals(3000, to.getBalance());
    }

    @Test
    void debitToSavingsTransferWorks() {
        // Arrange
        BankAccount from = new DebitAccount("1", "Ivan", 5000);
        BankAccount to = new SavingsAccount("2", "Petr", 2000, 1000);

        TransferService service = new TransferService(
                new NoCommission(),
                new ConsoleNotificationService()
        );

        // Act
        boolean result = service.transfer(from, to, 2000);

        // Assert
        assertTrue(result);
        assertEquals(3000, from.getBalance());
        assertEquals(4000, to.getBalance());
    }

    @Test
    void creditToDebitTransferWorks() {
        // Arrange
        BankAccount from = new CreditAccount("1", "Ivan", 1000, 5000);
        BankAccount to = new DebitAccount("2", "Petr", 1000);

        TransferService service = new TransferService(
                new NoCommission(),
                new ConsoleNotificationService()
        );

        // Act
        boolean result = service.transfer(from, to, 3000);

        // Assert
        assertTrue(result);
        assertEquals(-2000, from.getBalance());
        assertEquals(4000, to.getBalance());
    }

    @Test
    void savingsToDebitTransferWorks() {
        // Arrange
        BankAccount from = new SavingsAccount("1", "Ivan", 5000, 1000);
        BankAccount to = new DebitAccount("2", "Petr", 1000);

        TransferService service = new TransferService(
                new NoCommission(),
                new ConsoleNotificationService()
        );

        // Act
        boolean result = service.transfer(from, to, 3000);

        // Assert
        assertTrue(result);
        assertEquals(2000, from.getBalance());
        assertEquals(4000, to.getBalance());
    }

    @Test
    void successfulTransferSendsOneNotification() {
        // Arrange
        BankAccount from = new DebitAccount("1", "Ivan", 5000);
        BankAccount to = new DebitAccount("2", "Petr", 1000);

        FakeNotificationService notificationService =
                new FakeNotificationService();

        TransferService service = new TransferService(
                new NoCommission(),
                notificationService
        );

        // Act
        boolean result = service.transfer(from, to, 3000);

        // Assert
        assertTrue(result);
        assertEquals(1, notificationService.getNotificationCount());
    }

    @Test
    void failedTransferDoesNotSendNotification() {
        // Arrange
        BankAccount from = new DebitAccount("1", "Ivan", 1000);
        BankAccount to = new DebitAccount("2", "Petr", 1000);

        FakeNotificationService notificationService =
                new FakeNotificationService();

        TransferService service = new TransferService(
                new NoCommission(),
                notificationService
        );

        // Act
        boolean result = service.transfer(from, to, 3000);

        // Assert
        assertFalse(result);
        assertEquals(0, notificationService.getNotificationCount());
    }

    @Test
    void successfulTransferSendsCorrectMessage() {
        // Arrange
        BankAccount from = new DebitAccount("1", "Ivan", 5000);
        BankAccount to = new DebitAccount("2", "Petr", 1000);

        FakeNotificationService notificationService =
                new FakeNotificationService();

        TransferService service = new TransferService(
                new NoCommission(),
                notificationService
        );

        // Act
        boolean result = service.transfer(from, to, 3000);

        // Assert
        assertTrue(result);

        assertEquals(
                "Transfer 3000.0 completed",
                notificationService.getLastMessage()
        );
    }
}