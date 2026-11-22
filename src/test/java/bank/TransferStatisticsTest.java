package bank;

import bank.concurrency.ConcurrentBank;
import bank.concurrency.TransferWorker;
import bank.domain.AccountNumber;
import bank.domain.BankAccount;
import bank.domain.DebitAccount;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TransferStatisticsTest {

    @Test
    void concurrentStatisticsDoNotLoseUpdates()
            throws InterruptedException {

        // Arrange
        ConcurrentBank bank =
                createBank();

        int operationsPerClient =
                10_000;

        int clientCount =
                5;

        int totalAttempts =
                operationsPerClient
                        * clientCount;

        Thread client1 =
                createClient(
                        bank,
                        operationsPerClient,
                        1
                );

        Thread client2 =
                createClient(
                        bank,
                        operationsPerClient,
                        2
                );

        Thread client3 =
                createClient(
                        bank,
                        operationsPerClient,
                        3
                );

        Thread client4 =
                createClient(
                        bank,
                        operationsPerClient,
                        4
                );

        Thread client5 =
                createClient(
                        bank,
                        operationsPerClient,
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

        int successful =
                bank.getSuccessfulTransfers();

        int failed =
                bank.getFailedTransfers();

        // Assert
        assertEquals(
                totalAttempts,
                successful + failed
        );

        assertEquals(
                50_000,
                successful + failed
        );
    }

    @Test
    void everyClientAttemptIsCountedOnce()
            throws InterruptedException {

        // Arrange
        ConcurrentBank bank =
                createBank();

        int operationCount =
                20_000;

        TransferWorker worker =
                new TransferWorker(
                        bank,
                        operationCount,
                        100
                );

        Thread client =
                new Thread(
                        worker
                );

        // Act
        client.start();
        client.join();

        // Assert
        assertEquals(
                operationCount,
                worker.getTotalOperations()
        );

        assertEquals(
                operationCount,
                bank.getSuccessfulTransfers()
                        + bank.getFailedTransfers()
        );

        assertEquals(
                worker.getSuccessfulOperations(),
                bank.getSuccessfulTransfers()
        );

        assertEquals(
                worker.getFailedOperations(),
                bank.getFailedTransfers()
        );
    }

    @Test
    void failedTransferIsCounted()
            throws InterruptedException {

        // Arrange
        BankAccount poorAccount =
                new DebitAccount(
                        new AccountNumber(
                                "1000000001"
                        ),
                        "Poor Client",
                        0
                );

        BankAccount richAccount =
                new DebitAccount(
                        new AccountNumber(
                                "1000000002"
                        ),
                        "Rich Client",
                        100_000
                );

        ConcurrentBank bank =
                new ConcurrentBank(
                        List.of(
                                poorAccount,
                                richAccount
                        )
                );

        // Act
        try {
            bank.transfer(
                    poorAccount,
                    richAccount,
                    1000
            );

        } catch (RuntimeException ignored) {
        }

        // Assert
        assertEquals(
                0,
                bank.getSuccessfulTransfers()
        );

        assertEquals(
                1,
                bank.getFailedTransfers()
        );

        assertEquals(
                1,
                bank.getSuccessfulTransfers()
                        + bank.getFailedTransfers()
        );
    }

    private ConcurrentBank createBank() {

        return new ConcurrentBank(
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
    }

    private BankAccount createAccount(
            String number) {

        return new DebitAccount(
                new AccountNumber(number),
                "Client",
                100_000
        );
    }

    private Thread createClient(
            ConcurrentBank bank,
            int operations,
            long seed) {

        return new Thread(
                new TransferWorker(
                        bank,
                        operations,
                        seed
                )
        );
    }
}