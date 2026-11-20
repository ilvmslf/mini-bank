package bank.ports;

import bank.domain.AccountNumber;
import bank.domain.BankAccount;

import java.util.Optional;

public interface AccountRepository {

    void save(BankAccount account);

    Optional<BankAccount> findById(
            AccountNumber number);

    boolean deleteById(
            AccountNumber number);
}