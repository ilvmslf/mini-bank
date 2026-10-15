package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TransferQueueTest {

    @Test
    void requestsAreReturnedInFifoOrder() {
        // Arrange
        TransferQueue queue =
                new TransferQueue();

        TransferRequest first =
                new TransferRequest(
                        new AccountNumber("0000000001"),
                        new AccountNumber("0000000002"),
                        1000
                );

        TransferRequest second =
                new TransferRequest(
                        new AccountNumber("0000000002"),
                        new AccountNumber("0000000003"),
                        2000
                );

        TransferRequest third =
                new TransferRequest(
                        new AccountNumber("0000000003"),
                        new AccountNumber("0000000004"),
                        3000
                );

        // Act
        queue.add(first);
        queue.add(second);
        queue.add(third);

        // Assert
        assertSame(first, queue.next());
        assertSame(second, queue.next());
        assertSame(third, queue.next());
    }

    @Test
    void peekDoesNotRemoveFirstRequest() {
        // Arrange
        TransferQueue queue =
                new TransferQueue();

        TransferRequest request =
                new TransferRequest(
                        new AccountNumber("0000000001"),
                        new AccountNumber("0000000002"),
                        1000
                );

        queue.add(request);

        // Act
        TransferRequest result =
                queue.peek();

        // Assert
        assertSame(request, result);
        assertEquals(1, queue.size());
        assertFalse(queue.isEmpty());
    }

    @Test
    void nextReturnsNullForEmptyQueue() {
        // Arrange
        TransferQueue queue =
                new TransferQueue();

        // Act
        TransferRequest result =
                queue.next();

        // Assert
        assertNull(result);
    }

    @Test
    void sizeDecreasesAfterRequestIsRemoved() {
        // Arrange
        TransferQueue queue =
                new TransferQueue();

        TransferRequest first =
                new TransferRequest(
                        new AccountNumber("0000000001"),
                        new AccountNumber("0000000002"),
                        1000
                );

        TransferRequest second =
                new TransferRequest(
                        new AccountNumber("0000000002"),
                        new AccountNumber("0000000003"),
                        2000
                );

        queue.add(first);
        queue.add(second);

        assertEquals(2, queue.size());

        // Act
        queue.next();

        // Assert
        assertEquals(1, queue.size());
    }

    @Test
    void queueIsEmptyAfterAllRequestsAreRemoved() {
        // Arrange
        TransferQueue queue =
                new TransferQueue();

        TransferRequest request =
                new TransferRequest(
                        new AccountNumber("0000000001"),
                        new AccountNumber("0000000002"),
                        1000
                );

        queue.add(request);

        // Act
        queue.next();

        // Assert
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
    }
}