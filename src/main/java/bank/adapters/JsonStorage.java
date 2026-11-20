package bank.adapters;

import bank.application.dto.AccountDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class JsonStorage {

    private final ObjectMapper mapper =
            new ObjectMapper();

    public void saveAccounts(
            Path path,
            List<AccountDto> accounts)
            throws IOException {

        if (path == null) {
            throw new IllegalArgumentException(
                    "Path must not be null"
            );
        }

        if (accounts == null) {
            throw new IllegalArgumentException(
                    "Accounts must not be null"
            );
        }

        Path parent =
                path.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        mapper.writerWithDefaultPrettyPrinter()
                .writeValue(
                        path.toFile(),
                        accounts
                );
    }

    public List<AccountDto> loadAccounts(
            Path path)
            throws IOException {

        if (path == null) {
            throw new IllegalArgumentException(
                    "Path must not be null"
            );
        }

        return mapper.readValue(
                path.toFile(),
                new TypeReference<List<AccountDto>>() {
                }
        );
    }
}