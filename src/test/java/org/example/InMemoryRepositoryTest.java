package org.example;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryRepositoryTest {

    @Test
    void saveAndFindByIdReturnsSavedAccount() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        AccountNumber id =
                new AccountNumber("0000000001");

        BankAccount account =
                new DebitAccount(
                        id,
                        "Ivan",
                        1000
                );

        // Act
        repository.save(account);

        BankAccount result =
                repository.findById(id);

        // Assert
        assertSame(account, result);
    }

    @Test
    void existsByIdReturnsTrueForExistingId() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        AccountNumber id =
                new AccountNumber("0000000001");

        BankAccount account =
                new DebitAccount(
                        id,
                        "Ivan",
                        1000
                );

        repository.save(account);

        // Act
        boolean result =
                repository.existsById(id);

        // Assert
        assertTrue(result);
    }

    @Test
    void existsByIdReturnsFalseForMissingId() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        AccountNumber id =
                new AccountNumber("0000000001");

        // Act
        boolean result =
                repository.existsById(id);

        // Assert
        assertFalse(result);
    }

    @Test
    void sizeIncreasesAfterSavingNewId() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BankAccount account =
                new DebitAccount(
                        new AccountNumber("0000000001"),
                        "Ivan",
                        1000
                );

        // Act
        repository.save(account);

        // Assert
        assertEquals(
                1,
                repository.size()
        );
    }

    @Test
    void savingSameIdReplacesOldValue() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        AccountNumber id =
                new AccountNumber("0000000001");

        BankAccount oldAccount =
                new DebitAccount(
                        id,
                        "Ivan",
                        1000
                );

        BankAccount newAccount =
                new DebitAccount(
                        id,
                        "Petr",
                        5000
                );

        // Act
        repository.save(oldAccount);
        repository.save(newAccount);

        // Assert
        assertEquals(
                1,
                repository.size()
        );

        assertSame(
                newAccount,
                repository.findById(id)
        );
    }

    @Test
    void deleteByIdRemovesExistingAccount() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        AccountNumber id =
                new AccountNumber("0000000001");

        BankAccount account =
                new DebitAccount(
                        id,
                        "Ivan",
                        1000
                );

        repository.save(account);

        // Act
        boolean result =
                repository.deleteById(id);

        // Assert
        assertTrue(result);
        assertFalse(repository.existsById(id));
        assertNull(repository.findById(id));
        assertEquals(0, repository.size());
    }

    @Test
    void deleteByIdReturnsFalseForMissingId() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        AccountNumber id =
                new AccountNumber("0000000001");

        // Act
        boolean result =
                repository.deleteById(id);

        // Assert
        assertFalse(result);
    }

    @Test
    void findAllReturnsAllSavedAccounts() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        BankAccount first =
                new DebitAccount(
                        new AccountNumber("0000000001"),
                        "Ivan",
                        1000
                );

        BankAccount second =
                new DebitAccount(
                        new AccountNumber("0000000002"),
                        "Petr",
                        2000
                );

        repository.save(first);
        repository.save(second);

        // Act
        List<BankAccount> result =
                repository.findAll();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(first));
        assertTrue(result.contains(second));
    }

    @Test
    void saveNullIsForbidden() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        // Act + Assert
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> repository.save(null)
                );

        assertEquals(
                "Value must not be null",
                exception.getMessage()
        );
    }
}