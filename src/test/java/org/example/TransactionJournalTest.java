package org.example;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionJournalTest {

    @Test
    void transactionsAreStoredInInsertionOrder() {
        // Arrange
        TransactionJournal journal =
                new TransactionJournal();

        Transaction first = new Transaction(
                TransactionType.DEPOSIT,
                new AccountNumber("0000000001"),
                1000,
                TransactionStatus.SUCCESS
        );

        Transaction second = new Transaction(
                TransactionType.WITHDRAWAL,
                new AccountNumber("0000000002"),
                500,
                TransactionStatus.SUCCESS
        );

        // Act
        journal.add(first);
        journal.add(second);

        List<Transaction> transactions =
                journal.findAll();

        // Assert
        assertSame(first, transactions.get(0));
        assertSame(second, transactions.get(1));
    }

    @Test
    void identicalTransactionsCanBeStoredTwice() {
        // Arrange
        TransactionJournal journal =
                new TransactionJournal();

        Transaction transaction = new Transaction(
                TransactionType.TRANSFER,
                new AccountNumber("0000000001"),
                1000,
                TransactionStatus.SUCCESS
        );

        // Act
        journal.add(transaction);
        journal.add(transaction);

        // Assert
        assertEquals(2, journal.size());
    }

    @Test
    void sizeReturnsNumberOfTransactions() {
        // Arrange
        TransactionJournal journal =
                new TransactionJournal();

        Transaction first = new Transaction(
                TransactionType.DEPOSIT,
                new AccountNumber("0000000001"),
                1000,
                TransactionStatus.SUCCESS
        );

        Transaction second = new Transaction(
                TransactionType.TRANSFER,
                new AccountNumber("0000000002"),
                2000,
                TransactionStatus.SUCCESS
        );

        // Act
        journal.add(first);
        journal.add(second);

        // Assert
        assertEquals(2, journal.size());
    }

    @Test
    void returnedListDoesNotExposeInternalState() {
        // Arrange
        TransactionJournal journal =
                new TransactionJournal();

        Transaction transaction = new Transaction(
                TransactionType.TRANSFER,
                new AccountNumber("0000000001"),
                1000,
                TransactionStatus.SUCCESS
        );

        journal.add(transaction);

        // Act
        List<Transaction> copy =
                journal.findAll();

        copy.clear();

        // Assert
        assertEquals(1, journal.size());
    }

    @Test
    void removeRejectedRemovesAllRejectedTransactions() {
        // Arrange
        TransactionJournal journal =
                new TransactionJournal();

        Transaction success1 = new Transaction(
                TransactionType.DEPOSIT,
                new AccountNumber("0000000001"),
                1000,
                TransactionStatus.SUCCESS
        );

        Transaction rejected1 = new Transaction(
                TransactionType.WITHDRAWAL,
                new AccountNumber("0000000002"),
                500,
                TransactionStatus.REJECTED
        );

        Transaction success2 = new Transaction(
                TransactionType.TRANSFER,
                new AccountNumber("0000000003"),
                2000,
                TransactionStatus.SUCCESS
        );

        Transaction rejected2 = new Transaction(
                TransactionType.TRANSFER,
                new AccountNumber("0000000004"),
                3000,
                TransactionStatus.REJECTED
        );

        journal.add(success1);
        journal.add(rejected1);
        journal.add(success2);
        journal.add(rejected2);

        // Act
        journal.removeRejected();

        // Assert
        List<Transaction> transactions =
                journal.findAll();

        assertEquals(2, transactions.size());

        assertSame(
                success1,
                transactions.get(0)
        );

        assertSame(
                success2,
                transactions.get(1)
        );
    }

    @Test
    void removeRejectedRemovesAllWhenEveryTransactionIsRejected() {
        // Arrange
        TransactionJournal journal =
                new TransactionJournal();

        Transaction first = new Transaction(
                TransactionType.TRANSFER,
                new AccountNumber("0000000001"),
                1000,
                TransactionStatus.REJECTED
        );

        Transaction second = new Transaction(
                TransactionType.TRANSFER,
                new AccountNumber("0000000002"),
                2000,
                TransactionStatus.REJECTED
        );

        journal.add(first);
        journal.add(second);

        // Act
        journal.removeRejected();

        // Assert
        assertEquals(0, journal.size());
        assertTrue(journal.findAll().isEmpty());
    }

    @Test
    void removeRejectedDoesNotRemoveSuccessfulTransactions() {
        // Arrange
        TransactionJournal journal =
                new TransactionJournal();

        Transaction first = new Transaction(
                TransactionType.DEPOSIT,
                new AccountNumber("0000000001"),
                1000,
                TransactionStatus.SUCCESS
        );

        Transaction second = new Transaction(
                TransactionType.TRANSFER,
                new AccountNumber("0000000002"),
                2000,
                TransactionStatus.SUCCESS
        );

        journal.add(first);
        journal.add(second);

        // Act
        journal.removeRejected();

        // Assert
        assertEquals(2, journal.size());
    }
}