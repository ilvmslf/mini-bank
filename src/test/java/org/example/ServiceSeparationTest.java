package org.example;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceSeparationTest {

    @Test
    void accountServiceWorksWithoutTransferService() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        AccountService accountService =
                new AccountService(repository);

        BankAccount account =
                new DebitAccount(
                        new AccountNumber("1111111111"),
                        "Ivan",
                        1000
                );

        // Act
        accountService.open(account);

        Optional<BankAccount> found =
                accountService.find(
                        account.getId()
                );

        // Assert
        assertTrue(found.isPresent());
        assertSame(account, found.get());

        assertTrue(
                accountService.close(
                        account.getId()
                )
        );

        assertTrue(
                accountService
                        .find(account.getId())
                        .isEmpty()
        );
    }

    @Test
    void accountServiceDoesNotDependOnTransferService() {
        // Arrange
        Class<?>[] constructorParameters =
                AccountService.class
                        .getConstructors()[0]
                        .getParameterTypes();

        // Assert
        assertEquals(
                1,
                constructorParameters.length
        );

        assertEquals(
                Repository.class,
                constructorParameters[0]
        );
    }

    @Test
    void transferApplicationServiceHasNoReportMethods() {
        // Arrange
        Method[] methods =
                TransferApplicationService.class
                        .getDeclaredMethods();

        // Act
        boolean hasReportMethod =
                Arrays.stream(methods)
                        .map(Method::getName)
                        .anyMatch(name ->
                                name.toLowerCase()
                                        .contains("report")
                        );

        // Assert
        assertFalse(hasReportMethod);
    }

    @Test
    void transferApplicationServiceTransfersMoney() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BlockedAccountRegistry blockedAccounts =
                new BlockedAccountRegistry();

        TransferService transferService =
                new TransferService(
                        amount -> 0.0,
                        message -> {
                        },
                        blockedAccounts,
                        new TransactionJournal()
                );

        TransferApplicationService applicationService =
                new TransferApplicationService(
                        repository,
                        transferService
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

        repository.save(from);
        repository.save(to);

        // Act
        applicationService.transfer(
                from.getId(),
                to.getId(),
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
    }

    @Test
    void transferStillRejectsInsufficientFunds() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        TransferService transferService =
                new TransferService(
                        amount -> 0.0,
                        message -> {
                        },
                        new BlockedAccountRegistry(),
                        new TransactionJournal()
                );

        TransferApplicationService applicationService =
                new TransferApplicationService(
                        repository,
                        transferService
                );

        BankAccount from =
                new DebitAccount(
                        new AccountNumber("1111111111"),
                        "Ivan",
                        1000
                );

        BankAccount to =
                new DebitAccount(
                        new AccountNumber("2222222222"),
                        "Petr",
                        500
                );

        repository.save(from);
        repository.save(to);

        double fromBefore =
                from.getBalance();

        double toBefore =
                to.getBalance();

        // Act
        assertThrows(
                InsufficientFundsException.class,
                () ->
                        applicationService.transfer(
                                from.getId(),
                                to.getId(),
                                2000
                        )
        );

        // Assert
        assertEquals(
                fromBefore,
                from.getBalance()
        );

        assertEquals(
                toBefore,
                to.getBalance()
        );
    }

    @Test
    void transferStillRejectsTransferOverLimit() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        TransferService transferService =
                new TransferService(
                        amount -> 0.0,
                        message -> {
                        },
                        new BlockedAccountRegistry(),
                        new TransactionJournal()
                );

        TransferApplicationService applicationService =
                new TransferApplicationService(
                        repository,
                        transferService
                );

        BankAccount from =
                new DebitAccount(
                        new AccountNumber("1111111111"),
                        "Ivan",
                        100000
                );

        BankAccount to =
                new DebitAccount(
                        new AccountNumber("2222222222"),
                        "Petr",
                        1000
                );

        repository.save(from);
        repository.save(to);

        double fromBefore =
                from.getBalance();

        double toBefore =
                to.getBalance();

        // Act
        assertThrows(
                TransferLimitExceededException.class,
                () ->
                        applicationService.transfer(
                                from.getId(),
                                to.getId(),
                                60000
                        )
        );

        // Assert
        assertEquals(
                fromBefore,
                from.getBalance()
        );

        assertEquals(
                toBefore,
                to.getBalance()
        );
    }

    @Test
    void transferStillRejectsBlockedAccount() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BlockedAccountRegistry blockedAccounts =
                new BlockedAccountRegistry();

        TransferService transferService =
                new TransferService(
                        amount -> 0.0,
                        message -> {
                        },
                        blockedAccounts,
                        new TransactionJournal()
                );

        TransferApplicationService applicationService =
                new TransferApplicationService(
                        repository,
                        transferService
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

        repository.save(from);
        repository.save(to);

        blockedAccounts.block(
                from.getId()
        );

        double fromBefore =
                from.getBalance();

        double toBefore =
                to.getBalance();

        // Act
        assertThrows(
                IllegalStateException.class,
                () ->
                        applicationService.transfer(
                                from.getId(),
                                to.getId(),
                                1000
                        )
        );

        // Assert
        assertEquals(
                fromBefore,
                from.getBalance()
        );

        assertEquals(
                toBefore,
                to.getBalance()
        );
    }
}