package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryAccountRepository
        implements AccountRepository {

    private final Map<AccountNumber, BankAccount> data =
            new HashMap<>();

    @Override
    public void save(BankAccount account) {
        if (account == null) {
            throw new IllegalArgumentException(
                    "Account must not be null"
            );
        }

        data.put(
                account.getId(),
                account
        );
    }

    @Override
    public Optional<BankAccount> findById(
            AccountNumber number) {

        return Optional.ofNullable(
                data.get(number)
        );
    }

    @Override
    public boolean deleteById(
            AccountNumber number) {

        return data.remove(number) != null;
    }
}