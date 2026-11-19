package org.example;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StreamApiTest {

    @Test
    void loopAndStreamReturnSameAccounts() {
        // Arrange
        List<BankAccount> accounts = List.of(
                new DebitAccount(
                        new AccountNumber("1111111111"),
                        "Ivan",
                        50000
                ),
                new DebitAccount(
                        new AccountNumber("2222222222"),
                        "Petr",
                        100000
                ),
                new CreditAccount(
                        new AccountNumber("3333333333"),
                        "Alex",
                        200000,
                        50000
                )
        );

        // Старый вариант - обычный цикл
        List<BankAccount> loopResult =
                new ArrayList<>();

        for (BankAccount account : accounts) {
            if (account.getBalance() >= 100_000) {
                loopResult.add(account);
            }
        }

        // Новый вариант - Stream API
        List<BankAccount> streamResult =
                accounts.stream()
                        .filter(account ->
                                account.getBalance() >= 100_000
                        )
                        .toList();

        // Assert
        assertEquals(2, streamResult.size());
        assertEquals(loopResult, streamResult);
    }
}