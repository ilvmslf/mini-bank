package bank;

import bank.adapters.TransactionFileExporter;
import bank.domain.AccountNumber;
import bank.domain.Transaction;
import bank.domain.TransactionStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionFileExporterTest {

    @TempDir
    Path tempDirectory;

    @Test
    void exporterCreatesFile() throws IOException {
        // Arrange
        TransactionFileExporter exporter =
                new TransactionFileExporter();

        Path path =
                tempDirectory
                        .resolve("exports")
                        .resolve(
                                "transactions.txt"
                        );

        Transaction transaction =
                new Transaction(
                        1,
                        new AccountNumber(
                                "1111111111"
                        ),
                        new AccountNumber(
                                "2222222222"
                        ),
                        1000,
                        TransactionStatus.SUCCESS
                );

        // Act
        exporter.export(
                path,
                List.of(transaction)
        );

        // Assert
        assertTrue(
                Files.exists(path)
        );

        assertTrue(
                Files.isRegularFile(path)
        );
    }

    @Test
    void exporterCreatesMissingDirectory()
            throws IOException {

        // Arrange
        TransactionFileExporter exporter =
                new TransactionFileExporter();

        Path directory =
                tempDirectory
                        .resolve("data")
                        .resolve("exports");

        Path path =
                directory.resolve(
                        "transactions.txt"
                );

        Transaction transaction =
                new Transaction(
                        1,
                        new AccountNumber(
                                "1111111111"
                        ),
                        new AccountNumber(
                                "2222222222"
                        ),
                        1000,
                        TransactionStatus.SUCCESS
                );

        assertFalse(
                Files.exists(directory)
        );

        // Act
        exporter.export(
                path,
                List.of(transaction)
        );

        // Assert
        assertTrue(
                Files.exists(directory)
        );

        assertTrue(
                Files.exists(path)
        );
    }

    @Test
    void exporterWritesExpectedFormat()
            throws IOException {

        // Arrange
        TransactionFileExporter exporter =
                new TransactionFileExporter();

        Path path =
                tempDirectory.resolve(
                        "transactions.txt"
                );

        Transaction first =
                new Transaction(
                        1,
                        new AccountNumber(
                                "1111111111"
                        ),
                        new AccountNumber(
                                "2222222222"
                        ),
                        1000,
                        TransactionStatus.SUCCESS
                );

        Transaction second =
                new Transaction(
                        2,
                        new AccountNumber(
                                "3333333333"
                        ),
                        new AccountNumber(
                                "4444444444"
                        ),
                        2500.5,
                        TransactionStatus.REJECTED
                );

        // Act
        exporter.export(
                path,
                List.of(
                        first,
                        second
                )
        );

        List<String> lines =
                Files.readAllLines(
                        path,
                        StandardCharsets.UTF_8
                );

        // Assert
        assertEquals(
                3,
                lines.size()
        );

        assertEquals(
                "id;from;to;amount;status",
                lines.get(0)
        );

        assertEquals(
                "1;1111111111;2222222222;1000.0;SUCCESS",
                lines.get(1)
        );

        assertEquals(
                "2;3333333333;4444444444;2500.5;REJECTED",
                lines.get(2)
        );
    }

    @Test
    void exporterWritesOnlyHeaderForEmptyList()
            throws IOException {

        // Arrange
        TransactionFileExporter exporter =
                new TransactionFileExporter();

        Path path =
                tempDirectory.resolve(
                        "transactions.txt"
                );

        // Act
        exporter.export(
                path,
                List.of()
        );

        List<String> lines =
                Files.readAllLines(
                        path,
                        StandardCharsets.UTF_8
                );

        // Assert
        assertEquals(
                1,
                lines.size()
        );

        assertEquals(
                "id;from;to;amount;status",
                lines.get(0)
        );
    }
}