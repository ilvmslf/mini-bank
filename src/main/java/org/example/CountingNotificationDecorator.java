package org.example;

public class CountingNotificationDecorator
        implements NotificationService {

    private final NotificationService delegate;
    private int count;

    public CountingNotificationDecorator(
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
        count++;

        delegate.notify(message);
    }

    public int getCount() {
        return count;
    }
}