package bank.application;

import bank.domain.AccountNumber;
import bank.domain.Transaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BankReportService {

    public Map<AccountNumber, Integer> countByAccount(
            List<Transaction> transactions) {

        Map<AccountNumber, Integer> result =
                new HashMap<>();

        for (Transaction transaction : transactions) {

            AccountNumber number =
                    transaction.from();

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
                    transaction.from();

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