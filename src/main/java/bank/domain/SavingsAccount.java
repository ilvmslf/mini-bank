package bank.domain;

public class SavingsAccount
        extends BankAccount {

    private final double minimumBalance;

    public SavingsAccount(
            AccountNumber number,
            String owner,
            double initialBalance,
            double minimumBalance) {

        super(
                number,
                owner,
                initialBalance
        );

        if (minimumBalance < 0) {
            throw new IllegalArgumentException(
                    "Minimum balance must not be negative"
            );
        }

        this.minimumBalance =
                minimumBalance;
    }

    @Override
    protected double getAvailableAmount() {
        return getBalance()
                - minimumBalance;
    }
}