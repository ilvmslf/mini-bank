package org.example;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class DependencyInversionTest {

    @Test
    void accountServiceWorksWithRepositoryPort() {
        // Arrange
        AccountRepository repository =
                new InMemoryAccountRepository();

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

        // Assert
        assertTrue(
                accountService
                        .find(account.getId())
                        .isPresent()
        );
    }

    @Test
    void transferApplicationServiceWorksWithRepositoryPort() {
        // Arrange
        AccountRepository repository =
                new InMemoryAccountRepository();

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
    void accountServiceCanWorkWithFakeAdapter() {
        // Arrange
        AccountRepository repository =
                new FakeAccountRepository();

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

        // Assert
        assertSame(
                account,
                accountService
                        .find(account.getId())
                        .orElseThrow()
        );
    }

    private static class FakeAccountRepository
            implements AccountRepository {

        private BankAccount account;

        @Override
        public void save(BankAccount account) {
            this.account = account;
        }

        @Override
        public Optional<BankAccount> findById(
                AccountNumber number) {

            if (account == null) {
                return Optional.empty();
            }

            if (account.getId().equals(number)) {
                return Optional.of(account);
            }

            return Optional.empty();
        }

        @Override
        public boolean deleteById(
                AccountNumber number) {

            if (account == null) {
                return false;
            }

            if (!account.getId().equals(number)) {
                return false;
            }

            account = null;
            return true;
        }
    }
}