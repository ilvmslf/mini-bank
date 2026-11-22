package bank;

import bank.concurrency.BankMonitor;
import bank.concurrency.ConcurrentBank;
import bank.concurrency.TransferWorker;
import bank.domain.AccountNumber;
import bank.domain.BankAccount;
import bank.domain.DebitAccount;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BankMonitorTest {

    @Test
    void monitorChecksInvariantWhileClientsWork()
            throws InterruptedException {

        // Arrange
        ConcurrentBank bank =
                createBank();

        double expectedTotal =
                bank.totalBalance();

        BankMonitor monitor =
                new BankMonitor(
                        bank,
                        expectedTotal,
                        1
                );

        Thread monitorThread =
                new Thread(
                        monitor,
                        "test-monitor"
                );

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

        monitor.stop();
        monitorThread.interrupt();
        monitorThread.join();

        // Assert
        assertFalse(
                monitorThread.isAlive()
        );

        assertTrue(
                monitor.getChecks() > 0
        );

        assertEquals(
                0,
                monitor.getViolations()
        );

        assertEquals(
                expectedTotal,
                bank.totalBalance()
        );
    }

    @Test
    void monitorThreadStopsAfterInterrupt()
            throws InterruptedException {

        // Arrange
        ConcurrentBank bank =
                createBank();

        BankMonitor monitor =
                new BankMonitor(
                        bank,
                        500_000,
                        1000
                );

        Thread monitorThread =
                new Thread(
                        monitor
                );

        // Act
        monitorThread.start();

        monitor.stop();
        monitorThread.interrupt();

        monitorThread.join();

        // Assert
        assertFalse(
                monitorThread.isAlive()
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
            long seed) {

        return new Thread(
                new TransferWorker(
                        bank,
                        5000,
                        seed
                )
        );
    }
}