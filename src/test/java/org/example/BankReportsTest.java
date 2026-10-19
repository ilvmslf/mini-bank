package org.example;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class BankReportsTest {

    @Test
    void emptyListProducesEmptyStatistics() {
        // Arrange
        BankReports reports =
                new BankReports();

        List<Transaction> transactions =
                new ArrayList<>();

        // Act
        Map<AccountNumber, Integer> countResult =
                reports.countByAccount(transactions);

        Map<AccountNumber, Double> amountResult =
                reports.totalAmountByAccount(transactions);

        // Assert
        assertTrue(countResult.isEmpty());
        assertTrue(amountResult.isEmpty());
    }

    @Test
    void oneAccountIsCounted() {
        // Arrange
        BankReports reports =
                new BankReports();

        AccountNumber number =
                new AccountNumber("1111111111");

        List<Transaction> transactions =
                new ArrayList<>();

        transactions.add(
                new Transaction(
                        TransactionType.TRANSFER,
                        number,
                        1000,
                        TransactionStatus.SUCCESS
                )
        );

        // Act
        Map<AccountNumber, Integer> result =
                reports.countByAccount(transactions);

        // Assert
        assertEquals(1, result.size());
        assertEquals(1, result.get(number));
    }

    @Test
    void severalAccountsAreCountedSeparately() {
        // Arrange
        BankReports reports =
                new BankReports();

        AccountNumber firstNumber =
                new AccountNumber("1111111111");

        AccountNumber secondNumber =
                new AccountNumber("2222222222");

        List<Transaction> transactions =
                new ArrayList<>();

        transactions.add(
                new Transaction(
                        TransactionType.TRANSFER,
                        firstNumber,
                        1000,
                        TransactionStatus.SUCCESS
                )
        );

        transactions.add(
                new Transaction(
                        TransactionType.TRANSFER,
                        secondNumber,
                        2000,
                        TransactionStatus.SUCCESS
                )
        );

        // Act
        Map<AccountNumber, Integer> result =
                reports.countByAccount(transactions);

        // Assert
        assertEquals(2, result.size());
        assertEquals(1, result.get(firstNumber));
        assertEquals(1, result.get(secondNumber));
    }

    @Test
    void severalOperationsOfSameAccountAreCountedTogether() {
        // Arrange
        BankReports reports =
                new BankReports();

        AccountNumber number =
                new AccountNumber("1111111111");

        List<Transaction> transactions =
                new ArrayList<>();

        transactions.add(
                new Transaction(
                        TransactionType.TRANSFER,
                        number,
                        1000,
                        TransactionStatus.SUCCESS
                )
        );

        transactions.add(
                new Transaction(
                        TransactionType.TRANSFER,
                        number,
                        2000,
                        TransactionStatus.SUCCESS
                )
        );

        transactions.add(
                new Transaction(
                        TransactionType.TRANSFER,
                        number,
                        3000,
                        TransactionStatus.SUCCESS
                )
        );

        // Act
        Map<AccountNumber, Integer> result =
                reports.countByAccount(transactions);

        // Assert
        assertEquals(1, result.size());
        assertEquals(3, result.get(number));
    }

    @Test
    void totalAmountForOneAccountIsCalculated() {
        // Arrange
        BankReports reports =
                new BankReports();

        AccountNumber number =
                new AccountNumber("1111111111");

        List<Transaction> transactions =
                new ArrayList<>();

        transactions.add(
                new Transaction(
                        TransactionType.TRANSFER,
                        number,
                        1000,
                        TransactionStatus.SUCCESS
                )
        );

        transactions.add(
                new Transaction(
                        TransactionType.TRANSFER,
                        number,
                        2500,
                        TransactionStatus.SUCCESS
                )
        );

        // Act
        Map<AccountNumber, Double> result =
                reports.totalAmountByAccount(
                        transactions
                );

        // Assert
        assertEquals(1, result.size());
        assertEquals(
                3500.0,
                result.get(number)
        );
    }

    @Test
    void totalAmountForSeveralAccountsIsCalculatedSeparately() {
        // Arrange
        BankReports reports =
                new BankReports();

        AccountNumber firstNumber =
                new AccountNumber("1111111111");

        AccountNumber secondNumber =
                new AccountNumber("2222222222");

        List<Transaction> transactions =
                new ArrayList<>();

        transactions.add(
                new Transaction(
                        TransactionType.TRANSFER,
                        firstNumber,
                        1000,
                        TransactionStatus.SUCCESS
                )
        );

        transactions.add(
                new Transaction(
                        TransactionType.TRANSFER,
                        secondNumber,
                        5000,
                        TransactionStatus.SUCCESS
                )
        );

        transactions.add(
                new Transaction(
                        TransactionType.TRANSFER,
                        firstNumber,
                        2000,
                        TransactionStatus.SUCCESS
                )
        );

        // Act
        Map<AccountNumber, Double> result =
                reports.totalAmountByAccount(
                        transactions
                );

        // Assert
        assertEquals(2, result.size());

        assertEquals(
                3000.0,
                result.get(firstNumber)
        );

        assertEquals(
                5000.0,
                result.get(secondNumber)
        );
    }

    @Test
    void equalAccountNumbersAreAggregatedIntoOneCountEntry() {
        // Arrange
        BankReports reports =
                new BankReports();

        AccountNumber firstObject =
                new AccountNumber("1111111111");

        AccountNumber secondObject =
                new AccountNumber("1111111111");

        List<Transaction> transactions =
                new ArrayList<>();

        transactions.add(
                new Transaction(
                        TransactionType.TRANSFER,
                        firstObject,
                        1000,
                        TransactionStatus.SUCCESS
                )
        );

        transactions.add(
                new Transaction(
                        TransactionType.TRANSFER,
                        secondObject,
                        2000,
                        TransactionStatus.SUCCESS
                )
        );

        // Act
        Map<AccountNumber, Integer> result =
                reports.countByAccount(
                        transactions
                );

        // Assert
        assertEquals(1, result.size());
        assertEquals(2, result.get(firstObject));
        assertEquals(2, result.get(secondObject));
    }

    @Test
    void equalAccountNumbersAreAggregatedIntoOneAmountEntry() {
        // Arrange
        BankReports reports =
                new BankReports();

        AccountNumber firstObject =
                new AccountNumber("1111111111");

        AccountNumber secondObject =
                new AccountNumber("1111111111");

        List<Transaction> transactions =
                new ArrayList<>();

        transactions.add(
                new Transaction(
                        TransactionType.TRANSFER,
                        firstObject,
                        1000,
                        TransactionStatus.SUCCESS
                )
        );

        transactions.add(
                new Transaction(
                        TransactionType.TRANSFER,
                        secondObject,
                        2500,
                        TransactionStatus.SUCCESS
                )
        );

        // Act
        Map<AccountNumber, Double> result =
                reports.totalAmountByAccount(
                        transactions
                );

        // Assert
        assertEquals(1, result.size());

        assertEquals(
                3500.0,
                result.get(firstObject)
        );

        assertEquals(
                3500.0,
                result.get(secondObject)
        );
    }
}