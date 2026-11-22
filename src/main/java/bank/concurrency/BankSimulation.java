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

        /*
         * 1. Создаём счета.
         */
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

        /*
         * Банковский инвариант.
         */
        double initialTotal =
                bank.totalBalance();

        int totalAttempts =
                CLIENT_COUNT
                        * OPERATIONS_PER_CLIENT;

        System.out.printf(
                "INITIAL_TOTAL = %.0f%n",
                initialTotal
        );

        System.out.println(
                "Expected attempts = "
                        + totalAttempts
        );

        /*
         * 2. Создаём фоновые задачи.
         */
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

        Thread statisticsThread =
                new Thread(
                        statisticsMonitor,
                        "statistics-monitor"
                );

        /*
         * 3. Создаём клиентские задачи.
         */
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
         * 4. Запускаем фоновые процессы.
         */
        bankMonitorThread.start();
        statisticsThread.start();

        /*
         * 5. Запускаем клиентов.
         */
        client1.start();
        client2.start();
        client3.start();
        client4.start();
        client5.start();

        /*
         * 6. Ждём завершения всех клиентов.
         */
        client1.join();
        client2.join();
        client3.join();
        client4.join();
        client5.join();

        /*
         * 7. Запрашиваем остановку
         * фоновых процессов.
         */
        bankMonitor.stop();
        statisticsMonitor.stop();

        /*
         * Будим monitor-thread,
         * если они находятся в sleep().
         */
        bankMonitorThread.interrupt();
        statisticsThread.interrupt();

        /*
         * 8. Ждём корректного завершения
         * фоновых потоков.
         */
        bankMonitorThread.join();
        statisticsThread.join();

        /*
         * 9. Получаем финальное состояние.
         */
        double finalTotal =
                bank.totalBalance();

        int successfulTransfers =
                bank.getSuccessfulTransfers();

        int failedTransfers =
                bank.getFailedTransfers();

        int countedAttempts =
                successfulTransfers
                        + failedTransfers;

        boolean invariantPreserved =
                Double.compare(
                        initialTotal,
                        finalTotal
                ) == 0;

        boolean statisticsCorrect =
                countedAttempts
                        == totalAttempts;

        boolean allWorkersStopped =
                !client1.isAlive()
                        && !client2.isAlive()
                        && !client3.isAlive()
                        && !client4.isAlive()
                        && !client5.isAlive()
                        && !bankMonitorThread.isAlive()
                        && !statisticsThread.isAlive();

        /*
         * 10. Финальный отчёт.
         */
        printFinalReport(
                initialTotal,
                finalTotal,
                successfulTransfers,
                failedTransfers,
                totalAttempts,
                invariantPreserved,
                statisticsCorrect,
                allWorkersStopped,
                bankMonitor,
                statisticsMonitor
        );
    }

    private static void printFinalReport(
            double initialTotal,
            double finalTotal,
            int successfulTransfers,
            int failedTransfers,
            int totalAttempts,
            boolean invariantPreserved,
            boolean statisticsCorrect,
            boolean allWorkersStopped,
            BankMonitor bankMonitor,
            StatisticsMonitor statisticsMonitor) {

        System.out.println();
        System.out.println(
                "=== FINAL REPORT ==="
        );

        System.out.printf(
                "Initial total: %.0f%n",
                initialTotal
        );

        System.out.printf(
                "Final total: %.0f%n",
                finalTotal
        );

        System.out.println(
                "Successful transfers: "
                        + successfulTransfers
        );

        System.out.println(
                "Failed transfers: "
                        + failedTransfers
        );

        System.out.println(
                "Total attempts: "
                        + totalAttempts
        );

        System.out.println(
                "Counted attempts: "
                        + (successfulTransfers
                        + failedTransfers)
        );

        System.out.println(
                "Statistics: "
                        + (statisticsCorrect
                        ? "OK"
                        : "ERROR")
        );

        System.out.println(
                "Bank invariant: "
                        + (invariantPreserved
                        ? "OK"
                        : "BROKEN")
        );

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

        if (allWorkersStopped) {

            System.out.println(
                    "All workers stopped."
            );

        } else {

            System.out.println(
                    "ERROR: some workers are still running."
            );
        }
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
}