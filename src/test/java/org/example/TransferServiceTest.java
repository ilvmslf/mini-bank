package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TransferServiceTest {

    @Test
    void successfulTransferChangesBothBalances() {
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
                new ConsoleNotificationService(),
                new BlockedAccountRegistry()
        );

        service.transfer(from, to, 3000);

        assertEquals(7000, from.getBalance());
        assertEquals(5000, to.getBalance());
    }

    @Test
    void negativeTransferThrowsException() {
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
                new ConsoleNotificationService(),
                new BlockedAccountRegistry()
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.transfer(from, to, -100)
        );

        assertEquals(
                "Amount must be positive",
                ex.getMessage()
        );

        assertEquals(5000, from.getBalance());
        assertEquals(1000, to.getBalance());
    }

    @Test
    void zeroTransferThrowsException() {
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
                new ConsoleNotificationService(),
                new BlockedAccountRegistry()
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.transfer(from, to, 0)
        );

        assertEquals(5000, from.getBalance());
        assertEquals(1000, to.getBalance());
    }

    @Test
    void transferToSameAccountThrowsException() {
        BankAccount account = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                5000
        );

        TransferService service = new TransferService(
                new NoCommission(),
                new ConsoleNotificationService(),
                new BlockedAccountRegistry()
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.transfer(
                        account,
                        account,
                        1000
                )
        );

        assertEquals(5000, account.getBalance());
    }

    @Test
    void transferWithoutEnoughMoneyThrowsException() {
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
                new ConsoleNotificationService(),
                new BlockedAccountRegistry()
        );

        InsufficientFundsException ex = assertThrows(
                InsufficientFundsException.class,
                () -> service.transfer(from, to, 5000)
        );

        assertEquals(
                "Insufficient funds",
                ex.getMessage()
        );
    }

    @Test
    void transferOverLimitThrowsException() {
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
                new ConsoleNotificationService(),
                new BlockedAccountRegistry()
        );

        assertThrows(
                TransferLimitExceededException.class,
                () -> service.transfer(from, to, 50001)
        );

        assertEquals(100000, from.getBalance());
        assertEquals(1000, to.getBalance());
    }

    @Test
    void failedTransferDoesNotChangeBalances() {
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
                new ConsoleNotificationService(),
                new BlockedAccountRegistry()
        );

        assertThrows(
                InsufficientFundsException.class,
                () -> service.transfer(from, to, 5000)
        );

        assertEquals(1000, from.getBalance());
        assertEquals(2000, to.getBalance());
    }

    @Test
    void commissionIsWithdrawnFromSender() {
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
                new ConsoleNotificationService(),
                new BlockedAccountRegistry()
        );

        service.transfer(from, to, 10000);

        assertEquals(9900, from.getBalance());
        assertEquals(10000, to.getBalance());
    }

    @Test
    void debitToDebitTransferWorks() {
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
                new ConsoleNotificationService(),
                new BlockedAccountRegistry()
        );

        service.transfer(from, to, 2000);

        assertEquals(3000, from.getBalance());
        assertEquals(3000, to.getBalance());
    }

    @Test
    void debitToSavingsTransferWorks() {
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
                new ConsoleNotificationService(),
                new BlockedAccountRegistry()
        );

        service.transfer(from, to, 2000);

        assertEquals(3000, from.getBalance());
        assertEquals(4000, to.getBalance());
    }

    @Test
    void creditToDebitTransferWorks() {
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
                new ConsoleNotificationService(),
                new BlockedAccountRegistry()
        );

        service.transfer(from, to, 3000);

        assertEquals(-2000, from.getBalance());
        assertEquals(4000, to.getBalance());
    }

    @Test
    void savingsToDebitTransferWorks() {
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
                new ConsoleNotificationService(),
                new BlockedAccountRegistry()
        );

        service.transfer(from, to, 3000);

        assertEquals(2000, from.getBalance());
        assertEquals(4000, to.getBalance());
    }

    @Test
    void successfulTransferSendsOneNotification() {
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
                notificationService,
                new BlockedAccountRegistry()
        );

        service.transfer(from, to, 3000);

        assertEquals(
                1,
                notificationService.getNotificationCount()
        );
    }

    @Test
    void failedTransferDoesNotSendNotification() {
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
                notificationService,
                new BlockedAccountRegistry()
        );

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
                notificationService,
                new BlockedAccountRegistry()
        );

        service.transfer(from, to, 3000);

        assertEquals(
                "Transfer 3000.0 completed",
                notificationService.getLastMessage()
        );
    }

    @Test
    void transferFromBlockedAccountThrowsException() {
        BankAccount from = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                5000
        );

        BankAccount to = new DebitAccount(
                new AccountNumber("0000000002"),
                "Petr",
                2000
        );

        BlockedAccountRegistry blockedAccounts =
                new BlockedAccountRegistry();

        blockedAccounts.block(from.getId());

        FakeNotificationService notificationService =
                new FakeNotificationService();

        TransferService service = new TransferService(
                new NoCommission(),
                notificationService,
                blockedAccounts
        );

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.transfer(from, to, 1000)
        );

        assertEquals(
                "Source account is blocked",
                ex.getMessage()
        );
    }

    @Test
    void blockedTransferDoesNotChangeBalances() {
        BankAccount from = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                5000
        );

        BankAccount to = new DebitAccount(
                new AccountNumber("0000000002"),
                "Petr",
                2000
        );

        BlockedAccountRegistry blockedAccounts =
                new BlockedAccountRegistry();

        blockedAccounts.block(from.getId());

        TransferService service = new TransferService(
                new NoCommission(),
                new ConsoleNotificationService(),
                blockedAccounts
        );

        assertThrows(
                IllegalStateException.class,
                () -> service.transfer(from, to, 1000)
        );

        assertEquals(5000, from.getBalance());
        assertEquals(2000, to.getBalance());
    }

    @Test
    void blockedTransferDoesNotSendNotification() {
        BankAccount from = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                5000
        );

        BankAccount to = new DebitAccount(
                new AccountNumber("0000000002"),
                "Petr",
                2000
        );

        BlockedAccountRegistry blockedAccounts =
                new BlockedAccountRegistry();

        blockedAccounts.block(from.getId());

        FakeNotificationService notificationService =
                new FakeNotificationService();

        TransferService service = new TransferService(
                new NoCommission(),
                notificationService,
                blockedAccounts
        );

        assertThrows(
                IllegalStateException.class,
                () -> service.transfer(from, to, 1000)
        );

        assertEquals(
                0,
                notificationService.getNotificationCount()
        );
    }
}