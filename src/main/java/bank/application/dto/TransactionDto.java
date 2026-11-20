package bank.application.dto;

public record TransactionDto(
        long id,
        String from,
        String to,
        double amount,
        String status) {
}