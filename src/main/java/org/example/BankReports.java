package org.example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BankReports {

    public Map<AccountNumber, Integer> countByAccount(
            List<Transaction> transactions) {

        Map<AccountNumber, Integer> result =
                new HashMap<>();

        for (Transaction transaction : transactions) {

            AccountNumber number =
                    transaction.account();

            int old =
                    result.getOrDefault(
                            number,
                            0
                    );

            result.put(
                    number,
                    old + 1
            );
        }

        return result;
    }

    public Map<AccountNumber, Double> totalAmountByAccount(
            List<Transaction> transactions) {

        Map<AccountNumber, Double> result =
                new HashMap<>();

        for (Transaction transaction : transactions) {

            AccountNumber number =
                    transaction.account();

            double old =
                    result.getOrDefault(
                            number,
                            0.0
                    );

            result.put(
                    number,
                    old + transaction.amount()
            );
        }

        return result;
    }
}