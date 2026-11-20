package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AccountFactoryTest {

    @Test
    void createsDebitAccount() {
        // Arrange
        AccountFactory factory =
                new DefaultAccountFactory();

        AccountNumber number =
                new AccountNumber(
                        "1111111111"
                );

        // Act
        BankAccount account =
                factory.create(
                        AccountType.DEBIT,
                        number,
                        "Ivan",
                        5000
                );

        // Assert
        assertInstanceOf(
                DebitAccount.class,
                account
        );

        assertEquals(
                number,
                account.getId()
        );

        assertEquals(
                "Ivan",
                account.getOwner()
        );

        assertEquals(
                5000,
                account.getBalance()
        );
    }

    @Test
    void createsSavingsAccount() {
        // Arrange
        AccountFactory factory =
                new DefaultAccountFactory();

        AccountNumber number =
                new AccountNumber(
                        "2222222222"
                );

        // Act
        BankAccount account =
                factory.create(
                        AccountType.SAVINGS,
                        number,
                        "Petr",
                        10000
                );

        // Assert
        assertInstanceOf(
                SavingsAccount.class,
                account
        );

        assertEquals(
                number,
                account.getId()
        );

        assertEquals(
                "Petr",
                account.getOwner()
        );

        assertEquals(
                10000,
                account.getBalance()
        );
    }

    @Test
    void createsCreditAccount() {
        // Arrange
        AccountFactory factory =
                new DefaultAccountFactory();

        AccountNumber number =
                new AccountNumber(
                        "3333333333"
                );

        // Act
        BankAccount account =
                factory.create(
                        AccountType.CREDIT,
                        number,
                        "Alex",
                        15000
                );

        // Assert
        assertInstanceOf(
                CreditAccount.class,
                account
        );

        assertEquals(
                number,
                account.getId()
        );

        assertEquals(
                "Alex",
                account.getOwner()
        );

        assertEquals(
                15000,
                account.getBalance()
        );
    }

    @Test
    void savingsAccountUsesDefaultMinimumBalance() {
        // Arrange
        AccountFactory factory =
                new DefaultAccountFactory();

        BankAccount account =
                factory.create(
                        AccountType.SAVINGS,
                        new AccountNumber(
                                "1111111111"
                        ),
                        "Ivan",
                        5000
                );

        // Act
        account.withdraw(
                4000
        );

        // Assert
        assertEquals(
                1000,
                account.getBalance()
        );

        assertThrows(
                InsufficientFundsException.class,
                () -> account.withdraw(1)
        );
    }

    @Test
    void creditAccountUsesDefaultCreditLimit() {
        // Arrange
        AccountFactory factory =
                new DefaultAccountFactory();

        BankAccount account =
                factory.create(
                        AccountType.CREDIT,
                        new AccountNumber(
                                "1111111111"
                        ),
                        "Ivan",
                        0
                );

        // Act
        account.withdraw(
                50000
        );

        // Assert
        assertEquals(
                -50000,
                account.getBalance()
        );

        assertThrows(
                InsufficientFundsException.class,
                () -> account.withdraw(1)
        );
    }

    @Test
    void nullAccountTypeIsRejected() {
        // Arrange
        AccountFactory factory =
                new DefaultAccountFactory();

        // Act + Assert
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> factory.create(
                                null,
                                new AccountNumber(
                                        "1111111111"
                                ),
                                "Ivan",
                                1000
                        )
                );

        assertEquals(
                "Account type must not be null",
                exception.getMessage()
        );
    }
}