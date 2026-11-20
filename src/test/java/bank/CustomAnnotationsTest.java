package bank;

import bank.application.AccountService;
import bank.application.TransferApplicationService;
import bank.application.annotation.Audited;
import bank.application.annotation.RequiresRole;
import bank.domain.AccountNumber;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class CustomAnnotationsTest {

    @Test
    void transferApplicationServiceIsAudited() {
        // Act
        boolean audited =
                TransferApplicationService.class
                        .isAnnotationPresent(
                                Audited.class
                        );

        // Assert
        assertTrue(audited);
    }

    @Test
    void auditedAnnotationIsAvailableAtRuntime() {
        // Act
        Audited annotation =
                TransferApplicationService.class
                        .getAnnotation(
                                Audited.class
                        );

        // Assert
        assertNotNull(annotation);
    }

    @Test
    void closeAccountRequiresAdminRole()
            throws NoSuchMethodException {

        // Arrange
        Method method =
                AccountService.class
                        .getDeclaredMethod(
                                "closeAccount",
                                AccountNumber.class
                        );

        // Act
        RequiresRole annotation =
                method.getAnnotation(
                        RequiresRole.class
                );

        // Assert
        assertNotNull(annotation);

        assertEquals(
                "ADMIN",
                annotation.value()
        );
    }

    @Test
    void closeMethodDoesNotRequireRole()
            throws NoSuchMethodException {

        // Arrange
        Method method =
                AccountService.class
                        .getDeclaredMethod(
                                "close",
                                AccountNumber.class
                        );

        // Act
        boolean annotated =
                method.isAnnotationPresent(
                        RequiresRole.class
                );

        // Assert
        assertFalse(annotated);
    }
}