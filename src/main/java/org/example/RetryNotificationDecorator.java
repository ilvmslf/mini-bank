package org.example;

public class RetryNotificationDecorator
        implements NotificationService {

    private final NotificationService delegate;
    private final int maxAttempts;

    public RetryNotificationDecorator(
            NotificationService delegate,
            int maxAttempts) {

        if (delegate == null) {
            throw new IllegalArgumentException(
                    "Delegate must not be null"
            );
        }

        if (maxAttempts <= 0) {
            throw new IllegalArgumentException(
                    "Max attempts must be positive"
            );
        }

        this.delegate = delegate;
        this.maxAttempts = maxAttempts;
    }

    @Override
    public void notify(String message) {

        RuntimeException lastException = null;

        for (int attempt = 1;
             attempt <= maxAttempts;
             attempt++) {

            try {
                delegate.notify(message);
                return;
            } catch (RuntimeException exception) {
                lastException = exception;
            }
        }

        throw lastException;
    }
}