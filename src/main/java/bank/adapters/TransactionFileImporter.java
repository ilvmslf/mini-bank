package bank.adapters;

import bank.application.dto.TransactionDto;
import bank.domain.TransactionStatus;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class TransactionFileImporter {

    public List<TransactionDto> load(
            Path path)
            throws IOException {

        if (path == null) {
            throw new IllegalArgumentException(
                    "Path must not be null"
            );
        }

        List<String> lines =
                Files.readAllLines(
                        path,
                        StandardCharsets.UTF_8
                );

        if (lines.isEmpty()) {
            return List.of();
        }

        return lines.stream()
                .skip(1)
                .filter(line ->
                        !line.isBlank()
                )
                .map(this::parseLine)
                .toList();
    }

    private TransactionDto parseLine(
            String line) {

        String[] parts =
                line.split(";", -1);

        if (parts.length != 5) {
            throw new IllegalArgumentException(
                    "Invalid transaction line: "
                            + line
            );
        }

        try {
            long id =
                    Long.parseLong(
                            parts[0]
                    );

            String from =
                    parts[1];

            String to =
                    parts[2];

            double amount =
                    Double.parseDouble(
                            parts[3]
                    );

            TransactionStatus status =
                    TransactionStatus.valueOf(
                            parts[4]
                    );

            return new TransactionDto(
                    id,
                    from,
                    to,
                    amount,
                    status.name()
            );

        } catch (IllegalArgumentException exception) {

            throw new IllegalArgumentException(
                    "Invalid transaction line: "
                            + line,
                    exception
            );
        }
    }
}