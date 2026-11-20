package org.example;

public class LoggingNotificationDecorator
        implements NotificationService {

    private final NotificationService delegate;

    public LoggingNotificationDecorator(
            NotificationService delegate) {

        if (delegate == null) {
            throw new IllegalArgumentException(
                    "Delegate must not be null"
            );
        }

        this.delegate = delegate;
    }

    @Override
    public void notify(String message) {
        System.out.println(
                "[LOG] " + message
        );

        delegate.notify(message);
    }
}