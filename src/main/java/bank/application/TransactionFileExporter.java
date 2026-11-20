package bank.adapters;

import bank.domain.Transaction;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class TransactionFileExporter {

    public void export(
            Path path,
            List<Transaction> transactions)
            throws IOException {

        if (path == null) {
            throw new IllegalArgumentException(
                    "Path must not be null"
            );
        }

        if (transactions == null) {
            throw new IllegalArgumentException(
                    "Transactions must not be null"
            );
        }

        Path parent =
                path.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        List<String> lines =
                Stream.concat(
                                Stream.of(
                                        "id;from;to;amount;status"
                                ),
                                transactions.stream()
                                        .map(this::toLine)
                        )
                        .toList();

        Files.write(
                path,
                lines,
                StandardCharsets.UTF_8
        );
    }

    private String toLine(
            Transaction transaction) {

        return transaction.id()
                + ";"
                + transaction.from().value()
                + ";"
                + transaction.to().value()
                + ";"
                + transaction.amount()
                + ";"
                + transaction.status().name();
    }
}