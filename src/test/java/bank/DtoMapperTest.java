package bank;

import bank.application.dto.AccountDto;
import bank.application.dto.TransactionDto;
import bank.application.mapper.AccountMapper;
import bank.domain.AccountNumber;
import bank.domain.BankAccount;
import bank.domain.CreditAccount;
import bank.domain.DebitAccount;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DtoMapperTest {

    @Test
    void mapsDebitAccountToDto() {
        // Arrange
        BankAccount account =
                new DebitAccount(
                        new AccountNumber(
                                "1111111111"
                        ),
                        "Ivan",
                        5000
                );

        // Act
        AccountDto dto =
                AccountMapper.fromDomain(
                        account
                );

        // Assert
        assertEquals(
                "1111111111",
                dto.number()
        );

        assertEquals(
                "Ivan",
                dto.owner()
        );

        assertEquals(
                5000,
                dto.balance()
        );

        assertEquals(
                "DebitAccount",
                dto.type()
        );
    }

    @Test
    void mapsCreditAccountTypeCorrectly() {
        // Arrange
        BankAccount account =
                new CreditAccount(
                        new AccountNumber(
                                "2222222222"
                        ),
                        "Petr",
                        10000,
                        50000
                );

        // Act
        AccountDto dto =
                AccountMapper.fromDomain(
                        account
                );

        // Assert
        assertEquals(
                "2222222222",
                dto.number()
        );

        assertEquals(
                "Petr",
                dto.owner()
        );

        assertEquals(
                10000,
                dto.balance()
        );

        assertEquals(
                "CreditAccount",
                dto.type()
        );
    }

    @Test
    void dtoDoesNotExposeDomainObject() {
        // Arrange
        BankAccount account =
                new DebitAccount(
                        new AccountNumber(
                                "1111111111"
                        ),
                        "Ivan",
                        2500
                );

        // Act
        AccountDto dto =
                AccountMapper.fromDomain(
                        account
                );

        // Assert
        assertEquals(
                String.class,
                dto.number().getClass()
        );

        assertEquals(
                String.class,
                dto.type().getClass()
        );
    }

    @Test
    void mapperRejectsNullAccount() {
        // Act + Assert
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                AccountMapper.fromDomain(
                                        null
                                )
                );

        assertEquals(
                "Account must not be null",
                exception.getMessage()
        );
    }

    @Test
    void transactionDtoCanRepresentExternalTransaction() {
        // Arrange + Act
        TransactionDto dto =
                new TransactionDto(
                        1L,
                        "1111111111",
                        "2222222222",
                        5000,
                        "SUCCESS"
                );

        // Assert
        assertEquals(
                1L,
                dto.id()
        );

        assertEquals(
                "1111111111",
                dto.from()
        );

        assertEquals(
                "2222222222",
                dto.to()
        );

        assertEquals(
                5000,
                dto.amount()
        );

        assertEquals(
                "SUCCESS",
                dto.status()
        );
    }
}