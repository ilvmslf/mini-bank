package org.example;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ComparatorDemo {

    public static void main(String[] args) {

        List<BankAccount> accounts = new ArrayList<>();

        accounts.add(
                new DebitAccount(
                        new AccountNumber("0000000001"),
                        "Ivan",
                        5000
                )
        );

        accounts.add(
                new DebitAccount(
                        new AccountNumber("0000000002"),
                        "Petr",
                        1000
                )
        );

        accounts.add(
                new DebitAccount(
                        new AccountNumber("0000000003"),
                        "Alex",
                        3000
                )
        );

        // 1. Анонимный класс
        Comparator<BankAccount> byBalanceAnonymous =
                new Comparator<>() {
                    @Override
                    public int compare(
                            BankAccount a,
                            BankAccount b) {

                        return Double.compare(
                                a.getBalance(),
                                b.getBalance()
                        );
                    }
                };

        accounts.sort(byBalanceAnonymous);

        System.out.println(
                "Anonymous class:"
        );

        printBalances(accounts);

        // 2. Lambda
        Comparator<BankAccount> byBalanceLambda =
                (a, b) -> Double.compare(
                        a.getBalance(),
                        b.getBalance()
                );

        accounts.sort(byBalanceLambda);

        System.out.println(
                "Lambda:"
        );

        printBalances(accounts);

        // 3. Comparator factory + method reference
        Comparator<BankAccount> byBalanceMethodReference =
                Comparator.comparingDouble(
                        BankAccount::getBalance
                );

        accounts.sort(byBalanceMethodReference);

        System.out.println(
                "Method reference:"
        );

        printBalances(accounts);
    }

    private static void printBalances(
            List<BankAccount> accounts) {

        for (BankAccount account : accounts) {
            System.out.println(
                    account.getBalance()
            );
        }

        System.out.println();
    }
}