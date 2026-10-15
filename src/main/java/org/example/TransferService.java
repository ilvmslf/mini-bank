package org.example;

public class TransferService {

    private static final double TRANSFER_LIMIT = 50000;

    private final CommissionPolicy commissionPolicy;
    private final NotificationService notificationService;
    private final BlockedAccountRegistry blockedAccounts;
    private final TransactionJournal journal;

    public TransferService(
            CommissionPolicy commissionPolicy,
            NotificationService notificationService,
            BlockedAccountRegistry blockedAccounts,
            TransactionJournal journal) {

        this.commissionPolicy = commissionPolicy;
        this.notificationService = notificationService;
        this.blockedAccounts = blockedAccounts;
        this.journal = journal;
    }

    public void transfer(
            BankAccount from,
            BankAccount to,
            double amount) {

        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be positive"
            );
        }

        if (from == to) {
            throw new IllegalArgumentException(
                    "Cannot transfer to the same account"
            );
        }

        if (amount > TRANSFER_LIMIT) {
            throw new TransferLimitExceededException(
                    "Transfer limit exceeded"
            );
        }

        if (blockedAccounts.isBlocked(from.getId())) {
            throw new IllegalStateException(
                    "Source account is blocked"
            );
        }

        double commission =
                commissionPolicy.calculate(amount);

        double totalAmount =
                amount + commission;

        from.withdraw(totalAmount);

        to.deposit(amount);

        journal.add(
                new Transaction(
                        TransactionType.TRANSFER,
                        from.getId(),
                        amount,
                        TransactionStatus.SUCCESS
                )
        );

        notificationService.notify(
                "Transfer " + amount + " completed"
        );
    }
}