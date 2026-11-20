package org.example;

public class DefaultAccountFactory
        implements AccountFactory {

    private static final double DEFAULT_SAVINGS_MINIMUM_BALANCE =
            1000;

    private static final double DEFAULT_CREDIT_LIMIT =
            50000;

    @Override
    public BankAccount create(
            AccountType type,
            AccountNumber number,
            String owner,
            double initialBalance) {

        if (type == null) {
            throw new IllegalArgumentException(
                    "Account type must not be null"
            );
        }

        return switch (type) {

            case DEBIT ->
                    new DebitAccount(
                            number,
                            owner,
                            initialBalance
                    );

            case SAVINGS ->
                    new SavingsAccount(
                            number,
                            owner,
                            initialBalance,
                            DEFAULT_SAVINGS_MINIMUM_BALANCE
                    );

            case CREDIT ->
                    new CreditAccount(
                            number,
                            owner,
                            initialBalance,
                            DEFAULT_CREDIT_LIMIT
                    );
        };
    }
}