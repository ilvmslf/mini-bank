package org.example;

public class Main {

    public static void main(String[] args) {

        BankAccount from = new DebitAccount(
                new AccountNumber("0000000001"),
                "Ivan",
                3000
        );

        BankAccount to = new DebitAccount(
                new AccountNumber("0000000002"),
                "Petr",
                2000
        );

        TransferService transferService = new TransferService(
                new NoCommission(),
                new ConsoleNotificationService()
        );

        try {
            transferService.transfer(from, to, 5000);

            System.out.println(
                    "Transfer completed"
            );

        } catch (InsufficientFundsException e) {

            System.out.println(
                    "Transfer failed: " + e.getMessage()
            );
        }

        System.out.println("From balance: " + from.getBalance());
        System.out.println("To balance: " + to.getBalance());
    }
}