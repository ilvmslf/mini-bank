package org.example;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TransferObserverTest {

    @Test
    void publisherNotifiesSubscribedListener() {
        // Arrange
        TransferEventPublisher publisher =
                new TransferEventPublisher();

        List<Transaction> received =
                new ArrayList<>();

        publisher.subscribe(
                received::add
        );

        Transaction transaction =
                new Transaction(
                        TransactionType.TRANSFER,
                        new AccountNumber(
                                "1111111111"
                        ),
                        1000,
                        TransactionStatus.SUCCESS
                );

        // Act
        publisher.publish(transaction);

        // Assert
        assertEquals(
                1,
                received.size()
        );

        assertEquals(
                transaction,
                received.get(0)
        );
    }

    @Test
    void publisherNotifiesMultipleListeners() {
        // Arrange
        TransferEventPublisher publisher =
                new TransferEventPublisher();

        List<Transaction> firstListener =
                new ArrayList<>();

        List<Transaction> secondListener =
                new ArrayList<>();

        publisher.subscribe(
                firstListener::add
        );

        publisher.subscribe(
                secondListener::add
        );

        Transaction transaction =
                new Transaction(
                        TransactionType.TRANSFER,
                        new AccountNumber(
                                "1111111111"
                        ),
                        2000,
                        TransactionStatus.SUCCESS
                );

        // Act
        publisher.publish(transaction);

        // Assert
        assertEquals(
                1,
                firstListener.size()
        );

        assertEquals(
                1,
                secondListener.size()
        );

        assertEquals(
                transaction,
                firstListener.get(0)
        );

        assertEquals(
                transaction,
                secondListener.get(0)
        );
    }

    @Test
    void transferServicePublishesCompletedTransfer() {
        // Arrange
        TransferEventPublisher publisher =
                new TransferEventPublisher();

        List<Transaction> received =
                new ArrayList<>();

        publisher.subscribe(
                received::add
        );

        TransferService transferService =
                new TransferService(
                        amount -> 0.0,
                        new BlockedAccountRegistry(),
                        new TransactionJournal(),
                        publisher
                );

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

        // Act
        transferService.transfer(
                from,
                to,
                2000
        );

        // Assert
        assertEquals(
                1,
                received.size()
        );

        Transaction transaction =
                received.get(0);

        assertEquals(
                TransactionType.TRANSFER,
                transaction.type()
        );

        assertEquals(
                from.getId(),
                transaction.account()
        );

        assertEquals(
                2000,
                transaction.amount()
        );

        assertEquals(
                TransactionStatus.SUCCESS,
                transaction.status()
        );
    }

    @Test
    void transferServiceNotifiesSeveralObservers() {
        // Arrange
        TransferEventPublisher publisher =
                new TransferEventPublisher();

        List<Transaction> auditLog =
                new ArrayList<>();

        List<Transaction> notifications =
                new ArrayList<>();

        int[] statisticsCounter =
                {0};

        publisher.subscribe(
                auditLog::add
        );

        publisher.subscribe(
                notifications::add
        );

        publisher.subscribe(
                transaction ->
                        statisticsCounter[0]++
        );

        TransferService transferService =
                new TransferService(
                        amount -> 0.0,
                        new BlockedAccountRegistry(),
                        new TransactionJournal(),
                        publisher
                );

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

        // Act
        transferService.transfer(
                from,
                to,
                1000
        );

        // Assert
        assertEquals(
                1,
                auditLog.size()
        );

        assertEquals(
                1,
                notifications.size()
        );

        assertEquals(
                1,
                statisticsCounter[0]
        );
    }

    @Test
    void failedTransferDoesNotPublishEvent() {
        // Arrange
        TransferEventPublisher publisher =
                new TransferEventPublisher();

        List<Transaction> received =
                new ArrayList<>();

        publisher.subscribe(
                received::add
        );

        TransferService transferService =
                new TransferService(
                        amount -> 0.0,
                        new BlockedAccountRegistry(),
                        new TransactionJournal(),
                        publisher
                );

        BankAccount from =
                new DebitAccount(
                        new AccountNumber("1111111111"),
                        "Ivan",
                        500
                );

        BankAccount to =
                new DebitAccount(
                        new AccountNumber("2222222222"),
                        "Petr",
                        1000
                );

        // Act
        assertThrows(
                InsufficientFundsException.class,
                () ->
                        transferService.transfer(
                                from,
                                to,
                                2000
                        )
        );

        // Assert
        assertTrue(
                received.isEmpty()
        );
    }

    @Test
    void publisherCountsSubscribers() {
        // Arrange
        TransferEventPublisher publisher =
                new TransferEventPublisher();

        publisher.subscribe(
                transaction -> {
                }
        );

        publisher.subscribe(
                transaction -> {
                }
        );

        // Assert
        assertEquals(
                2,
                publisher.listenerCount()
        );
    }
}