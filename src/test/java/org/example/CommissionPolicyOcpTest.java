package org.example;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

public class CommissionPolicyOcpTest {

    @Test
    void fixedCommissionAlwaysReturnsSameValue() {
        // Arrange
        CommissionPolicy policy =
                new FixedCommissionPolicy(
                        100
                );

        // Act
        double smallTransfer =
                policy.calculate(1000);

        double largeTransfer =
                policy.calculate(50000);

        // Assert
        assertEquals(
                100,
                smallTransfer
        );

        assertEquals(
                100,
                largeTransfer
        );
    }

    @Test
    void progressiveCommissionUsesBasePercentBelowThreshold() {
        // Arrange
        CommissionPolicy policy =
                new ProgressiveCommissionPolicy(
                        10000,
                        1.0,
                        2.0
                );

        // Act
        double commission =
                policy.calculate(
                        5000
                );

        // Assert
        assertEquals(
                50,
                commission
        );
    }

    @Test
    void progressiveCommissionUsesHigherPercentAboveThreshold() {
        // Arrange
        CommissionPolicy policy =
                new ProgressiveCommissionPolicy(
                        10000,
                        1.0,
                        2.0
                );

        // Act
        double commission =
                policy.calculate(
                        15000
                );

        // Assert
        assertEquals(
                200,
                commission
        );
    }

    @Test
    void weekendCommissionIsChargedOnSaturday() {
        // Arrange
        Clock saturdayClock =
                Clock.fixed(
                        Instant.parse(
                                "2026-11-21T12:00:00Z"
                        ),
                        ZoneOffset.UTC
                );

        CommissionPolicy policy =
                new WeekendCommissionPolicy(
                        2.0,
                        saturdayClock
                );

        // Act
        double commission =
                policy.calculate(
                        10000
                );

        // Assert
        assertEquals(
                200,
                commission
        );
    }

    @Test
    void weekendCommissionIsNotChargedOnWeekday() {
        // Arrange
        Clock mondayClock =
                Clock.fixed(
                        Instant.parse(
                                "2026-11-23T12:00:00Z"
                        ),
                        ZoneOffset.UTC
                );

        CommissionPolicy policy =
                new WeekendCommissionPolicy(
                        2.0,
                        mondayClock
                );

        // Act
        double commission =
                policy.calculate(
                        10000
                );

        // Assert
        assertEquals(
                0,
                commission
        );
    }

    @Test
    void transferServiceWorksWithFixedCommissionWithoutModification() {
        // Arrange
        BankAccount from =
                new DebitAccount(
                        new AccountNumber("1111111111"),
                        "Ivan",
                        5000
                );

        BankAccount to =
                new DebitAccount(
                        new AccountNumber("2222222222"),
                        "Petr",
                        1000
                );

        TransferService transferService =
                new TransferService(
                        new FixedCommissionPolicy(
                                100
                        ),
                        message -> {
                        },
                        new BlockedAccountRegistry(),
                        new TransactionJournal()
                );

        // Act
        transferService.transfer(
                from,
                to,
                2000
        );

        // Assert
        assertEquals(
                2900,
                from.getBalance()
        );

        assertEquals(
                3000,
                to.getBalance()
        );
    }

    @Test
    void transferServiceWorksWithProgressiveCommissionWithoutModification() {
        // Arrange
        BankAccount from =
                new DebitAccount(
                        new AccountNumber("1111111111"),
                        "Ivan",
                        20000
                );

        BankAccount to =
                new DebitAccount(
                        new AccountNumber("2222222222"),
                        "Petr",
                        1000
                );

        TransferService transferService =
                new TransferService(
                        new ProgressiveCommissionPolicy(
                                10000,
                                1.0,
                                2.0
                        ),
                        message -> {
                        },
                        new BlockedAccountRegistry(),
                        new TransactionJournal()
                );

        // Act
        transferService.transfer(
                from,
                to,
                15000
        );

        // Assert
        assertEquals(
                4800,
                from.getBalance()
        );

        assertEquals(
                16000,
                to.getBalance()
        );
    }
}