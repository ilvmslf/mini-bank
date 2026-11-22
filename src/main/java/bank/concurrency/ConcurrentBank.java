package bank.concurrency;

import bank.domain.BankAccount;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrentBank {

    private final List<BankAccount> accounts;

    private final List<BankAccount> lockOrder;

    private final AtomicInteger successfulTransfers =
            new AtomicInteger();

    private final AtomicInteger failedTransfers =
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

        this.lockOrder =
                new ArrayList<>(accounts);

        this.lockOrder.sort(
                Comparator.comparing(
                        account ->
                                account.getId()
                                        .value()
                )
        );
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

        BankAccount first =
                getFirstLock(
                        from,
                        to
                );

        BankAccount second =
                first == from
                        ? to
                        : from;

        synchronized (first) {

            synchronized (second) {

                try {
                    from.withdraw(amount);
                    to.deposit(amount);

                    successfulTransfers
                            .incrementAndGet();

                } catch (RuntimeException exception) {

                    failedTransfers
                            .incrementAndGet();

                    throw exception;
                }
            }
        }
    }

    private BankAccount getFirstLock(
            BankAccount firstAccount,
            BankAccount secondAccount) {

        String firstNumber =
                firstAccount
                        .getId()
                        .value();

        String secondNumber =
                secondAccount
                        .getId()
                        .value();

        if (firstNumber.compareTo(
                secondNumber
        ) < 0) {

            return firstAccount;
        }

        return secondAccount;
    }

    public double totalBalance() {

        return totalBalanceWithLocks(
                0
        );
    }

    private double totalBalanceWithLocks(
            int index) {

        if (index == lockOrder.size()) {

            double total = 0;

            for (BankAccount account : accounts) {
                total += account.getBalance();
            }

            return total;
        }

        BankAccount account =
                lockOrder.get(index);

        synchronized (account) {

            return totalBalanceWithLocks(
                    index + 1
            );
        }
    }

    public List<Double> balanceSnapshot() {

        return balanceSnapshotWithLocks(
                0
        );
    }

    private List<Double> balanceSnapshotWithLocks(
            int index) {

        if (index == lockOrder.size()) {

            List<Double> result =
                    new ArrayList<>();

            for (BankAccount account : accounts) {
                result.add(
                        account.getBalance()
                );
            }

            return result;
        }

        BankAccount account =
                lockOrder.get(index);

        synchronized (account) {

            return balanceSnapshotWithLocks(
                    index + 1
            );
        }
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

    public int getFailedTransfers() {

        return failedTransfers.get();
    }

    /*
     * Оставляем старое имя для совместимости
     * с тестами предыдущих этапов.
     */
    public int getRejectedTransfers() {

        return getFailedTransfers();
    }
}