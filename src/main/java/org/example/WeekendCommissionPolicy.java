package org.example;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;

public final class WeekendCommissionPolicy
        implements CommissionPolicy {

    private final double percent;
    private final Clock clock;

    public WeekendCommissionPolicy(
            double percent,
            Clock clock) {

        if (percent < 0) {
            throw new IllegalArgumentException(
                    "Percent must not be negative"
            );
        }

        if (clock == null) {
            throw new IllegalArgumentException(
                    "Clock must not be null"
            );
        }

        this.percent = percent;
        this.clock = clock;
    }

    @Override
    public double calculate(double amount) {

        LocalDate today =
                LocalDate.now(clock);

        DayOfWeek day =
                today.getDayOfWeek();

        boolean weekend =
                day == DayOfWeek.SATURDAY
                        || day == DayOfWeek.SUNDAY;

        if (!weekend) {
            return 0.0;
        }

        return amount
                * percent
                / 100;
    }
}