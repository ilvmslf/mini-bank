package bank;

import bank.adapters.TransactionFileImporter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionFileStatisticsTest {

    @TempDir
    Path tempDirectory;

    @Test
    void countSuccessfulCountsSuccessfulTransactions()
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
                        "2;2222222222;3333333333;500.0;REJECTED",
                        "3;3333333333;4444444444;2000.0;SUCCESS",
                        "4;4444444444;5555555555;300.0;REJECTED",
                        "5;5555555555;6666666666;7000.0;SUCCESS"
                ),
                StandardCharsets.UTF_8
        );

        TransactionFileImporter importer =
                new TransactionFileImporter();

        // Act
        long result =
                importer.countSuccessful(
                        path
                );

        // Assert
        assertEquals(
                3,
                result
        );
    }

    @Test
    void countSuccessfulReturnsZeroWhenThereAreNoSuccessfulTransactions()
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
                        "1;1111111111;2222222222;1000.0;REJECTED",
                        "2;2222222222;3333333333;500.0;REJECTED"
                ),
                StandardCharsets.UTF_8
        );

        TransactionFileImporter importer =
                new TransactionFileImporter();

        // Act
        long result =
                importer.countSuccessful(
                        path
                );

        // Assert
        assertEquals(
                0,
                result
        );
    }

    @Test
    void countSuccessfulWorksWithHeaderOnlyFile()
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
        long result =
                importer.countSuccessful(
                        path
                );

        // Assert
        assertEquals(
                0,
                result
        );
    }

    @Test
    void countSuccessfulWorksWithLargeFile()
            throws IOException {

        // Arrange
        Path path =
                tempDirectory.resolve(
                        "large-transactions.txt"
                );

        List<String> lines =
                new ArrayList<>();

        lines.add(
                "id;from;to;amount;status"
        );

        for (int i = 1; i <= 10000; i++) {

            String status =
                    i % 2 == 0
                            ? "SUCCESS"
                            : "REJECTED";

            lines.add(
                    i
                            + ";1111111111"
                            + ";2222222222"
                            + ";1000.0;"
                            + status
            );
        }

        Files.write(
                path,
                lines,
                StandardCharsets.UTF_8
        );

        TransactionFileImporter importer =
                new TransactionFileImporter();

        // Act
        long result =
                importer.countSuccessful(
                        path
                );

        // Assert
        assertEquals(
                5000,
                result
        );
    }
}