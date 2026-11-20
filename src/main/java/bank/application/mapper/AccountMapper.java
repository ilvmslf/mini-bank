package bank.application.mapper;

import bank.application.dto.AccountDto;
import bank.domain.BankAccount;

public final class AccountMapper {

    private AccountMapper() {
    }

    public static AccountDto fromDomain(
            BankAccount account) {

        if (account == null) {
            throw new IllegalArgumentException(
                    "Account must not be null"
            );
        }

        return new AccountDto(
                account.getId().value(),
                account.getOwner(),
                account.getBalance(),
                account.getClass().getSimpleName()
        );
    }
}