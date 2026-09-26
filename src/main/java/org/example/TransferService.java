package org.example;

public class TransferService {

    public boolean transfer(
            BankAccount from,
            BankAccount to,
            double amount) {

        if (amount <= 0) {
            return false;
        }

        if (from == to) {
            return false;
        }

        boolean withdrawn = from.withdraw(amount);

        if (!withdrawn) {
            return false;
        }

        to.deposit(amount);
        return true;
    }
}