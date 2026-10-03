package org.example;

import java.util.Objects;

public abstract class BankAccount
        implements Identifiable<AccountNumber> {

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
                    "Начальный баланс не может быть отрицательным"
            );
        }

        this.balance = initialBalance;
    }

    @Override
    public AccountNumber getId() {
        return number;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be positive"
            );
        }

        increaseBalance(amount);
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

        decreaseBalance(amount);
    }

    public double getBalance() {
        return balance;
    }

    protected abstract double getAvailableAmount();

    protected void increaseBalance(double amount) {
        balance += amount;
    }

    protected void decreaseBalance(double amount) {
        balance -= amount;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{\n" +
                " number='" + number.value() + "',\n" +
                " owner='" + owner + "',\n" +
                " balance=" + balance + "\n" +
                "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof BankAccount)) {
            return false;
        }

        BankAccount other = (BankAccount) obj;

        return Objects.equals(number, other.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }
}