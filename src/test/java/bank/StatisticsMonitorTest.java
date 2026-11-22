package bank;

import bank.concurrency.ConcurrentBank;
import bank.concurrency.StatisticsMonitor;
import bank.concurrency.TransferWorker;
import bank.domain.AccountNumber;
import bank.domain.BankAccount;
import bank.domain.DebitAccount;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StatisticsMonitorTest {

    @Test
    void statisticsMonitorWorksWhileClientsRun()
            throws InterruptedException {

        // Arrange
        ConcurrentBank bank =
                createBank();

        StatisticsMonitor monitor =
                new StatisticsMonitor(
                        bank,
                        1
                );

        Thread monitorThread =
                new Thread(
                        monitor,
                        "statistics-monitor"
                );

        Thread client1 =
                createClient(
                        bank,
                        5000,
                        1
                );

        Thread client2 =
                createClient(
                        bank,
                        5000,
                        2
                );

        Thread client3 =
                createClient(
                        bank,
                        5000,
                        3
                );

        Thread client4 =
                createClient(
                        bank,
                        5000,
                        4
                );

        Thread client5 =
                createClient(
                        bank,
                        5000,
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
                monitor.getReports() > 0
        );

        assertEquals(
                25_000,
                bank.getSuccessfulTransfers()
                        + bank.getFailedTransfers()
        );

        assertEquals(
                500_000,
                bank.totalBalance()
        );
    }

    @Test
    void statisticsMonitorCanBeStoppedGracefully()
            throws InterruptedException {

        // Arrange
        ConcurrentBank bank =
                createBank();

        StatisticsMonitor monitor =
                new StatisticsMonitor(
                        bank,
                        10_000
                );

        Thread monitorThread =
                new Thread(
                        monitor,
                        "statistics-monitor"
                );

        // Act
        monitorThread.start();

        Thread.sleep(20);

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