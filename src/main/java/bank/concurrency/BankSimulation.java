package bank.concurrency;

import bank.domain.AccountNumber;
import bank.domain.BankAccount;
import bank.domain.DebitAccount;

import java.util.List;

public class BankSimulation {

    private static final double INITIAL_BALANCE =
            100_000;

    private static final double EXPECTED_TOTAL =
            500_000;

    public static void main(String[] args)
            throws InterruptedException {

        BankAccount accountA =
                new DebitAccount(
                        new AccountNumber(
                                "1000000001"
                        ),
                        "Client A",
                        INITIAL_BALANCE
                );

        BankAccount accountB =
                new DebitAccount(
                        new AccountNumber(
                                "1000000002"
                        ),
                        "Client B",
                        INITIAL_BALANCE
                );

        BankAccount accountC =
                new DebitAccount(
                        new AccountNumber(
                                "1000000003"
                        ),
                        "Client C",
                        INITIAL_BALANCE
                );

        BankAccount accountD =
                new DebitAccount(
                        new AccountNumber(
                                "1000000004"
                        ),
                        "Client D",
                        INITIAL_BALANCE
                );

        BankAccount accountE =
                new DebitAccount(
                        new AccountNumber(
                                "1000000005"
                        ),
                        "Client E",
                        INITIAL_BALANCE
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

        System.out.println(
                "Initial total: "
                        + initialTotal
        );

        InvariantMonitor monitor =
                new InvariantMonitor(
                        bank,
                        EXPECTED_TOTAL
                );

        StatisticsWorker statistics =
                new StatisticsWorker(
                        bank
                );

        Thread monitorThread =
                new Thread(
                        monitor,
                        "bank-monitor"
                );

        Thread statisticsThread =
                new Thread(
                        statistics,
                        "statistics-worker"
                );

        Thread client1 =
                new Thread(
                        new TransferWorker(
                                bank,
                                10_000,
                                1
                        ),
                        "client-1"
                );

        Thread client2 =
                new Thread(
                        new TransferWorker(
                                bank,
                                10_000,
                                2
                        ),
                        "client-2"
                );

        Thread client3 =
                new Thread(
                        new TransferWorker(
                                bank,
                                10_000,
                                3
                        ),
                        "client-3"
                );

        Thread client4 =
                new Thread(
                        new TransferWorker(
                                bank,
                                10_000,
                                4
                        ),
                        "client-4"
                );

        monitorThread.start();
        statisticsThread.start();

        client1.start();
        client2.start();
        client3.start();
        client4.start();

        client1.join();
        client2.join();
        client3.join();
        client4.join();

        monitor.stop();
        statistics.stop();

        monitorThread.interrupt();
        statisticsThread.interrupt();

        monitorThread.join();
        statisticsThread.join();

        double finalTotal =
                bank.totalBalance();

        System.out.println(
                "Final total: "
                        + finalTotal
        );

        System.out.println(
                "Successful transfers: "
                        + bank.getSuccessfulTransfers()
        );

        System.out.println(
                "Rejected transfers: "
                        + bank.getRejectedTransfers()
        );

        System.out.println(
                "Invariant checks: "
                        + monitor.getChecks()
        );

        System.out.println(
                "Invariant violations: "
                        + monitor.getViolations()
        );

        System.out.println(
                "Statistics snapshots: "
                        + statistics.getSnapshots()
        );

        System.out.println(
                "All threads finished."
        );
    }
}