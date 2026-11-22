package bank;

import bank.concurrency.ConcurrentBank;
import bank.domain.AccountNumber;
import bank.domain.BankAccount;
import bank.domain.DebitAccount;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AtomicTransferTest {

    @Test
    void oppositeTransfersDoNotDeadlock()
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
                new Thread(
                        () -> {

                            for (int i = 0;
                                 i < 10_000;
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

        Thread second =
                new Thread(
                        () -> {

                            for (int i = 0;
                                 i < 10_000;
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

        // Act
        first.start();
        second.start();

        first.join();
        second.join();

        // Assert
        assertFalse(
                first.isAlive()
        );

        assertFalse(
                second.isAlive()
        );

        assertEquals(
                200_000,
                bank.totalBalance()
        );
    }

    @Test
    void manyAtomicTransfersPreserveTotalBalance()
            throws InterruptedException {

        // Arrange
        BankAccount accountA =
                createAccount(
                        "1000000001"
                );

        BankAccount accountB =
                createAccount(
                        "1000000002"
                );

        BankAccount accountC =
                createAccount(
                        "1000000003"
                );

        BankAccount accountD =
                createAccount(
                        "1000000004"
                );

        BankAccount accountE =
                createAccount(
                        "1000000005"
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

        Thread first =
                createTransferThread(
                        bank,
                        accountA,
                        accountB
                );

        Thread second =
                createTransferThread(
                        bank,
                        accountB,
                        accountC
                );

        Thread third =
                createTransferThread(
                        bank,
                        accountC,
                        accountD
                );

        Thread fourth =
                createTransferThread(
                        bank,
                        accountD,
                        accountE
                );

        Thread fifth =
                createTransferThread(
                        bank,
                        accountE,
                        accountA
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
    void failedTransferDoesNotChangeBalances() {

        // Arrange
        BankAccount from =
                new DebitAccount(
                        new AccountNumber(
                                "1000000001"
                        ),
                        "Client A",
                        100
                );

        BankAccount to =
                new DebitAccount(
                        new AccountNumber(
                                "1000000002"
                        ),
                        "Client B",
                        100
                );

        ConcurrentBank bank =
                new ConcurrentBank(
                        List.of(
                                from,
                                to
                        )
                );

        // Act
        assertThrows(
                RuntimeException.class,
                () ->
                        bank.transfer(
                                from,
                                to,
                                1000
                        )
        );

        // Assert
        assertEquals(
                100,
                from.getBalance()
        );

        assertEquals(
                100,
                to.getBalance()
        );

        assertEquals(
                200,
                bank.totalBalance()
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

    private Thread createTransferThread(
            ConcurrentBank bank,
            BankAccount from,
            BankAccount to) {

        return new Thread(
                () -> {

                    for (int i = 0;
                         i < 10_000;
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