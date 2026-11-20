package bank.domain;

public record Transaction(
        long id,
        AccountNumber from,
        AccountNumber to,
        double amount,
        TransactionStatus status) {
}