package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AccountNumberTest {

    @Test
    void validNumberIsCreated() {
        // Arrange + Act
        AccountNumber number =
                new AccountNumber("1234567890");

        // Assert
        assertEquals(
                "1234567890",
                number.value()
        );
    }

    @Test
    void shortNumberIsRejected() {
        // Arrange + Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> new AccountNumber("123")
        );
    }

    @Test
    void nullNumberIsRejected() {
        // Arrange + Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> new AccountNumber(null)
        );
    }

    @Test
    void emptyNumberIsRejected() {
        // Arrange + Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> new AccountNumber("")
        );
    }

    @Test
    void nineDigitNumberIsRejected() {
        // Arrange + Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> new AccountNumber("123456789")
        );
    }

    @Test
    void elevenDigitNumberIsRejected() {
        // Arrange + Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> new AccountNumber("12345678901")
        );
    }

    @Test
    void numberWithLettersIsRejected() {
        // Arrange + Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> new AccountNumber("12345A7890")
        );
    }
}