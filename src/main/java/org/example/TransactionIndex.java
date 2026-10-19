package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionIndex {

    private final Map<AccountNumber, List<Transaction>> byAccount =
            new HashMap<>();

    public void add(Transaction transaction) {
        AccountNumber number =
                transaction.account();

        List<Transaction> transactions =
                byAccount.get(number);

        if (transactions == null) {
            transactions = new ArrayList<>();

            byAccount.put(
                    number,
                    transactions
            );
        }

        transactions.add(transaction);
    }

    public List<Transaction> findByAccount(
            AccountNumber number) {

        List<Transaction> transactions =
                byAccount.get(number);

        if (transactions == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(transactions);
    }
}