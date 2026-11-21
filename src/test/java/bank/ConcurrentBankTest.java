package bank;

import bank.concurrency.ConcurrentBank;
import bank.concurrency.TransferWorker;
import bank.domain.AccountNumber;
import bank.domain.BankAccount;
import bank.domain.DebitAccount;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ConcurrentBankTest {

    @Test
    void concurrentClientsFinishTheirWork()
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

        Thread first =
                new Thread(
                        new TransferWorker(
                                bank,
                                5000,
                                1
                        ),
                        "client-1"
                );

        Thread second =
                new Thread(
                        new TransferWorker(
                                bank,
                                5000,
                                2
                        ),
                        "client-2"
                );

        Thread third =
                new Thread(
                        new TransferWorker(
                                bank,
                                5000,
                                3
                        ),
                        "client-3"
                );

        Thread fourth =
                new Thread(
                        new TransferWorker(
                                bank,
                                5000,
                                4
                        ),
                        "client-4"
                );

        Thread fifth =
                new Thread(
                        new TransferWorker(
                                bank,
                                5000,
                                5
                        ),
                        "client-5"
                );

        // Act
        first.start();
        second.start();
        third.start();
        fourth.start();
        fifth.start();

        first.join();
        second.join();
        third.join();
        fourth.join();
        fifth.join();

        // Assert
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
                fifth.isAlive()
        );
    }

    @Test
    void initialBankTotalIsFiveHundredThousand() {

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

        // Act
        double total =
                bank.totalBalance();

        // Assert
        assertEquals(
                500_000,
                total
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