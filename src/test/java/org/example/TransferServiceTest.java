package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TransferServiceTest {

    @Test
    void successfulTransferChangesBothBalances() {
        // Arrange
        BankAccount from = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                10000
        );

        BankAccount to = new DebitAccount(
                new AccountNumber("0000000002"),
                "Petr",
                2000
        );

        TransferService service = new TransferService(
                new NoCommission(),
                new ConsoleNotificationService()
        );

        // Act
        service.transfer(from, to, 3000);

        // Assert
        assertEquals(7000, from.getBalance());
        assertEquals(5000, to.getBalance());
    }

    @Test
    void insufficientFundsThrowsExceptionAndDoesNotChangeReceiver() {
        // Arrange
        BankAccount from = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                1000
        );

        BankAccount to = new DebitAccount(
                new AccountNumber("0000000002"),
                "Petr",
                2000
        );

        TransferService service = new TransferService(
                new NoCommission(),
                new ConsoleNotificationService()
        );

        // Act + Assert
        assertThrows(
                InsufficientFundsException.class,
                () -> service.transfer(from, to, 3000)
        );

        assertEquals(1000, from.getBalance());
        assertEquals(2000, to.getBalance());
    }

    @Test
    void negativeAmountThrowsException() {
        // Arrange
        BankAccount from = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                1000
        );

        BankAccount to = new DebitAccount(
                new AccountNumber("0000000002"),
                "Petr",
                1000
        );

        TransferService service = new TransferService(
                new NoCommission(),
                new ConsoleNotificationService()
        );

        // Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> service.transfer(from, to, -500)
        );

        assertEquals(1000, from.getBalance());
        assertEquals(1000, to.getBalance());
    }

    @Test
    void zeroAmountThrowsException() {
        // Arrange
        BankAccount from = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                1000
        );

        BankAccount to = new DebitAccount(
                new AccountNumber("0000000002"),
                "Petr",
                1000
        );

        TransferService service = new TransferService(
                new NoCommission(),
                new ConsoleNotificationService()
        );

        // Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> service.transfer(from, to, 0)
        );

        assertEquals(1000, from.getBalance());
        assertEquals(1000, to.getBalance());
    }

    @Test
    void transferToSameAccountThrowsException() {
        // Arrange
        BankAccount account = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                1000
        );

        TransferService service = new TransferService(
                new NoCommission(),
                new ConsoleNotificationService()
        );

        // Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> service.transfer(account, account, 500)
        );

        assertEquals(1000, account.getBalance());
    }

    @Test
    void transferAboveLimitThrowsException() {
        // Arrange
        BankAccount from = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                100000
        );

        BankAccount to = new DebitAccount(
                new AccountNumber("0000000002"),
                "Petr",
                1000
        );

        TransferService service = new TransferService(
                new NoCommission(),
                new ConsoleNotificationService()
        );

        // Act + Assert
        assertThrows(
                TransferLimitExceededException.class,
                () -> service.transfer(from, to, 50001)
        );

        assertEquals(100000, from.getBalance());
        assertEquals(1000, to.getBalance());
    }

    @Test
    void commissionIsWithdrawnFromSender() {
        // Arrange
        BankAccount from = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                20000
        );

        BankAccount to = new DebitAccount(
                new AccountNumber("0000000002"),
                "Petr",
                0
        );

        TransferService service = new TransferService(
                new PercentCommission(1),
                new ConsoleNotificationService()
        );

        // Act
        service.transfer(from, to, 10000);

        // Assert
        assertEquals(9900, from.getBalance());
    }

    @Test
    void receiverGetsExactTransferAmount() {
        // Arrange
        BankAccount from = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                20000
        );

        BankAccount to = new DebitAccount(
                new AccountNumber("0000000002"),
                "Petr",
                0
        );

        TransferService service = new TransferService(
                new PercentCommission(1),
                new ConsoleNotificationService()
        );

        // Act
        service.transfer(from, to, 10000);

        // Assert
        assertEquals(10000, to.getBalance());
    }

    @Test
    void transferFailsIfBalanceIsNotEnoughWithCommission() {
        // Arrange
        BankAccount from = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                10000
        );

        BankAccount to = new DebitAccount(
                new AccountNumber("0000000002"),
                "Petr",
                2000
        );

        TransferService service = new TransferService(
                new PercentCommission(1),
                new ConsoleNotificationService()
        );

        // Act + Assert
        assertThrows(
                InsufficientFundsException.class,
                () -> service.transfer(from, to, 10000)
        );

        assertEquals(10000, from.getBalance());
        assertEquals(2000, to.getBalance());
    }

    @Test
    void debitToDebitTransferWorks() {
        // Arrange
        BankAccount from = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                5000
        );

        BankAccount to = new DebitAccount(
                new AccountNumber("0000000002"),
                "Petr",
                1000
        );

        TransferService service = new TransferService(
                new NoCommission(),
                new ConsoleNotificationService()
        );

        // Act
        service.transfer(from, to, 2000);

        // Assert
        assertEquals(3000, from.getBalance());
        assertEquals(3000, to.getBalance());
    }

    @Test
    void debitToSavingsTransferWorks() {
        // Arrange
        BankAccount from = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                5000
        );

        BankAccount to = new SavingsAccount(
                new AccountNumber("0000000002"),
                "Petr",
                2000,
                1000
        );

        TransferService service = new TransferService(
                new NoCommission(),
                new ConsoleNotificationService()
        );

        // Act
        service.transfer(from, to, 2000);

        // Assert
        assertEquals(3000, from.getBalance());
        assertEquals(4000, to.getBalance());
    }

    @Test
    void creditToDebitTransferWorks() {
        // Arrange
        BankAccount from = new CreditAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                1000,
                5000
        );

        BankAccount to = new DebitAccount(
                new AccountNumber("0000000002"),
                "Petr",
                1000
        );

        TransferService service = new TransferService(
                new NoCommission(),
                new ConsoleNotificationService()
        );

        // Act
        service.transfer(from, to, 3000);

        // Assert
        assertEquals(-2000, from.getBalance());
        assertEquals(4000, to.getBalance());
    }

    @Test
    void savingsToDebitTransferWorks() {
        // Arrange
        BankAccount from = new SavingsAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                5000,
                1000
        );

        BankAccount to = new DebitAccount(
                new AccountNumber("0000000002"),
                "Petr",
                1000
        );

        TransferService service = new TransferService(
                new NoCommission(),
                new ConsoleNotificationService()
        );

        // Act
        service.transfer(from, to, 3000);

        // Assert
        assertEquals(2000, from.getBalance());
        assertEquals(4000, to.getBalance());
    }

    @Test
    void successfulTransferSendsOneNotification() {
        // Arrange
        BankAccount from = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                5000
        );

        BankAccount to = new DebitAccount(
                new AccountNumber("0000000002"),
                "Petr",
                1000
        );

        FakeNotificationService notificationService =
                new FakeNotificationService();

        TransferService service = new TransferService(
                new NoCommission(),
                notificationService
        );

        // Act
        service.transfer(from, to, 3000);

        // Assert
        assertEquals(
                1,
                notificationService.getNotificationCount()
        );
    }

    @Test
    void failedTransferDoesNotSendNotification() {
        // Arrange
        BankAccount from = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                1000
        );

        BankAccount to = new DebitAccount(
                new AccountNumber("0000000002"),
                "Petr",
                1000
        );

        FakeNotificationService notificationService =
                new FakeNotificationService();

        TransferService service = new TransferService(
                new NoCommission(),
                notificationService
        );

        // Act + Assert
        assertThrows(
                InsufficientFundsException.class,
                () -> service.transfer(from, to, 3000)
        );

        assertEquals(
                0,
                notificationService.getNotificationCount()
        );
    }

    @Test
    void successfulTransferSendsCorrectMessage() {
        // Arrange
        BankAccount from = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                5000
        );

        BankAccount to = new DebitAccount(
                new AccountNumber("0000000002"),
                "Petr",
                1000
        );

        FakeNotificationService notificationService =
                new FakeNotificationService();

        TransferService service = new TransferService(
                new NoCommission(),
                notificationService
        );

        // Act
        service.transfer(from, to, 3000);

        // Assert
        assertEquals(
                "Transfer 3000.0 completed",
                notificationService.getLastMessage()
        );
    }
}