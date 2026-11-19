package org.example;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BankServiceSummaryTest {

    @Test
    void getAccountSummariesConvertsAccountsToSummaries() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BankService bank =
                new BankService(
                        repository,
                        createTransferService()
                );

        BankAccount debit =
                new DebitAccount(
                        new AccountNumber("1111111111"),
                        "Ivan",
                        5000
                );

        BankAccount credit =
                new CreditAccount(
                        new AccountNumber("2222222222"),
                        "Petr",
                        10000,
                        5000
                );

        bank.openAccount(debit);
        bank.openAccount(credit);

        // Act
        List<AccountSummary> summaries =
                bank.getAccountSummaries();

        // Assert
        assertEquals(2, summaries.size());

        assertTrue(
                summaries.contains(
                        new AccountSummary(
                                debit.getId(),
                                "DebitAccount",
                                5000
                        )
                )
        );

        assertTrue(
                summaries.contains(
                        new AccountSummary(
                                credit.getId(),
                                "CreditAccount",
                                10000
                        )
                )
        );
    }

    @Test
    void summaryContainsAccountNumberTypeAndBalance() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BankService bank =
                new BankService(
                        repository,
                        createTransferService()
                );

        BankAccount account =
                new DebitAccount(
                        new AccountNumber("1111111111"),
                        "Ivan",
                        7500
                );

        bank.openAccount(account);

        // Act
        List<AccountSummary> summaries =
                bank.getAccountSummaries();

        AccountSummary summary =
                summaries.get(0);

        // Assert
        assertEquals(
                account.getId(),
                summary.number()
        );

        assertEquals(
                "DebitAccount",
                summary.type()
        );

        assertEquals(
                7500,
                summary.balance()
        );
    }

    @Test
    void emptyRepositoryReturnsEmptySummaryList() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BankService bank =
                new BankService(
                        repository,
                        createTransferService()
                );

        // Act
        List<AccountSummary> summaries =
                bank.getAccountSummaries();

        // Assert
        assertTrue(summaries.isEmpty());
    }

    @Test
    void gettingSummariesDoesNotChangeRepository() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BankService bank =
                new BankService(
                        repository,
                        createTransferService()
                );

        BankAccount account =
                new DebitAccount(
                        new AccountNumber("1111111111"),
                        "Ivan",
                        5000
                );

        bank.openAccount(account);

        int sizeBefore =
                repository.size();

        // Act
        List<AccountSummary> summaries =
                bank.getAccountSummaries();

        // Assert
        assertEquals(1, summaries.size());
        assertEquals(sizeBefore, repository.size());

        assertSame(
                account,
                repository.findById(account.getId())
        );
    }

    private TransferService createTransferService() {
        return new TransferService(
                amount -> 0.0,
                message -> {
                },
                new BlockedAccountRegistry(),
                new TransactionJournal()
        );
    }
}