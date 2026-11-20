package bank.application;

import bank.application.annotation.Audited;
import bank.domain.AccountNumber;
import bank.domain.BankAccount;
import bank.domain.CommissionPolicy;
import bank.domain.Transaction;
import bank.domain.TransactionStatus;
import bank.ports.AccountRepository;
import bank.ports.NotificationService;

@Audited
public class TransferApplicationService {

    private static final double TRANSFER_LIMIT =
            50000;

    private final AccountRepository accounts;
    private final CommissionPolicy commissionPolicy;
    private final NotificationService notificationService;

    private long nextTransactionId = 1;

    public TransferApplicationService(
            AccountRepository accounts,
            CommissionPolicy commissionPolicy,
            NotificationService notificationService) {

        this.accounts = accounts;
        this.commissionPolicy =
                commissionPolicy;
        this.notificationService =
                notificationService;
    }

    public Transaction transfer(
            AccountNumber fromNumber,
            AccountNumber toNumber,
            double amount) {

        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be positive"
            );
        }

        if (fromNumber.equals(toNumber)) {
            throw new IllegalArgumentException(
                    "Cannot transfer to the same account"
            );
        }

        if (amount > TRANSFER_LIMIT) {
            throw new IllegalArgumentException(
                    "Transfer limit exceeded"
            );
        }

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

        double commission =
                commissionPolicy.calculate(
                        amount
                );

        from.withdraw(
                amount + commission
        );

        to.deposit(amount);

        Transaction transaction =
                new Transaction(
                        nextTransactionId++,
                        from.getId(),
                        to.getId(),
                        amount,
                        TransactionStatus.SUCCESS
                );

        notificationService.notify(
                "Transfer "
                        + amount
                        + " completed"
        );

        return transaction;
    }
}