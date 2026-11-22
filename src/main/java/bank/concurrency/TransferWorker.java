package bank.concurrency;

import bank.domain.BankAccount;

import java.util.Random;

public class TransferWorker
        implements Runnable {

    private final ConcurrentBank bank;
    private final int operationCount;
    private final Random random;

    private int successfulOperations;
    private int failedOperations;

    public TransferWorker(
            ConcurrentBank bank,
            int operationCount,
            long randomSeed) {

        if (bank == null) {
            throw new IllegalArgumentException(
                    "Bank must not be null"
            );
        }

        if (operationCount <= 0) {
            throw new IllegalArgumentException(
                    "Operation count must be positive"
            );
        }

        if (bank.accountCount() < 2) {
            throw new IllegalArgumentException(
                    "At least two accounts are required"
            );
        }

        this.bank = bank;
        this.operationCount =
                operationCount;

        this.random =
                new Random(randomSeed);
    }

    @Override
    public void run() {

        for (int i = 0;
             i < operationCount;
             i++) {

            if (Thread.currentThread()
                    .isInterrupted()) {

                return;
            }

            int fromIndex =
                    random.nextInt(
                            bank.accountCount()
                    );

            int toIndex =
                    random.nextInt(
                            bank.accountCount() - 1
                    );

            if (toIndex >= fromIndex) {
                toIndex++;
            }

            BankAccount from =
                    bank.getAccount(
                            fromIndex
                    );

            BankAccount to =
                    bank.getAccount(
                            toIndex
                    );

            double amount =
                    random.nextInt(
                            5000
                    ) + 1;

            try {
                bank.transfer(
                        from,
                        to,
                        amount
                );

                successfulOperations++;

            } catch (RuntimeException exception) {

                failedOperations++;
            }
        }
    }

    public int getSuccessfulOperations() {

        return successfulOperations;
    }

    public int getFailedOperations() {

        return failedOperations;
    }

    public int getTotalOperations() {

        return successfulOperations
                + failedOperations;
    }
}