package bank;

import bank.concurrency.ConcurrentBank;
import bank.concurrency.TransferWorker;
import bank.domain.AccountNumber;
import bank.domain.BankAccount;
import bank.domain.DebitAccount;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BankInvariantTest {

    @Test
    void initialTotalIsFiveHundredThousand() {

        // Arrange
        ConcurrentBank bank =
                createBank();

        // Act
        double initialTotal =
                bank.totalBalance();

        // Assert
        assertEquals(
                500_000,
                initialTotal
        );
    }

    @Test
    void clientsFinishAndFinalTotalCanBeMeasured()
            throws InterruptedException {

        // Arrange
        ConcurrentBank bank =
                createBank();

        double initialTotal =
                bank.totalBalance();

        TransferWorker worker1 =
                new TransferWorker(
                        bank,
                        10_000,
                        1
                );

        TransferWorker worker2 =
                new TransferWorker(
                        bank,
                        10_000,
                        2
                );

        TransferWorker worker3 =
                new TransferWorker(
                        bank,
                        10_000,
                        3
                );

        TransferWorker worker4 =
                new TransferWorker(
                        bank,
                        10_000,
                        4
                );

        TransferWorker worker5 =
                new TransferWorker(
                        bank,
                        10_000,
                        5
                );

        Thread client1 =
                new Thread(worker1);

        Thread client2 =
                new Thread(worker2);

        Thread client3 =
                new Thread(worker3);

        Thread client4 =
                new Thread(worker4);

        Thread client5 =
                new Thread(worker5);

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
        assertFalse(
                client1.isAlive()
        );

        assertFalse(
                client2.isAlive()
        );

        assertFalse(
                client3.isAlive()
        );

        assertFalse(
                client4.isAlive()
        );

        assertFalse(
                client5.isAlive()
        );

        assertTrue(
                initialTotal > 0
        );

        assertTrue(
                finalTotal >= 0
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
}