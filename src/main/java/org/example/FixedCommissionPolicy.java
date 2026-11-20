package org.example;

public final class FixedCommissionPolicy
        implements CommissionPolicy {

    private final double commission;

    public FixedCommissionPolicy(
            double commission) {

        if (commission < 0) {
            throw new IllegalArgumentException(
                    "Commission must not be negative"
            );
        }

        this.commission = commission;
    }

    @Override
    public double calculate(double amount) {
        return commission;
    }
}