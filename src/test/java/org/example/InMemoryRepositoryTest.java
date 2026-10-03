package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryRepositoryTest {

    @Test
    void savedAccountCanBeFoundById() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        AccountNumber number =
                new AccountNumber("0000000001");

        BankAccount account =
                new DebitAccount(
                        number,
                        "Ivan",
                        1000
                );

        // Act
        repository.save(account);

        BankAccount result =
                repository.findById(number);

        // Assert
        assertEquals(account, result);
    }

    @Test
    void sizeIncreasesAfterSave() {
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
        assertEquals(1, repository.size());
    }

    @Test
    void existsByIdReturnsTrueForSavedAccount() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        AccountNumber number =
                new AccountNumber("0000000001");

        BankAccount account =
                new DebitAccount(
                        number,
                        "Ivan",
                        1000
                );

        repository.save(account);

        // Act
        boolean result =
                repository.existsById(number);

        // Assert
        assertTrue(result);
    }

    @Test
    void existsByIdReturnsFalseForMissingAccount() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        AccountNumber number =
                new AccountNumber("0000000001");

        // Act
        boolean result =
                repository.existsById(number);

        // Assert
        assertFalse(result);
    }

    @Test
    void findByIdReturnsNullWhenAccountDoesNotExist() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        AccountNumber number =
                new AccountNumber("0000000001");

        // Act
        BankAccount result =
                repository.findById(number);

        // Assert
        assertNull(result);
    }

    @Test
    void nullCannotBeSaved() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

        // Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> repository.save(null)
        );
    }

    @Test
    void savingSameIdReplacesOldAccount() {
        // Arrange
        Repository<AccountNumber, BankAccount> repository =
                new InMemoryRepository<>();

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
        repository.save(oldAccount);
        repository.save(newAccount);

        BankAccount result =
                repository.findById(number);

        // Assert
        assertSame(newAccount, result);
        assertEquals(1, repository.size());
    }
}