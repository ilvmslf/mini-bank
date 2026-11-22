package bank.concurrency;

import bank.domain.AccountNumber;
import bank.domain.BankAccount;
import bank.domain.DebitAccount;

import java.util.List;

public class BankSimulation {

    private static final double INITIAL_BALANCE =
            100_000;

    private static final int OPERATIONS_PER_CLIENT =
            20_000;

    private static final long MONITOR_INTERVAL_MS =
            10;

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

        System.out.println(
                "INITIAL_TOTAL = "
                        + initialTotal
        );

        BankMonitor monitor =
                new BankMonitor(
                        bank,
                        initialTotal,
                        MONITOR_INTERVAL_MS
                );

        Thread monitorThread =
                new Thread(
                        monitor,
                        "bank-monitor"
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
         * Monitor запускаем ДО клиентов,
         * чтобы он наблюдал всю симуляцию.
         */
        monitorThread.start();

        client1.start();
        client2.start();
        client3.start();
        client4.start();
        client5.start();

        client1.join();
        client2.join();
        client3.join();
        client4.join();
        client5.join();

        /*
         * Клиенты закончили работу.
         * Теперь корректно останавливаем
         * фоновый monitor.
         */
        monitor.stop();

        monitorThread.interrupt();

        monitorThread.join();

        double finalTotal =
                bank.totalBalance();

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
                "Monitor checks: "
                        + monitor.getChecks()
        );

        System.out.println(
                "Monitor violations: "
                        + monitor.getViolations()
        );

        System.out.println();

        System.out.println(
                "Successful transfers: "
                        + bank.getSuccessfulTransfers()
        );

        System.out.println(
                "Rejected transfers: "
                        + bank.getRejectedTransfers()
        );

        System.out.println();

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