package bank;

import bank.concurrency.ConcurrentBank;
import bank.concurrency.TransferWorker;
import bank.domain.AccountNumber;
import bank.domain.BankAccount;
import bank.domain.DebitAccount;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BankAccountThreadSafetyTest {

    @Test
    void concurrentDepositsDoNotLoseUpdates()
            throws InterruptedException {

        // Arrange
        BankAccount account =
                new DebitAccount(
                        new AccountNumber(
                                "1000000001"
                        ),
                        "Client",
                        0
                );

        int threadCount = 5;
        int operationsPerThread = 10_000;

        Thread[] threads =
                new Thread[threadCount];

        for (int i = 0;
             i < threadCount;
             i++) {

            threads[i] =
                    new Thread(
                            () -> {

                                for (int j = 0;
                                     j < operationsPerThread;
                                     j++) {

                                    account.deposit(1);
                                }
                            }
                    );
        }

        // Act
        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // Assert
        assertEquals(
                50_000,
                account.getBalance()
        );
    }

    @Test
    void concurrentWithdrawalsDoNotLoseUpdates()
            throws InterruptedException {

        // Arrange
        BankAccount account =
                new DebitAccount(
                        new AccountNumber(
                                "1000000001"
                        ),
                        "Client",
                        50_000
                );

        int threadCount = 5;
        int operationsPerThread = 10_000;

        Thread[] threads =
                new Thread[threadCount];

        for (int i = 0;
             i < threadCount;
             i++) {

            threads[i] =
                    new Thread(
                            () -> {

                                for (int j = 0;
                                     j < operationsPerThread;
                                     j++) {

                                    account.withdraw(1);
                                }
                            }
                    );
        }

        // Act
        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // Assert
        assertEquals(
                0,
                account.getBalance()
        );
    }

    @Test
    void concurrentTransfersPreserveFinalBankTotal()
            throws InterruptedException {

        // Arrange
        ConcurrentBank bank =
                new ConcurrentBank(
                        List.of(
                                createAccount(
                                        "1000000001"
                                ),
                                createAccount(
                                        "1000000002"
                                ),
                                createAccount(
                                        "1000000003"
                                ),
                                createAccount(
                                        "1000000004"
                                ),
                                createAccount(
                                        "1000000005"
                                )
                        )
                );

        double initialTotal =
                bank.totalBalance();

        Thread client1 =
                createClient(
                        bank,
                        1
                );

        Thread client2 =
                createClient(
                        bank,
                        2
                );

        Thread client3 =
                createClient(
                        bank,
                        3
                );

        Thread client4 =
                createClient(
                        bank,
                        4
                );

        Thread client5 =
                createClient(
                        bank,
                        5
                );

        // Act
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

        double finalTotal =
                bank.totalBalance();

        // Assert
        assertEquals(
                500_000,
                initialTotal
        );

        assertEquals(
                initialTotal,
                finalTotal
        );
    }

    private Thread createClient(
            ConcurrentBank bank,
            long seed) {

        return new Thread(
                new TransferWorker(
                        bank,
                        10_000,
                        seed
                )
        );
    }

    private BankAccount createAccount(
            String number) {

        return new DebitAccount(
                new AccountNumber(number),
                "Client",
                100_000
        );
    }
}