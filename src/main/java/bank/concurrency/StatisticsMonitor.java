package bank.concurrency;

import java.util.concurrent.atomic.AtomicInteger;

public class StatisticsMonitor
        implements Runnable {

    private final ConcurrentBank bank;
    private final long checkIntervalMillis;

    private final AtomicInteger reports =
            new AtomicInteger();

    private volatile boolean running =
            true;

    public StatisticsMonitor(
            ConcurrentBank bank,
            long checkIntervalMillis) {

        if (bank == null) {
            throw new IllegalArgumentException(
                    "Bank must not be null"
            );
        }

        if (checkIntervalMillis <= 0) {
            throw new IllegalArgumentException(
                    "Check interval must be positive"
            );
        }

        this.bank = bank;
        this.checkIntervalMillis =
                checkIntervalMillis;
    }

    @Override
    public void run() {

        int previousOperations =
                getTotalOperations();

        long previousTime =
                System.nanoTime();

        while (running
                && !Thread.currentThread()
                .isInterrupted()) {

            try {
                Thread.sleep(
                        checkIntervalMillis
                );

            } catch (InterruptedException exception) {

                Thread.currentThread()
                        .interrupt();

                break;
            }

            if (!running) {
                break;
            }

            int currentOperations =
                    getTotalOperations();

            long currentTime =
                    System.nanoTime();

            int operationsDifference =
                    currentOperations
                            - previousOperations;

            long timeDifference =
                    currentTime
                            - previousTime;

            double seconds =
                    timeDifference
                            / 1_000_000_000.0;

            double operationsPerSecond;

            if (seconds > 0) {

                operationsPerSecond =
                        operationsDifference
                                / seconds;

            } else {

                operationsPerSecond = 0;
            }

            printStatistics(
                    operationsPerSecond
            );

            previousOperations =
                    currentOperations;

            previousTime =
                    currentTime;
        }
    }

    private int getTotalOperations() {

        return bank.getSuccessfulTransfers()
                + bank.getFailedTransfers();
    }

    private void printStatistics(
            double operationsPerSecond) {

        int successful =
                bank.getSuccessfulTransfers();

        int failed =
                bank.getFailedTransfers();

        reports.incrementAndGet();

        System.out.println(
                "[STATISTICS]"
        );

        System.out.println(
                "success = "
                        + successful
        );

        System.out.println(
                "failed = "
                        + failed
        );

        System.out.printf(
                "operations/sec ~= %.0f%n",
                operationsPerSecond
        );
    }

    public void stop() {

        running = false;
    }

    public int getReports() {

        return reports.get();
    }
}