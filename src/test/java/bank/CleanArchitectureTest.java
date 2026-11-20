package bank;

import bank.adapters.InMemoryAccountRepository;
import bank.application.AccountService;
import bank.application.BankReportService;
import bank.application.TransferApplicationService;
import bank.domain.AccountNumber;
import bank.domain.BankAccount;
import bank.domain.DebitAccount;
import bank.domain.Transaction;
import bank.ports.AccountRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CleanArchitectureTest {

    @Test
    void accountServiceWorksThroughPort() {
        // Arrange
        AccountRepository repository =
                new InMemoryAccountRepository();

        AccountService accountService =
                new AccountService(
                        repository
                );

        BankAccount account =
                new DebitAccount(
                        new AccountNumber(
                                "1111111111"
                        ),
                        "Ivan",
                        1000
                );

        // Act
        accountService.open(account);

        // Assert
        assertSame(
                account,
                accountService
                        .find(account.getId())
                        .orElseThrow()
        );
    }

    @Test
    void transferApplicationServiceDependsOnPortsAndDomain() {
        // Arrange
        AccountRepository repository =
                new InMemoryAccountRepository();

        BankAccount from =
                new DebitAccount(
                        new AccountNumber(
                                "1111111111"
                        ),
                        "Ivan",
                        5000
                );

        BankAccount to =
                new DebitAccount(
                        new AccountNumber(
                                "2222222222"
                        ),
                        "Petr",
                        1000
                );

        repository.save(from);
        repository.save(to);

        TransferApplicationService service =
                new TransferApplicationService(
                        repository,
                        amount -> 0.0,
                        message -> {
                        }
                );

        // Act
        Transaction transaction =
                service.transfer(
                        from.getId(),
                        to.getId(),
                        2000
                );

        // Assert
        assertEquals(
                3000,
                from.getBalance()
        );

        assertEquals(
                3000,
                to.getBalance()
        );

        assertEquals(
                2000,
                transaction.amount()
        );
    }

    @Test
    void reportServiceWorksOnlyWithDomainData() {
        // Arrange
        BankReportService reports =
                new BankReportService();

        AccountNumber number =
                new AccountNumber(
                        "1111111111"
                );

        Transaction first =
                new Transaction(
                        bank.domain.TransactionType.TRANSFER,
                        number,
                        1000,
                        bank.domain.TransactionStatus.SUCCESS
                );

        Transaction second =
                new Transaction(
                        bank.domain.TransactionType.TRANSFER,
                        number,
                        2000,
                        bank.domain.TransactionStatus.SUCCESS
                );

        // Act
        Map<AccountNumber, Integer> result =
                reports.countByAccount(
                        List.of(
                                first,
                                second
                        )
                );

        // Assert
        assertEquals(
                2,
                result.get(number)
        );
    }

    @Test
    void adapterImplementsRepositoryPort() {
        // Arrange
        AccountRepository repository =
                new InMemoryAccountRepository();

        // Assert
        assertInstanceOf(
                InMemoryAccountRepository.class,
                repository
        );
    }
}