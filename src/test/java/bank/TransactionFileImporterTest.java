package bank;

import bank.adapters.TransactionFileImporter;
import bank.application.dto.TransactionDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionFileImporterTest {

    @TempDir
    Path tempDirectory;

    @Test
    void importerLoadsTransactions()
            throws IOException {

        // Arrange
        Path path =
                tempDirectory.resolve(
                        "transactions.txt"
                );

        Files.write(
                path,
                List.of(
                        "id;from;to;amount;status",
                        "1;1111111111;2222222222;1000.0;SUCCESS",
                        "2;3333333333;4444444444;2500.5;REJECTED"
                ),
                StandardCharsets.UTF_8
        );

        TransactionFileImporter importer =
                new TransactionFileImporter();

        // Act
        List<TransactionDto> result =
                importer.load(path);

        // Assert
        assertEquals(
                2,
                result.size()
        );
    }

    @Test
    void importerParsesFirstTransactionCorrectly()
            throws IOException {

        // Arrange
        Path path =
                tempDirectory.resolve(
                        "transactions.txt"
                );

        Files.write(
                path,
                List.of(
                        "id;from;to;amount;status",
                        "1;1111111111;2222222222;1000.0;SUCCESS"
                ),
                StandardCharsets.UTF_8
        );

        TransactionFileImporter importer =
                new TransactionFileImporter();

        // Act
        List<TransactionDto> result =
                importer.load(path);

        TransactionDto transaction =
                result.get(0);

        // Assert
        assertEquals(
                1,
                transaction.id()
        );

        assertEquals(
                "1111111111",
                transaction.from()
        );

        assertEquals(
                "2222222222",
                transaction.to()
        );

        assertEquals(
                1000.0,
                transaction.amount()
        );

        assertEquals(
                "SUCCESS",
                transaction.status()
        );
    }

    @Test
    void importerParsesRejectedStatus()
            throws IOException {

        // Arrange
        Path path =
                tempDirectory.resolve(
                        "transactions.txt"
                );

        Files.write(
                path,
                List.of(
                        "id;from;to;amount;status",
                        "5;1111111111;2222222222;500.0;REJECTED"
                ),
                StandardCharsets.UTF_8
        );

        TransactionFileImporter importer =
                new TransactionFileImporter();

        // Act
        TransactionDto result =
                importer.load(path)
                        .get(0);

        // Assert
        assertEquals(
                "REJECTED",
                result.status()
        );
    }

    @Test
    void importerSkipsHeader() throws IOException {
        // Arrange
        Path path =
                tempDirectory.resolve(
                        "transactions.txt"
                );

        Files.write(
                path,
                List.of(
                        "id;from;to;amount;status",
                        "1;1111111111;2222222222;1000.0;SUCCESS"
                ),
                StandardCharsets.UTF_8
        );

        TransactionFileImporter importer =
                new TransactionFileImporter();

        // Act
        List<TransactionDto> result =
                importer.load(path);

        // Assert
        assertEquals(
                1,
                result.size()
        );
    }

    @Test
    void importerReturnsEmptyListForHeaderOnlyFile()
            throws IOException {

        // Arrange
        Path path =
                tempDirectory.resolve(
                        "transactions.txt"
                );

        Files.write(
                path,
                List.of(
                        "id;from;to;amount;status"
                ),
                StandardCharsets.UTF_8
        );

        TransactionFileImporter importer =
                new TransactionFileImporter();

        // Act
        List<TransactionDto> result =
                importer.load(path);

        // Assert
        assertTrue(
                result.isEmpty()
        );
    }

    @Test
    void importerRejectsInvalidAmount()
            throws IOException {

        // Arrange
        Path path =
                tempDirectory.resolve(
                        "transactions.txt"
                );

        Files.write(
                path,
                List.of(
                        "id;from;to;amount;status",
                        "1;1111111111;2222222222;abc;SUCCESS"
                ),
                StandardCharsets.UTF_8
        );

        TransactionFileImporter importer =
                new TransactionFileImporter();

        // Act
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                importer.load(path)
                );

        // Assert
        assertTrue(
                exception.getMessage()
                        .contains(
                                "Invalid transaction line"
                        )
        );
    }

    @Test
    void importerRejectsUnknownStatus()
            throws IOException {

        // Arrange
        Path path =
                tempDirectory.resolve(
                        "transactions.txt"
                );

        Files.write(
                path,
                List.of(
                        "id;from;to;amount;status",
                        "1;1111111111;2222222222;1000.0;UNKNOWN"
                ),
                StandardCharsets.UTF_8
        );

        TransactionFileImporter importer =
                new TransactionFileImporter();

        // Act
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                importer.load(path)
                );

        // Assert
        assertTrue(
                exception.getMessage()
                        .contains(
                                "Invalid transaction line"
                        )
        );
    }

    @Test
    void importerRejectsLineWithWrongFieldCount()
            throws IOException {

        // Arrange
        Path path =
                tempDirectory.resolve(
                        "transactions.txt"
                );

        Files.write(
                path,
                List.of(
                        "id;from;to;amount;status",
                        "1;1111111111;1000.0"
                ),
                StandardCharsets.UTF_8
        );

        TransactionFileImporter importer =
                new TransactionFileImporter();

        // Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        importer.load(path)
        );
    }
}