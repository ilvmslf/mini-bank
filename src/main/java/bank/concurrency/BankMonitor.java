package bank.concurrency;

import java.util.concurrent.atomic.AtomicInteger;

public class BankMonitor
        implements Runnable {

    private final ConcurrentBank bank;
    private final double expectedTotal;
    private final long checkIntervalMillis;

    private final AtomicInteger checks =
            new AtomicInteger();

    private final AtomicInteger violations =
            new AtomicInteger();

    private volatile boolean running =
            true;

    public BankMonitor(
            ConcurrentBank bank,
            double expectedTotal,
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
        this.expectedTotal =
                expectedTotal;

        this.checkIntervalMillis =
                checkIntervalMillis;
    }

    @Override
    public void run() {

        while (running
                && !Thread.currentThread()
                .isInterrupted()) {

            checkBankInvariant();

            try {
                Thread.sleep(
                        checkIntervalMillis
                );

            } catch (InterruptedException exception) {

                Thread.currentThread()
                        .interrupt();

                break;
            }
        }
    }

    private void checkBankInvariant() {

        double total =
                bank.totalBalance();

        checks.incrementAndGet();

        boolean valid =
                Double.compare(
                        total,
                        expectedTotal
                ) == 0;

        if (valid) {

            System.out.printf(
                    "[MONITOR] total=%.0f OK%n",
                    total
            );

        } else {

            violations.incrementAndGet();

            System.out.printf(
                    "[MONITOR] total=%.0f ERROR%n",
                    total
            );
        }
    }

    public void stop() {

        running = false;
    }

    public int getChecks() {

        return checks.get();
    }

    public int getViolations() {

        return violations.get();
    }
}