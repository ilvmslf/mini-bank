package org.example;

import java.util.ArrayList;
import java.util.List;

public class TransferEventPublisher {

    private final List<TransferListener> listeners =
            new ArrayList<>();

    public void subscribe(
            TransferListener listener) {

        if (listener == null) {
            throw new IllegalArgumentException(
                    "Listener must not be null"
            );
        }

        listeners.add(listener);
    }

    public void publish(
            Transaction transaction) {

        for (TransferListener listener : listeners) {
            listener.onTransferCompleted(
                    transaction
            );
        }
    }

    public int listenerCount() {
        return listeners.size();
    }
}