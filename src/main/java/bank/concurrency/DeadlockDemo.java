package bank.concurrency;

import bank.domain.AccountNumber;
import bank.domain.BankAccount;
import bank.domain.DebitAccount;

public class DeadlockDemo {

    private static volatile boolean thread1LockedA;
    private static volatile boolean thread2LockedB;

    public static void main(String[] args) {

        BankAccount accountA =
                new DebitAccount(
                        new AccountNumber(
                                "1000000001"
                        ),
                        "Client A",
                        100_000
                );

        BankAccount accountB =
                new DebitAccount(
                        new AccountNumber(
                                "1000000002"
                        ),
                        "Client B",
                        100_000
                );

        Thread thread1 =
                new Thread(
                        () ->
                                transferAToB(
                                        accountA,
                                        accountB
                                ),
                        "Thread-1"
                );

        Thread thread2 =
                new Thread(
                        () ->
                                transferBToA(
                                        accountA,
                                        accountB
                                ),
                        "Thread-2"
                );

        thread1.start();
        thread2.start();

        try {
            Thread.sleep(500);
        } catch (InterruptedException exception) {

            Thread.currentThread()
                    .interrupt();
        }

        System.out.println();
        System.out.println(
                "Thread-1 state: "
                        + thread1.getState()
        );

        System.out.println(
                "Thread-2 state: "
                        + thread2.getState()
        );

        System.out.println();
        System.out.println(
                "If both threads are BLOCKED, "
                        + "a deadlock has occurred."
        );
    }

    private static void transferAToB(
            BankAccount accountA,
            BankAccount accountB) {

        synchronized (accountA) {

            System.out.println(
                    "Thread 1 locked A"
            );

            thread1LockedA = true;

            while (!thread2LockedB) {
                // Ждём, пока Thread 2
                // захватит Account B.
            }

            System.out.println(
                    "Thread 1 waits for B"
            );

            synchronized (accountB) {

                accountA.withdraw(1);
                accountB.deposit(1);
            }
        }
    }

    private static void transferBToA(
            BankAccount accountA,
            BankAccount accountB) {

        synchronized (accountB) {

            System.out.println(
                    "Thread 2 locked B"
            );

            thread2LockedB = true;

            while (!thread1LockedA) {
                // Ждём, пока Thread 1
                // захватит Account A.
            }

            System.out.println(
                    "Thread 2 waits for A"
            );

            synchronized (accountA) {

                accountB.withdraw(1);
                accountA.deposit(1);
            }
        }
    }
}