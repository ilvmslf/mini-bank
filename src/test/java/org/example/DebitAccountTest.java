package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DebitAccountTest {

    @Test
    void initialBalanceIsSaved() {
        DebitAccount account = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                1000
        );

        assertEquals(1000, account.getBalance());
    }

    @Test
    void depositIncreasesBalance() {
        DebitAccount account = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                1000
        );

        account.deposit(500);

        assertEquals(1500, account.getBalance());
    }

    @Test
    void zeroDepositThrowsException() {
        DebitAccount account = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                1000
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> account.deposit(0)
        );
    }

    @Test
    void negativeDepositThrowsException() {
        DebitAccount account = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                1000
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> account.deposit(-500)
        );
    }

    @Test
    void withdrawDecreasesBalance() {
        DebitAccount account = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                1000
        );

        account.withdraw(400);

        assertEquals(600, account.getBalance());
    }

    @Test
    void cannotWithdrawMoreThanBalance() {
        DebitAccount account = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                1000
        );

        assertThrows(
                InsufficientFundsException.class,
                () -> account.withdraw(1500)
        );

        assertEquals(1000, account.getBalance());
    }

    @Test
    void zeroWithdrawThrowsException() {
        DebitAccount account = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                1000
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> account.withdraw(0)
        );

        assertEquals(1000, account.getBalance());
    }

    @Test
    void negativeWithdrawThrowsException() {
        DebitAccount account = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                1000
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> account.withdraw(-500)
        );

        assertEquals(1000, account.getBalance());
    }
}