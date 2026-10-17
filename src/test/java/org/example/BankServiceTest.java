package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BankServiceTest {

    @Test
    void openAccountThenFindAccount() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        TransferService transferService =
                createTransferService();

        BankService bankService =
                new BankService(
                        repository,
                        transferService
                );

        AccountNumber number =
                new AccountNumber("0000000001");

        BankAccount account =
                new DebitAccount(
                        number,
                        "Ivan",
                        1000
                );

        // Act
        bankService.openAccount(account);

        BankAccount result =
                bankService.findAccount(number);

        // Assert
        assertSame(account, result);
    }

    @Test
    void openingAccountWithSameIdReplacesOldAccount() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BankService bankService =
                new BankService(
                        repository,
                        createTransferService()
                );

        AccountNumber number =
                new AccountNumber("0000000001");

        BankAccount oldAccount =
                new DebitAccount(
                        number,
                        "Ivan",
                        1000
                );

        BankAccount newAccount =
                new DebitAccount(
                        number,
                        "Petr",
                        5000
                );

        // Act
        bankService.openAccount(oldAccount);
        bankService.openAccount(newAccount);

        // Assert
        assertEquals(1, repository.size());

        assertSame(
                newAccount,
                bankService.findAccount(number)
        );
    }

    @Test
    void closeAccountRemovesAccount() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BankService bankService =
                new BankService(
                        repository,
                        createTransferService()
                );

        AccountNumber number =
                new AccountNumber("0000000001");

        BankAccount account =
                new DebitAccount(
                        number,
                        "Ivan",
                        1000
                );

        bankService.openAccount(account);

        // Act
        boolean result =
                bankService.closeAccount(number);

        // Assert
        assertTrue(result);
        assertNull(bankService.findAccount(number));
        assertFalse(repository.existsById(number));
    }

    @Test
    void closeMissingAccountReturnsFalse() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BankService bankService =
                new BankService(
                        repository,
                        createTransferService()
                );

        AccountNumber number =
                new AccountNumber("0000000001");

        // Act
        boolean result =
                bankService.closeAccount(number);

        // Assert
        assertFalse(result);
    }

    @Test
    void findMissingAccountReturnsNull() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BankService bankService =
                new BankService(
                        repository,
                        createTransferService()
                );

        AccountNumber number =
                new AccountNumber("0000000001");

        // Act
        BankAccount result =
                bankService.findAccount(number);

        // Assert
        assertNull(result);
    }

    @Test
    void openNullAccountThrowsException() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BankService bankService =
                new BankService(
                        repository,
                        createTransferService()
                );

        // Act + Assert
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> bankService.openAccount(null)
                );

        assertEquals(
                "Account must not be null",
                exception.getMessage()
        );
    }

    private TransferService createTransferService() {
        return new TransferService(
                new NoCommission(),
                new ConsoleNotificationService(),
                new BlockedAccountRegistry(),
                new TransactionJournal()
        );
    }
}