package bank.domain;

public class CreditAccount
        extends BankAccount {

    private final double creditLimit;

    public CreditAccount(
            AccountNumber number,
            String owner,
            double initialBalance,
            double creditLimit) {

        super(
                number,
                owner,
                initialBalance
        );

        if (creditLimit < 0) {
            throw new IllegalArgumentException(
                    "Credit limit must not be negative"
            );
        }

        this.creditLimit =
                creditLimit;
    }

    @Override
    protected double getAvailableAmount() {
        return getBalance()
                + creditLimit;
    }
}