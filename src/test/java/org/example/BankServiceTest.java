package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BankServiceTest {

    @Test
    void openAccountThenFindAccount() {
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

    @Test
    void successfulTransferByAccountNumbers() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        AccountNumber fromNumber =
                new AccountNumber("1111111111");

        AccountNumber toNumber =
                new AccountNumber("2222222222");

        BankAccount from =
                new DebitAccount(
                        fromNumber,
                        "Ivan",
                        10000
                );

        BankAccount to =
                new DebitAccount(
                        toNumber,
                        "Petr",
                        2000
                );

        BankService bankService =
                new BankService(
                        repository,
                        createTransferService()
                );

        bankService.openAccount(from);
        bankService.openAccount(to);

        // Act
        bankService.transfer(
                fromNumber,
                toNumber,
                5000
        );

        // Assert
        assertEquals(5000, from.getBalance());
        assertEquals(7000, to.getBalance());
    }

    @Test
    void missingSourceAccountThrowsException() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        AccountNumber missingFrom =
                new AccountNumber("1111111111");

        AccountNumber toNumber =
                new AccountNumber("2222222222");

        BankAccount to =
                new DebitAccount(
                        toNumber,
                        "Petr",
                        2000
                );

        BankService bankService =
                new BankService(
                        repository,
                        createTransferService()
                );

        bankService.openAccount(to);

        // Act
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> bankService.transfer(
                                missingFrom,
                                toNumber,
                                1000
                        )
                );

        // Assert
        assertEquals(
                "Source account not found",
                exception.getMessage()
        );

        assertEquals(2000, to.getBalance());
    }

    @Test
    void missingTargetAccountThrowsException() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        AccountNumber fromNumber =
                new AccountNumber("1111111111");

        AccountNumber missingTo =
                new AccountNumber("2222222222");

        BankAccount from =
                new DebitAccount(
                        fromNumber,
                        "Ivan",
                        5000
                );

        BankService bankService =
                new BankService(
                        repository,
                        createTransferService()
                );

        bankService.openAccount(from);

        // Act
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> bankService.transfer(
                                fromNumber,
                                missingTo,
                                1000
                        )
                );

        // Assert
        assertEquals(
                "Target account not found",
                exception.getMessage()
        );

        assertEquals(5000, from.getBalance());
    }

    @Test
    void blockedSourceAccountCannotTransfer() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        AccountNumber fromNumber =
                new AccountNumber("1111111111");

        AccountNumber toNumber =
                new AccountNumber("2222222222");

        BankAccount from =
                new DebitAccount(
                        fromNumber,
                        "Ivan",
                        5000
                );

        BankAccount to =
                new DebitAccount(
                        toNumber,
                        "Petr",
                        2000
                );

        BlockedAccountRegistry blockedAccounts =
                new BlockedAccountRegistry();

        blockedAccounts.block(fromNumber);

        TransferService transferService =
                new TransferService(
                        new NoCommission(),
                        new ConsoleNotificationService(),
                        blockedAccounts,
                        new TransactionJournal()
                );

        BankService bankService =
                new BankService(
                        repository,
                        transferService
                );

        bankService.openAccount(from);
        bankService.openAccount(to);

        // Act
        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> bankService.transfer(
                                fromNumber,
                                toNumber,
                                1000
                        )
                );

        // Assert
        assertEquals(
                "Source account is blocked",
                exception.getMessage()
        );

        assertEquals(5000, from.getBalance());
        assertEquals(2000, to.getBalance());
    }

    @Test
    void transferWithoutEnoughMoneyThrowsException() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        AccountNumber fromNumber =
                new AccountNumber("1111111111");

        AccountNumber toNumber =
                new AccountNumber("2222222222");

        BankAccount from =
                new DebitAccount(
                        fromNumber,
                        "Ivan",
                        1000
                );

        BankAccount to =
                new DebitAccount(
                        toNumber,
                        "Petr",
                        2000
                );

        BankService bankService =
                new BankService(
                        repository,
                        createTransferService()
                );

        bankService.openAccount(from);
        bankService.openAccount(to);

        // Act
        InsufficientFundsException exception =
                assertThrows(
                        InsufficientFundsException.class,
                        () -> bankService.transfer(
                                fromNumber,
                                toNumber,
                                5000
                        )
                );

        // Assert
        assertEquals(
                "Insufficient funds",
                exception.getMessage()
        );

        assertEquals(1000, from.getBalance());
        assertEquals(2000, to.getBalance());
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