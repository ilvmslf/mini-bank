package bank;

import bank.concurrency.ConcurrentBank;
import bank.domain.AccountNumber;
import bank.domain.BankAccount;
import bank.domain.DebitAccount;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DeadlockPreventionTest {

    @Test
    void oppositeTransfersFinishWithoutDeadlock()
            throws InterruptedException {

        // Arrange
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

        ConcurrentBank bank =
                new ConcurrentBank(
                        List.of(
                                accountA,
                                accountB
                        )
                );

        int operations =
                100_000;

        Thread aToB =
                new Thread(
                        () -> {

                            for (int i = 0;
                                 i < operations;
                                 i++) {

                                try {
                                    bank.transfer(
                                            accountA,
                                            accountB,
                                            1
                                    );

                                } catch (RuntimeException ignored) {
                                }
                            }
                        },
                        "A-to-B"
                );

        Thread bToA =
                new Thread(
                        () -> {

                            for (int i = 0;
                                 i < operations;
                                 i++) {

                                try {
                                    bank.transfer(
                                            accountB,
                                            accountA,
                                            1
                                    );

                                } catch (RuntimeException ignored) {
                                }
                            }
                        },
                        "B-to-A"
                );

        double initialTotal =
                bank.totalBalance();

        // Act
        aToB.start();
        bToA.start();

        aToB.join();
        bToA.join();

        double finalTotal =
                bank.totalBalance();

        // Assert
        assertFalse(
                aToB.isAlive()
        );

        assertFalse(
                bToA.isAlive()
        );

        assertEquals(
                initialTotal,
                finalTotal
        );

        assertEquals(
                200_000,
                finalTotal
        );
    }

    @Test
    void severalOppositeTransferThreadsFinish()
            throws InterruptedException {

        // Arrange
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

        ConcurrentBank bank =
                new ConcurrentBank(
                        List.of(
                                accountA,
                                accountB
                        )
                );

        Thread first =
                createTransferThread(
                        bank,
                        accountA,
                        accountB,
                        50_000
                );

        Thread second =
                createTransferThread(
                        bank,
                        accountB,
                        accountA,
                        50_000
                );

        Thread third =
                createTransferThread(
                        bank,
                        accountA,
                        accountB,
                        50_000
                );

        Thread fourth =
                createTransferThread(
                        bank,
                        accountB,
                        accountA,
                        50_000
                );

        double initialTotal =
                bank.totalBalance();

        // Act
        first.start();
        second.start();
        third.start();
        fourth.start();

        first.join();
        second.join();
        third.join();
        fourth.join();

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

        assertEquals(
                initialTotal,
                bank.totalBalance()
        );
    }

    private Thread createTransferThread(
            ConcurrentBank bank,
            BankAccount from,
            BankAccount to,
            int operations) {

        return new Thread(
                () -> {

                    for (int i = 0;
                         i < operations;
                         i++) {

                        try {
                            bank.transfer(
                                    from,
                                    to,
                                    1
                            );

                        } catch (RuntimeException ignored) {
                        }
                    }
                }
        );
    }
}