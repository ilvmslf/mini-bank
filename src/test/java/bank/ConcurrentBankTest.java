package bank;

import bank.concurrency.ConcurrentBank;
import bank.concurrency.InvariantMonitor;
import bank.concurrency.TransferWorker;
import bank.domain.AccountNumber;
import bank.domain.BankAccount;
import bank.domain.DebitAccount;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ConcurrentBankTest {

    @Test
    void concurrentTransfersPreserveTotalBalance()
            throws InterruptedException {

        // Arrange
        BankAccount a =
                createAccount(
                        "1000000001"
                );

        BankAccount b =
                createAccount(
                        "1000000002"
                );

        BankAccount c =
                createAccount(
                        "1000000003"
                );

        BankAccount d =
                createAccount(
                        "1000000004"
                );

        BankAccount e =
                createAccount(
                        "1000000005"
                );

        ConcurrentBank bank =
                new ConcurrentBank(
                        List.of(
                                a,
                                b,
                                c,
                                d,
                                e
                        )
                );

        double expectedTotal =
                500_000;

        InvariantMonitor monitor =
                new InvariantMonitor(
                        bank,
                        expectedTotal
                );

        Thread monitorThread =
                new Thread(
                        monitor
                );

        Thread first =
                new Thread(
                        new TransferWorker(
                                bank,
                                2000,
                                1
                        )
                );

        Thread second =
                new Thread(
                        new TransferWorker(
                                bank,
                                2000,
                                2
                        )
                );

        Thread third =
                new Thread(
                        new TransferWorker(
                                bank,
                                2000,
                                3
                        )
                );

        Thread fourth =
                new Thread(
                        new TransferWorker(
                                bank,
                                2000,
                                4
                        )
                );

        // Act
        monitorThread.start();

        first.start();
        second.start();
        third.start();
        fourth.start();

        first.join();
        second.join();
        third.join();
        fourth.join();

        monitor.stop();
        monitorThread.interrupt();
        monitorThread.join();

        // Assert
        assertEquals(
                expectedTotal,
                bank.totalBalance()
        );

        assertEquals(
                0,
                monitor.getViolations()
        );

        assertFalse(
                first.isAlive()
        );

        assertFalse(
                second.isAlive()
        );

        assertFalse(
                third.isAlive()
        );

        assertFalse(
                fourth.isAlive()
        );

        assertFalse(
                monitorThread.isAlive()
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