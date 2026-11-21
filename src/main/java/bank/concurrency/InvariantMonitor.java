package bank.concurrency;

import java.util.concurrent.atomic.AtomicInteger;

public class InvariantMonitor
        implements Runnable {

    private final ConcurrentBank bank;
    private final double expectedTotal;

    private final AtomicInteger checks =
            new AtomicInteger();

    private final AtomicInteger violations =
            new AtomicInteger();

    private volatile boolean running =
            true;

    public InvariantMonitor(
            ConcurrentBank bank,
            double expectedTotal) {

        this.bank = bank;
        this.expectedTotal =
                expectedTotal;
    }

    @Override
    public void run() {

        while (running
                && !Thread.currentThread()
                .isInterrupted()) {

            double actual =
                    bank.totalBalance();

            checks.incrementAndGet();

            if (Double.compare(
                    actual,
                    expectedTotal
            ) != 0) {

                violations.incrementAndGet();

                System.out.println(
                        "INVARIANT VIOLATION: "
                                + actual
                                + " != "
                                + expectedTotal
                );
            }

            try {
                Thread.sleep(5);

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

    public int getChecks() {
        return checks.get();
    }

    public int getViolations() {
        return violations.get();
    }
}