package org.example;

public class PrefixNotificationDecorator
        implements NotificationService {

    private final NotificationService delegate;
    private final String prefix;

    public PrefixNotificationDecorator(
            NotificationService delegate,
            String prefix) {

        if (delegate == null) {
            throw new IllegalArgumentException(
                    "Delegate must not be null"
            );
        }

        if (prefix == null) {
            throw new IllegalArgumentException(
                    "Prefix must not be null"
            );
        }

        this.delegate = delegate;
        this.prefix = prefix;
    }

    @Override
    public void notify(String message) {
        delegate.notify(
                prefix + message
        );
    }
}