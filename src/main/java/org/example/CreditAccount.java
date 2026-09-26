package org.example;

public class CreditAccount extends BankAccount {

    private final double creditLimit;

    public CreditAccount(
            AccountNumber number,
            String owner,
            double initialBalance,
            double creditLimit) {

        super(number, owner, initialBalance);

        if (creditLimit < 0) {
            throw new IllegalArgumentException(
                    "Кредитный лимит не может быть отрицательным"
            );
        }

        this.creditLimit = creditLimit;
    }

    @Override
    protected double getAvailableAmount() {
        return getBalance() + creditLimit;
    }
}