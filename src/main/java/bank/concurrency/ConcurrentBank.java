package bank.concurrency;

import bank.domain.BankAccount;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrentBank {

    private final List<BankAccount> accounts;

    private final AtomicInteger successfulTransfers =
            new AtomicInteger();

    private final AtomicInteger rejectedTransfers =
            new AtomicInteger();

    public ConcurrentBank(
            List<BankAccount> accounts) {

        if (accounts == null) {
            throw new IllegalArgumentException(
                    "Accounts must not be null"
            );
        }

        if (accounts.isEmpty()) {
            throw new IllegalArgumentException(
                    "Bank must contain accounts"
            );
        }

        this.accounts =
                new ArrayList<>(accounts);
    }

    public void transfer(
            BankAccount from,
            BankAccount to,
            double amount) {

        if (from == null || to == null) {
            throw new IllegalArgumentException(
                    "Accounts must not be null"
            );
        }

        if (from == to) {
            throw new IllegalArgumentException(
                    "Cannot transfer to the same account"
            );
        }

        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be positive"
            );
        }

        BankAccount first;
        BankAccount second;

        if (from.getId()
                .value()
                .compareTo(
                        to.getId().value()
                ) < 0) {

            first = from;
            second = to;

        } else {

            first = to;
            second = from;
        }

        synchronized (first) {

            synchronized (second) {

                try {
                    from.withdraw(amount);
                    to.deposit(amount);

                    successfulTransfers
                            .incrementAndGet();

                } catch (RuntimeException exception) {

                    rejectedTransfers
                            .incrementAndGet();

                    throw exception;
                }
            }
        }
    }

    public double totalBalance() {

        double total = 0;

        for (BankAccount account : accounts) {
            total += account.getBalance();
        }

        return total;
    }

    public List<Double> balanceSnapshot() {

        List<Double> result =
                new ArrayList<>();

        for (BankAccount account : accounts) {
            result.add(
                    account.getBalance()
            );
        }

        return result;
    }

    public BankAccount getAccount(
            int index) {

        return accounts.get(index);
    }

    public int accountCount() {
        return accounts.size();
    }

    public int getSuccessfulTransfers() {

        return successfulTransfers.get();
    }

    public int getRejectedTransfers() {

        return rejectedTransfers.get();
    }
}