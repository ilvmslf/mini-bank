package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BlockedAccountRegistryTest {

    @Test
    void blockedAccountIsBlocked() {
        // Arrange
        BlockedAccountRegistry registry =
                new BlockedAccountRegistry();

        AccountNumber number =
                new AccountNumber("0000000001");

        // Act
        registry.block(number);

        // Assert
        assertTrue(registry.isBlocked(number));
    }

    @Test
    void blockingSameAccountTwiceDoesNotCreateDuplicate() {
        // Arrange
        BlockedAccountRegistry registry =
                new BlockedAccountRegistry();

        AccountNumber number =
                new AccountNumber("0000000001");

        // Act
        registry.block(number);
        registry.block(number);

        // Assert
        assertEquals(1, registry.size());
    }

    @Test
    void unblockRemovesAccount() {
        // Arrange
        BlockedAccountRegistry registry =
                new BlockedAccountRegistry();

        AccountNumber number =
                new AccountNumber("0000000001");

        registry.block(number);

        // Act
        registry.unblock(number);

        // Assert
        assertFalse(registry.isBlocked(number));
        assertEquals(0, registry.size());
    }

    @Test
    void unknownAccountIsNotBlocked() {
        // Arrange
        BlockedAccountRegistry registry =
                new BlockedAccountRegistry();

        AccountNumber number =
                new AccountNumber("0000000001");

        // Act
        boolean result =
                registry.isBlocked(number);

        // Assert
        assertFalse(result);
    }
}