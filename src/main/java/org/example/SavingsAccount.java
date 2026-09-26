package org.example;

public class SavingsAccount extends BankAccount {

    private final double minimumBalance;

    public SavingsAccount(
            AccountNumber number,
            String owner,
            double initialBalance,
            double minimumBalance) {

        super(number, owner, initialBalance);

        if (minimumBalance < 0) {
            throw new IllegalArgumentException(
                    "Минимальный остаток не может быть отрицательным"
            );
        }

        this.minimumBalance = minimumBalance;
    }

    @Override
    public boolean withdraw(double amount) {

        if (amount <= 0) {
            return false;
        }

        if (getBalance() - amount < minimumBalance) {
            return false;
        }

        decreaseBalance(amount);

        return true;
    }
}