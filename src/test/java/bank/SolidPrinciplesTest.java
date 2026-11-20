package bank;

import bank.domain.AccountNumber;
import bank.domain.BankAccount;
import bank.domain.CreditAccount;
import bank.domain.DebitAccount;
import bank.domain.InsufficientFundsException;
import bank.domain.SavingsAccount;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SolidPrinciplesTest {

    @Test
    void debitAccountCanBeUsedAsBankAccount() {
        // Arrange
        BankAccount account =
                new DebitAccount(
                        new AccountNumber(
                                "1111111111"
                        ),
                        "Ivan",
                        5000
                );

        // Act
        withdrawFromAccount(
                account,
                1000
        );

        // Assert
        assertEquals(
                4000,
                account.getBalance()
        );
    }

    @Test
    void savingsAccountCanBeUsedAsBankAccount() {
        // Arrange
        BankAccount account =
                new SavingsAccount(
                        new AccountNumber(
                                "2222222222"
                        ),
                        "Petr",
                        5000,
                        1000
                );

        // Act
        withdrawFromAccount(
                account,
                4000
        );

        // Assert
        assertEquals(
                1000,
                account.getBalance()
        );
    }

    @Test
    void creditAccountCanBeUsedAsBankAccount() {
        // Arrange
        BankAccount account =
                new CreditAccount(
                        new AccountNumber(
                                "3333333333"
                        ),
                        "Alex",
                        1000,
                        5000
                );

        // Act
        withdrawFromAccount(
                account,
                3000
        );

        // Assert
        assertEquals(
                -2000,
                account.getBalance()
        );
    }

    @Test
    void allAccountTypesSupportDeposit() {
        // Arrange
        BankAccount debit =
                new DebitAccount(
                        new AccountNumber(
                                "1111111111"
                        ),
                        "Ivan",
                        1000
                );

        BankAccount savings =
                new SavingsAccount(
                        new AccountNumber(
                                "2222222222"
                        ),
                        "Petr",
                        2000,
                        500
                );

        BankAccount credit =
                new CreditAccount(
                        new AccountNumber(
                                "3333333333"
                        ),
                        "Alex",
                        1000,
                        5000
                );

        // Act + Assert
        assertDoesNotThrow(
                () -> debit.deposit(500)
        );

        assertDoesNotThrow(
                () -> savings.deposit(500)
        );

        assertDoesNotThrow(
                () -> credit.deposit(500)
        );
    }

    @Test
    void allAccountTypesSupportWithdraw() {
        // Arrange
        BankAccount debit =
                new DebitAccount(
                        new AccountNumber(
                                "1111111111"
                        ),
                        "Ivan",
                        5000
                );

        BankAccount savings =
                new SavingsAccount(
                        new AccountNumber(
                                "2222222222"
                        ),
                        "Petr",
                        5000,
                        1000
                );

        BankAccount credit =
                new CreditAccount(
                        new AccountNumber(
                                "3333333333"
                        ),
                        "Alex",
                        1000,
                        5000
                );

        // Act + Assert
        assertDoesNotThrow(
                () -> debit.withdraw(1000)
        );

        assertDoesNotThrow(
                () -> savings.withdraw(1000)
        );

        assertDoesNotThrow(
                () -> credit.withdraw(1000)
        );
    }

    @Test
    void debitAccountRejectsWithdrawalAboveAvailableAmount() {
        // Arrange
        BankAccount account =
                new DebitAccount(
                        new AccountNumber(
                                "1111111111"
                        ),
                        "Ivan",
                        1000
                );

        // Act + Assert
        assertThrows(
                InsufficientFundsException.class,
                () -> account.withdraw(1001)
        );
    }

    @Test
    void savingsAccountRejectsWithdrawalBelowMinimumBalance() {
        // Arrange
        BankAccount account =
                new SavingsAccount(
                        new AccountNumber(
                                "2222222222"
                        ),
                        "Petr",
                        5000,
                        1000
                );

        // Act + Assert
        assertThrows(
                InsufficientFundsException.class,
                () -> account.withdraw(4001)
        );
    }

    @Test
    void creditAccountRejectsWithdrawalAboveCreditLimit() {
        // Arrange
        BankAccount account =
                new CreditAccount(
                        new AccountNumber(
                                "3333333333"
                        ),
                        "Alex",
                        1000,
                        5000
                );

        // Act + Assert
        assertThrows(
                InsufficientFundsException.class,
                () -> account.withdraw(6001)
        );
    }

    @Test
    void polymorphicCodeDoesNotNeedInstanceof() {
        // Arrange
        BankAccount[] accounts = {
                new DebitAccount(
                        new AccountNumber(
                                "1111111111"
                        ),
                        "Ivan",
                        5000
                ),

                new SavingsAccount(
                        new AccountNumber(
                                "2222222222"
                        ),
                        "Petr",
                        5000,
                        1000
                ),

                new CreditAccount(
                        new AccountNumber(
                                "3333333333"
                        ),
                        "Alex",
                        5000,
                        10000
                )
        };

        // Act
        for (BankAccount account : accounts) {
            withdrawFromAccount(
                    account,
                    1000
            );
        }

        // Assert
        assertEquals(
                4000,
                accounts[0].getBalance()
        );

        assertEquals(
                4000,
                accounts[1].getBalance()
        );

        assertEquals(
                4000,
                accounts[2].getBalance()
        );
    }

    private void withdrawFromAccount(
            BankAccount account,
            double amount) {

        account.withdraw(amount);
    }
}