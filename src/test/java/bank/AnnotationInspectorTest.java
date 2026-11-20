package bank;

import bank.adapters.AnnotationInspector;
import bank.application.AccountService;
import bank.application.TransferApplicationService;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class AnnotationInspectorTest {

    @Test
    void inspectorPrintsClassNameAndAuditedStatus() {

        // Arrange
        AnnotationInspector inspector =
                new AnnotationInspector();

        ByteArrayOutputStream output =
                new ByteArrayOutputStream();

        PrintStream originalOut =
                System.out;

        try {
            System.setOut(
                    new PrintStream(output)
            );

            // Act
            inspector.inspect(
                    TransferApplicationService.class
            );

        } finally {
            System.setOut(
                    originalOut
            );
        }

        String result =
                output.toString();

        // Assert
        assertTrue(
                result.contains(
                        "Class: bank.application.TransferApplicationService"
                )
        );

        assertTrue(
                result.contains(
                        "Audited: true"
                )
        );
    }

    @Test
    void inspectorPrintsDeclaredMethods() {

        // Arrange
        AnnotationInspector inspector =
                new AnnotationInspector();

        ByteArrayOutputStream output =
                new ByteArrayOutputStream();

        PrintStream originalOut =
                System.out;

        try {
            System.setOut(
                    new PrintStream(output)
            );

            // Act
            inspector.inspect(
                    AccountService.class
            );

        } finally {
            System.setOut(
                    originalOut
            );
        }

        String result =
                output.toString();

        // Assert
        assertTrue(
                result.contains(
                        "Declared methods:"
                )
        );

        assertTrue(
                result.contains(
                        "open"
                )
        );

        assertTrue(
                result.contains(
                        "find"
                )
        );

        assertTrue(
                result.contains(
                        "close"
                )
        );

        assertTrue(
                result.contains(
                        "closeAccount"
                )
        );
    }

    @Test
    void inspectorPrintsRequiresRoleMethod() {

        // Arrange
        AnnotationInspector inspector =
                new AnnotationInspector();

        ByteArrayOutputStream output =
                new ByteArrayOutputStream();

        PrintStream originalOut =
                System.out;

        try {
            System.setOut(
                    new PrintStream(output)
            );

            // Act
            inspector.inspect(
                    AccountService.class
            );

        } finally {
            System.setOut(
                    originalOut
            );
        }

        String result =
                output.toString();

        // Assert
        assertTrue(
                result.contains(
                        "Methods with @RequiresRole:"
                )
        );

        assertTrue(
                result.contains(
                        "closeAccount -> ADMIN"
                )
        );
    }

    @Test
    void inspectorShowsNonAuditedClass() {

        // Arrange
        AnnotationInspector inspector =
                new AnnotationInspector();

        ByteArrayOutputStream output =
                new ByteArrayOutputStream();

        PrintStream originalOut =
                System.out;

        try {
            System.setOut(
                    new PrintStream(output)
            );

            // Act
            inspector.inspect(
                    AccountService.class
            );

        } finally {
            System.setOut(
                    originalOut
            );
        }

        String result =
                output.toString();

        // Assert
        assertTrue(
                result.contains(
                        "Audited: false"
                )
        );
    }

    @Test
    void inspectorRejectsNullType() {

        // Arrange
        AnnotationInspector inspector =
                new AnnotationInspector();

        // Act + Assert
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                inspector.inspect(
                                        null
                                )
                );

        assertEquals(
                "Type must not be null",
                exception.getMessage()
        );
    }
}