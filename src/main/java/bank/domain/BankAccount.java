package bank.domain;

import java.util.Objects;

public abstract class BankAccount {

    private final AccountNumber number;
    private final String owner;
    private double balance;

    protected BankAccount(
            AccountNumber number,
            String owner,
            double initialBalance) {

        this.number = number;
        this.owner = owner;

        if (initialBalance < 0) {
            throw new IllegalArgumentException(
                    "Initial balance must not be negative"
            );
        }

        this.balance = initialBalance;
    }

    public AccountNumber getId() {
        return number;
    }

    public String getOwner() {
        return owner;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be positive"
            );
        }

        balance += amount;
    }

    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be positive"
            );
        }

        if (amount > getAvailableAmount()) {
            throw new InsufficientFundsException(
                    "Insufficient funds"
            );
        }

        balance -= amount;
    }

    protected abstract double getAvailableAmount();

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof BankAccount other)) {
            return false;
        }

        return Objects.equals(
                number,
                other.number
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }
}