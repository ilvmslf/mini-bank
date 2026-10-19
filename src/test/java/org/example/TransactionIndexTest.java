package org.example;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionIndexTest {

    @Test
    void addedTransactionCanBeFoundByAccount() {
        // Arrange
        TransactionIndex index =
                new TransactionIndex();

        AccountNumber number =
                new AccountNumber("1111111111");

        Transaction transaction =
                new Transaction(
                        TransactionType.TRANSFER,
                        number,
                        1000,
                        TransactionStatus.SUCCESS
                );

        // Act
        index.add(transaction);

        List<Transaction> result =
                index.findByAccount(number);

        // Assert
        assertEquals(1, result.size());
        assertSame(transaction, result.get(0));
    }

    @Test
    void transactionCanBeFoundByEqualAccountNumber() {
        // Arrange
        TransactionIndex index =
                new TransactionIndex();

        AccountNumber stored =
                new AccountNumber("1111111111");

        AccountNumber lookup =
                new AccountNumber("1111111111");

        Transaction transaction =
                new Transaction(
                        TransactionType.TRANSFER,
                        stored,
                        1000,
                        TransactionStatus.SUCCESS
                );

        // Act
        index.add(transaction);

        List<Transaction> result =
                index.findByAccount(lookup);

        // Assert
        assertEquals(1, result.size());
        assertSame(transaction, result.get(0));
    }

    @Test
    void unknownAccountReturnsEmptyList() {
        // Arrange
        TransactionIndex index =
                new TransactionIndex();

        AccountNumber unknown =
                new AccountNumber("1111111111");

        // Act
        List<Transaction> result =
                index.findByAccount(unknown);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void multipleTransactionsForSameAccountAreReturned() {
        // Arrange
        TransactionIndex index =
                new TransactionIndex();

        AccountNumber number =
                new AccountNumber("1111111111");

        Transaction first =
                new Transaction(
                        TransactionType.TRANSFER,
                        number,
                        1000,
                        TransactionStatus.SUCCESS
                );

        Transaction second =
                new Transaction(
                        TransactionType.TRANSFER,
                        number,
                        2000,
                        TransactionStatus.SUCCESS
                );

        // Act
        index.add(first);
        index.add(second);

        List<Transaction> result =
                index.findByAccount(number);

        // Assert
        assertEquals(2, result.size());
        assertSame(first, result.get(0));
        assertSame(second, result.get(1));
    }

    @Test
    void returnedListDoesNotExposeIndexState() {
        // Arrange
        TransactionIndex index =
                new TransactionIndex();

        AccountNumber number =
                new AccountNumber("1111111111");

        Transaction transaction =
                new Transaction(
                        TransactionType.TRANSFER,
                        number,
                        1000,
                        TransactionStatus.SUCCESS
                );

        index.add(transaction);

        // Act
        List<Transaction> result =
                index.findByAccount(number);

        result.clear();

        // Assert
        assertEquals(
                1,
                index.findByAccount(number).size()
        );
    }
}