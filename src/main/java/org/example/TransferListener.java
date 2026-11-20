package org.example;

@FunctionalInterface
public interface TransferListener {

    void onTransferCompleted(
            Transaction transaction);
}