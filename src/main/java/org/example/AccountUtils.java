package org.example;

import java.util.Comparator;
import java.util.List;

public final class AccountUtils {

    private AccountUtils() {
    }

    public static double totalBalance(
            List<? extends BankAccount> accounts) {

        return accounts.stream()
                .mapToDouble(
                        BankAccount::getBalance
                )
                .sum();
    }

    public static void addDemoDebitAccounts(
            List<? super DebitAccount> target) {

        target.add(
                new DebitAccount(
                        new AccountNumber("0000000001"),
                        "Ivan",
                        1000
                )
        );

        target.add(
                new DebitAccount(
                        new AccountNumber("0000000002"),
                        "Petr",
                        2000
                )
        );
    }

    public static <T> void copy(
            List<? extends T> source,
            List<? super T> target) {

        source.stream()
                .forEach(value ->
                        target.add(value)
                );
    }

    public static <T extends BankAccount>
    T richest(List<T> accounts) {

        return accounts.stream()
                .max(
                        Comparator.comparingDouble(
                                BankAccount::getBalance
                        )
                )
                .orElse(null);
    }
}