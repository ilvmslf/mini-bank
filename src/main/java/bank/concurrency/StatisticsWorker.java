package bank.concurrency;

import java.util.concurrent.atomic.AtomicInteger;

public class StatisticsWorker
        implements Runnable {

    private final ConcurrentBank bank;

    private final AtomicInteger snapshots =
            new AtomicInteger();

    private volatile boolean running =
            true;

    public StatisticsWorker(
            ConcurrentBank bank) {

        this.bank = bank;
    }

    @Override
    public void run() {

        while (running
                && !Thread.currentThread()
                .isInterrupted()) {

            bank.balanceSnapshot();

            snapshots.incrementAndGet();

            try {
                Thread.sleep(10);

            } catch (InterruptedException exception) {

                Thread.currentThread()
                        .interrupt();

                break;
            }
        }
    }

    public void stop() {
        running = false;
    }

    public int getSnapshots() {
        return snapshots.get();
    }
}