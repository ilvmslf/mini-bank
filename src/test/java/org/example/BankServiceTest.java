package org.example;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

public class BankServiceTest {

    @Test
    void openAccountThenFindAccount() {
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

        BankAccount result =
                bankService.findAccount(number);

        assertSame(account, result);
    }

    @Test
    void openingAccountWithSameIdReplacesOldAccount() {
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

        bankService.openAccount(oldAccount);
        bankService.openAccount(newAccount);

        assertEquals(1, repository.size());

        assertSame(
                newAccount,
                bankService.findAccount(number)
        );
    }

    @Test
    void closeAccountRemovesAccount() {
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

        boolean result =
                bankService.closeAccount(number);

        assertTrue(result);
        assertNull(bankService.findAccount(number));
        assertFalse(repository.existsById(number));
    }

    @Test
    void closeMissingAccountReturnsFalse() {
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BankService bankService =
                new BankService(
                        repository,
                        createTransferService()
                );

        AccountNumber number =
                new AccountNumber("0000000001");

        boolean result =
                bankService.closeAccount(number);

        assertFalse(result);
    }

    @Test
    void findMissingAccountReturnsNull() {
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BankService bankService =
                new BankService(
                        repository,
                        createTransferService()
                );

        BankAccount result =
                bankService.findAccount(
                        new AccountNumber("0000000001")
                );

        assertNull(result);
    }

    @Test
    void openNullAccountThrowsException() {
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BankService bankService =
                new BankService(
                        repository,
                        createTransferService()
                );

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

        bankService.transfer(
                fromNumber,
                toNumber,
                5000
        );

        assertEquals(5000, from.getBalance());
        assertEquals(7000, to.getBalance());
    }

    @Test
    void missingSourceAccountThrowsException() {
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

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> bankService.transfer(
                                missingFrom,
                                toNumber,
                                1000
                        )
                );

        assertEquals(
                "Source account not found",
                exception.getMessage()
        );

        assertEquals(2000, to.getBalance());
    }

    @Test
    void missingTargetAccountThrowsException() {
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

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> bankService.transfer(
                                fromNumber,
                                missingTo,
                                1000
                        )
                );

        assertEquals(
                "Target account not found",
                exception.getMessage()
        );

        assertEquals(5000, from.getBalance());
    }

    @Test
    void blockedSourceAccountCannotTransfer() {
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
                        amount -> 0.0,
                        message -> {
                        },
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

        assertThrows(
                IllegalStateException.class,
                () -> bankService.transfer(
                        fromNumber,
                        toNumber,
                        1000
                )
        );

        assertEquals(5000, from.getBalance());
        assertEquals(2000, to.getBalance());
    }

    @Test
    void transferWithoutEnoughMoneyThrowsException() {
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

        assertThrows(
                InsufficientFundsException.class,
                () -> bankService.transfer(
                        fromNumber,
                        toNumber,
                        5000
                )
        );

        assertEquals(1000, from.getBalance());
        assertEquals(2000, to.getBalance());
    }

    @Test
    void findAccountsWithPositiveBalance() {
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BankService bankService =
                new BankService(
                        repository,
                        createTransferService()
                );

        BankAccount positive =
                new DebitAccount(
                        new AccountNumber("0000000001"),
                        "Ivan",
                        5000
                );

        BankAccount zero =
                new DebitAccount(
                        new AccountNumber("0000000002"),
                        "Petr",
                        0
                );

        bankService.openAccount(positive);
        bankService.openAccount(zero);

        Predicate<BankAccount> positiveBalance =
                account -> account.getBalance() > 0;

        List<BankAccount> result =
                bankService.findAccounts(
                        positiveBalance
                );

        assertEquals(1, result.size());
        assertSame(positive, result.get(0));
    }

    @Test
    void findOnlyDebitAccounts() {
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BankService bankService =
                new BankService(
                        repository,
                        createTransferService()
                );

        BankAccount debit =
                new DebitAccount(
                        new AccountNumber("0000000001"),
                        "Ivan",
                        1000
                );

        BankAccount savings =
                new SavingsAccount(
                        new AccountNumber("0000000002"),
                        "Petr",
                        2000,
                        500
                );

        bankService.openAccount(debit);
        bankService.openAccount(savings);

        Predicate<BankAccount> debitOnly =
                DebitAccount.class::isInstance;

        List<BankAccount> result =
                bankService.findAccounts(
                        debitOnly
                );

        assertEquals(1, result.size());
        assertSame(debit, result.get(0));
    }

    @Test
    void findAccountsDoesNotExposeRepositoryList() {
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BankService bankService =
                new BankService(
                        repository,
                        createTransferService()
                );

        BankAccount account =
                new DebitAccount(
                        new AccountNumber("0000000001"),
                        "Ivan",
                        1000
                );

        bankService.openAccount(account);

        List<BankAccount> result =
                bankService.findAccounts(
                        value -> true
                );

        result.clear();

        assertEquals(1, repository.size());

        assertSame(
                account,
                bankService.findAccount(
                        new AccountNumber("0000000001")
                )
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