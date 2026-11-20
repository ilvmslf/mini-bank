package org.example;

public class TransferApplicationService {

    private final AccountRepository accounts;
    private final TransferService transferService;

    public TransferApplicationService(
            AccountRepository accounts,
            TransferService transferService) {

        this.accounts = accounts;
        this.transferService = transferService;
    }

    public void transfer(
            AccountNumber fromNumber,
            AccountNumber toNumber,
            double amount) {

        BankAccount from =
                accounts.findById(fromNumber)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Source account not found"
                                )
                        );

        BankAccount to =
                accounts.findById(toNumber)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Target account not found"
                                )
                        );

        transferService.transfer(
                from,
                to,
                amount
        );
    }
}