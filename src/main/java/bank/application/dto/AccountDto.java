package bank.application.dto;

public record AccountDto(
        String number,
        String owner,
        double balance,
        String type) {
}