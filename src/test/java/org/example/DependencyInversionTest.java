package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DependencyInversionTest {

    @Test
    void accountServiceWorksWithRepositoryAbstraction() {
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

        // Assert
        assertTrue(
                accountService
                        .find(account.getId())
                        .isPresent()
        );
    }

    @Test
    void transferApplicationServiceWorksWithRepositoryAbstraction() {
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
    void accountServiceCanWorkWithFakeRepository() {
        // Arrange
        FakeAccountRepository repository =
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
            implements Repository<AccountNumber, BankAccount> {

        private BankAccount account;

        @Override
        public void save(BankAccount value) {
            account = value;
        }

        @Override
        public BankAccount findById(
                AccountNumber id) {

            if (account == null) {
                return null;
            }

            if (account.getId().equals(id)) {
                return account;
            }

            return null;
        }

        @Override
        public boolean existsById(
                AccountNumber id) {

            return findById(id) != null;
        }

        @Override
        public boolean deleteById(
                AccountNumber id) {

            if (!existsById(id)) {
                return false;
            }

            account = null;
            return true;
        }

        @Override
        public java.util.List<BankAccount> findAll() {
            if (account == null) {
                return java.util.List.of();
            }

            return java.util.List.of(account);
        }

        @Override
        public int size() {
            return account == null ? 0 : 1;
        }
    }
}