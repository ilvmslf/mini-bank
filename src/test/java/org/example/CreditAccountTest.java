package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CreditAccountTest {

    @Test
    void accountCanHaveNegativeBalance() {
        CreditAccount account = new CreditAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                1000,
                5000
        );

        account.withdraw(2000);

        assertEquals(-1000, account.getBalance());
    }

    @Test
    void canUseCreditLimit() {
        CreditAccount account = new CreditAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                1000,
                5000
        );

        account.withdraw(6000);

        assertEquals(-5000, account.getBalance());
    }

    @Test
    void cannotExceedCreditLimit() {
        CreditAccount account = new CreditAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                1000,
                5000
        );

        assertThrows(
                InsufficientFundsException.class,
                () -> account.withdraw(7000)
        );
    }

    @Test
    void failedWithdrawDoesNotChangeBalance() {
        CreditAccount account = new CreditAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                1000,
                5000
        );

        account.withdraw(4000);

        assertThrows(
                InsufficientFundsException.class,
                () -> account.withdraw(3000)
        );

        assertEquals(-3000, account.getBalance());
    }
}