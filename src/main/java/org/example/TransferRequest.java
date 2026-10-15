package org.example;

public record TransferRequest(
        AccountNumber from,
        AccountNumber to,
        double amount) {
}