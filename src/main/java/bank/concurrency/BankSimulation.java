package bank.concurrency;

import bank.domain.AccountNumber;
import bank.domain.BankAccount;
import bank.domain.DebitAccount;

import java.util.List;

public class BankSimulation {

    private static final double INITIAL_BALANCE =
            100_000;

    private static final int CLIENT_COUNT =
            5;

    private static final int OPERATIONS_PER_CLIENT =
            20_000;

    private static final long BANK_MONITOR_INTERVAL_MS =
            10;

    private static final long STATISTICS_INTERVAL_MS =
            100;

    public static void main(String[] args)
            throws InterruptedException {

        BankAccount accountA =
                createAccount(
                        "1000000001",
                        "Client A"
                );

        BankAccount accountB =
                createAccount(
                        "1000000002",
                        "Client B"
                );

        BankAccount accountC =
                createAccount(
                        "1000000003",
                        "Client C"
                );

        BankAccount accountD =
                createAccount(
                        "1000000004",
                        "Client D"
                );

        BankAccount accountE =
                createAccount(
                        "1000000005",
                        "Client E"
                );

        ConcurrentBank bank =
                new ConcurrentBank(
                        List.of(
                                accountA,
                                accountB,
                                accountC,
                                accountD,
                                accountE
                        )
                );

        double initialTotal =
                bank.totalBalance();

        int totalAttempts =
                CLIENT_COUNT
                        * OPERATIONS_PER_CLIENT;

        System.out.println(
                "INITIAL_TOTAL = "
                        + initialTotal
        );

        System.out.println(
                "Expected attempts = "
                        + totalAttempts
        );

        BankMonitor bankMonitor =
                new BankMonitor(
                        bank,
                        initialTotal,
                        BANK_MONITOR_INTERVAL_MS
                );

        StatisticsMonitor statisticsMonitor =
                new StatisticsMonitor(
                        bank,
                        STATISTICS_INTERVAL_MS
                );

        Thread bankMonitorThread =
                new Thread(
                        bankMonitor,
                        "bank-monitor"
                );

        Thread statisticsMonitorThread =
                new Thread(
                        statisticsMonitor,
                        "statistics-monitor"
                );

        TransferWorker worker1 =
                new TransferWorker(
                        bank,
                        OPERATIONS_PER_CLIENT,
                        1
                );

        TransferWorker worker2 =
                new TransferWorker(
                        bank,
                        OPERATIONS_PER_CLIENT,
                        2
                );

        TransferWorker worker3 =
                new TransferWorker(
                        bank,
                        OPERATIONS_PER_CLIENT,
                        3
                );

        TransferWorker worker4 =
                new TransferWorker(
                        bank,
                        OPERATIONS_PER_CLIENT,
                        4
                );

        TransferWorker worker5 =
                new TransferWorker(
                        bank,
                        OPERATIONS_PER_CLIENT,
                        5
                );

        Thread client1 =
                new Thread(
                        worker1,
                        "client-1"
                );

        Thread client2 =
                new Thread(
                        worker2,
                        "client-2"
                );

        Thread client3 =
                new Thread(
                        worker3,
                        "client-3"
                );

        Thread client4 =
                new Thread(
                        worker4,
                        "client-4"
                );

        Thread client5 =
                new Thread(
                        worker5,
                        "client-5"
                );

        /*
         * Сначала запускаем оба monitor-thread.
         */
        bankMonitorThread.start();

        statisticsMonitorThread.start();

        /*
         * Затем пять клиентских потоков.
         */
        client1.start();
        client2.start();
        client3.start();
        client4.start();
        client5.start();

        /*
         * main ждёт всех клиентов.
         */
        client1.join();
        client2.join();
        client3.join();
        client4.join();
        client5.join();

        /*
         * Клиенты завершились.
         * Останавливаем оба фоновых процесса.
         */
        bankMonitor.stop();
        statisticsMonitor.stop();

        bankMonitorThread.interrupt();
        statisticsMonitorThread.interrupt();

        /*
         * Дожидаемся реального завершения
         * обоих служебных потоков.
         */
        bankMonitorThread.join();

        statisticsMonitorThread.join();

        double finalTotal =
                bank.totalBalance();

        int successfulTransfers =
                bank.getSuccessfulTransfers();

        int failedTransfers =
                bank.getFailedTransfers();

        int countedAttempts =
                successfulTransfers
                        + failedTransfers;

        System.out.println();
        System.out.println(
                "All clients finished."
        );

        System.out.println();

        System.out.println(
                "Final balances:"
        );

        printBalances(bank);

        System.out.println();

        System.out.println(
                "FINAL_TOTAL = "
                        + finalTotal
        );

        System.out.println(
                "Invariant preserved: "
                        + (Double.compare(
                        initialTotal,
                        finalTotal
                ) == 0)
        );

        System.out.println();

        System.out.println(
                "successfulTransfers = "
                        + successfulTransfers
        );

        System.out.println(
                "failedTransfers = "
                        + failedTransfers
        );

        System.out.println(
                "totalAttempts = "
                        + totalAttempts
        );

        System.out.println(
                "successful + failed = "
                        + countedAttempts
        );

        System.out.println(
                "Statistics correct: "
                        + (countedAttempts
                        == totalAttempts)
        );

        System.out.println();

        System.out.println(
                "Bank Monitor checks: "
                        + bankMonitor.getChecks()
        );

        System.out.println(
                "Bank Monitor violations: "
                        + bankMonitor.getViolations()
        );

        System.out.println(
                "Statistics reports: "
                        + statisticsMonitor.getReports()
        );

        System.out.println();

        System.out.println(
                "Bank Monitor alive: "
                        + bankMonitorThread.isAlive()
        );

        System.out.println(
                "Statistics Monitor alive: "
                        + statisticsMonitorThread.isAlive()
        );

        System.out.println(
                "All threads finished."
        );
    }

    private static BankAccount createAccount(
            String number,
            String owner) {

        return new DebitAccount(
                new AccountNumber(number),
                owner,
                INITIAL_BALANCE
        );
    }

    private static void printBalances(
            ConcurrentBank bank) {

        for (int i = 0;
             i < bank.accountCount();
             i++) {

            BankAccount account =
                    bank.getAccount(i);

            System.out.println(
                    account.getId().value()
                            + " = "
                            + account.getBalance()
            );
        }
    }
}