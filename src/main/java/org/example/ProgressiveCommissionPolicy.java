package org.example;

public final class ProgressiveCommissionPolicy
        implements CommissionPolicy {

    private final double threshold;
    private final double basePercent;
    private final double highPercent;

    public ProgressiveCommissionPolicy(
            double threshold,
            double basePercent,
            double highPercent) {

        if (threshold < 0) {
            throw new IllegalArgumentException(
                    "Threshold must not be negative"
            );
        }

        if (basePercent < 0 || highPercent < 0) {
            throw new IllegalArgumentException(
                    "Percent must not be negative"
            );
        }

        this.threshold = threshold;
        this.basePercent = basePercent;
        this.highPercent = highPercent;
    }

    @Override
    public double calculate(double amount) {

        if (amount <= threshold) {
            return amount
                    * basePercent
                    / 100;
        }

        double basePart =
                threshold
                        * basePercent
                        / 100;

        double highPart =
                (amount - threshold)
                        * highPercent
                        / 100;

        return basePart + highPart;
    }
}