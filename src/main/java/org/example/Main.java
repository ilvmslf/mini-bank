package org.example;

public class Main {

    public static void main(String[] args) {

        Transaction tx1 = new Transaction(
                TransactionType.DEPOSIT,
                new AccountNumber("1234567890"),
                5000,
                TransactionStatus.SUCCESS
        );

        Transaction tx2 = new Transaction(
                TransactionType.WITHDRAWAL,
                new AccountNumber("1234567890"),
                2000,
                TransactionStatus.SUCCESS
        );

        Transaction tx3 = new Transaction(
                TransactionType.TRANSFER,
                new AccountNumber("0987654321"),
                10000,
                TransactionStatus.REJECTED
        );

        System.out.println(tx1);
        System.out.println(tx2);
        System.out.println(tx3);
    }
}