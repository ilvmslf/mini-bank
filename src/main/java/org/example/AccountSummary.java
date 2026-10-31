package org.example;

public record AccountSummary(
        AccountNumber number,
        String type,
        double balance) {
}