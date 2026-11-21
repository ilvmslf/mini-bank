package bank.concurrency;

import bank.domain.BankAccount;

import java.util.Random;

public class TransferWorker
        implements Runnable {

    private final ConcurrentBank bank;
    private final int operationCount;
    private final Random random;

    public TransferWorker(
            ConcurrentBank bank,
            int operationCount,
            long randomSeed) {

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
                            bank.accountCount()
                    );

            if (fromIndex == toIndex) {
                continue;
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
                            1000
                    ) + 1;

            try {
                bank.transfer(
                        from,
                        to,
                        amount
                );

            } catch (RuntimeException ignored) {

                // Недостаточно средств или
                // другая допустимая ошибка перевода.
                // Банк остаётся в корректном состоянии.
            }
        }
    }
}