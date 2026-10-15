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
}