package org.example;

public class DebitAccount extends BankAccount {

    public DebitAccount(
            AccountNumber number,
            String owner,
            double initialBalance) {

        super(number, owner, initialBalance);
    }

    @Override
    protected double getAvailableAmount() {
        return getBalance();
    }
}