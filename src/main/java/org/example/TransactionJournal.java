package org.example;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TransactionJournal {

    private final List<Transaction> transactions =
            new ArrayList<>();

    public void add(Transaction transaction) {
        transactions.add(transaction);
    }

    public List<Transaction> findAll() {
        return new ArrayList<>(transactions);
    }

    public int size() {
        return transactions.size();
    }

    public void removeRejected() {
        Iterator<Transaction> iterator =
                transactions.iterator();

        while (iterator.hasNext()) {
            Transaction transaction =
                    iterator.next();

            if (transaction.status()
                    == TransactionStatus.REJECTED) {

                iterator.remove();
            }
        }
    }
}