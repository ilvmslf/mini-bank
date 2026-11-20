package org.example;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationDecoratorTest {

    @Test
    void loggingDecoratorDelegatesNotification() {
        // Arrange
        FakeNotificationService fake =
                new FakeNotificationService();

        NotificationService notification =
                new LoggingNotificationDecorator(
                        fake
                );

        // Act
        notification.notify(
                "Transfer completed"
        );

        // Assert
        assertEquals(
                1,
                fake.getMessages().size()
        );

        assertEquals(
                "Transfer completed",
                fake.getMessages().get(0)
        );
    }

    @Test
    void countingDecoratorCountsNotifications() {
        // Arrange
        FakeNotificationService fake =
                new FakeNotificationService();

        CountingNotificationDecorator notification =
                new CountingNotificationDecorator(
                        fake
                );

        // Act
        notification.notify("First");
        notification.notify("Second");
        notification.notify("Third");

        // Assert
        assertEquals(
                3,
                notification.getCount()
        );

        assertEquals(
                3,
                fake.getMessages().size()
        );
    }

    @Test
    void prefixDecoratorAddsPrefix() {
        // Arrange
        FakeNotificationService fake =
                new FakeNotificationService();

        NotificationService notification =
                new PrefixNotificationDecorator(
                        fake,
                        "[BANK] "
                );

        // Act
        notification.notify(
                "Transfer completed"
        );

        // Assert
        assertEquals(
                1,
                fake.getMessages().size()
        );

        assertEquals(
                "[BANK] Transfer completed",
                fake.getMessages().get(0)
        );
    }

    @Test
    void decoratorsCanBeCombined() {
        // Arrange
        FakeNotificationService fake =
                new FakeNotificationService();

        CountingNotificationDecorator counting =
                new CountingNotificationDecorator(
                        fake
                );

        NotificationService notification =
                new PrefixNotificationDecorator(
                        counting,
                        "[BANK] "
                );

        // Act
        notification.notify(
                "Transfer completed"
        );

        // Assert
        assertEquals(
                1,
                counting.getCount()
        );

        assertEquals(
                "[BANK] Transfer completed",
                fake.getMessages().get(0)
        );
    }

    @Test
    void retryDecoratorRetriesAfterFailure() {
        // Arrange
        FailingNotificationService failing =
                new FailingNotificationService(
                        2
                );

        NotificationService notification =
                new RetryNotificationDecorator(
                        failing,
                        3
                );

        // Act
        notification.notify(
                "Transfer completed"
        );

        // Assert
        assertEquals(
                3,
                failing.getAttempts()
        );

        assertEquals(
                "Transfer completed",
                failing.getLastMessage()
        );
    }

    @Test
    void retryDecoratorThrowsWhenAllAttemptsFail() {
        // Arrange
        FailingNotificationService failing =
                new FailingNotificationService(
                        10
                );

        NotificationService notification =
                new RetryNotificationDecorator(
                        failing,
                        3
                );

        // Act + Assert
        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () ->
                                notification.notify(
                                        "Transfer completed"
                                )
                );

        assertEquals(
                "Notification failed",
                exception.getMessage()
        );

        assertEquals(
                3,
                failing.getAttempts()
        );
    }

    @Test
    void transferServiceWorksWithDecoratorWithoutModification() {
        // Arrange
        BankAccount from =
                new DebitAccount(
                        new AccountNumber("1111111111"),
                        "Ivan",
                        5000
                );

        BankAccount to =
                new DebitAccount(
                        new AccountNumber("2222222222"),
                        "Petr",
                        1000
                );

        FakeNotificationService fake =
                new FakeNotificationService();

        CountingNotificationDecorator counting =
                new CountingNotificationDecorator(
                        fake
                );

        NotificationService notification =
                new PrefixNotificationDecorator(
                        counting,
                        "[BANK] "
                );

        TransferService transferService =
                new TransferService(
                        amount -> 0.0,
                        notification,
                        new BlockedAccountRegistry(),
                        new TransactionJournal()
                );

        // Act
        transferService.transfer(
                from,
                to,
                2000
        );

        // Assert
        assertEquals(
                3000,
                from.getBalance()
        );

        assertEquals(
                3000,
                to.getBalance()
        );

        assertEquals(
                1,
                counting.getCount()
        );

        assertEquals(
                1,
                fake.getMessages().size()
        );

        assertEquals(
                "[BANK] Transfer 2000.0 completed",
                fake.getMessages().get(0)
        );
    }

    private static class FakeNotificationService
            implements NotificationService {

        private final List<String> messages =
                new ArrayList<>();

        @Override
        public void notify(String message) {
            messages.add(message);
        }

        public List<String> getMessages() {
            return messages;
        }
    }

    private static class FailingNotificationService
            implements NotificationService {

        private final int failuresBeforeSuccess;

        private int attempts;
        private String lastMessage;

        public FailingNotificationService(
                int failuresBeforeSuccess) {

            this.failuresBeforeSuccess =
                    failuresBeforeSuccess;
        }

        @Override
        public void notify(String message) {
            attempts++;

            if (attempts <= failuresBeforeSuccess) {
                throw new RuntimeException(
                        "Notification failed"
                );
            }

            lastMessage = message;
        }

        public int getAttempts() {
            return attempts;
        }

        public String getLastMessage() {
            return lastMessage;
        }
    }
}