package org.example;

import java.util.Optional;

public interface AccountRepository {

    void save(BankAccount account);

    Optional<BankAccount> findById(
            AccountNumber number);

    boolean deleteById(
            AccountNumber number);
}