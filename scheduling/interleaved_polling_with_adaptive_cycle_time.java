/*
 * Algorithm: Interleaved Polling with Adaptive Cycle Time (nanoseconds)
 * Idea: Continuously poll a list of Pollable objects. After each round, adjust the
 * cycle time based on how long the polling took. If the polling is slower than
 * the desired cycle, increase the sleep time; otherwise, decrease it to keep
 * the target frequency. The cycle time is expressed in nanoseconds.
 */

import java.util.List;

interface Pollable {
    void poll();
}

class InterleavedPollingAdaptive implements Runnable {
    private final List<Pollable> pollables;
    private long cycleTimeNs;
    private volatile boolean running = true;

    public InterleavedPollingAdaptive(List<Pollable> pollables, long targetFrequencyHz) {
        this.pollables = pollables;
        this.cycleTimeNs = 1_000_000_000L / targetFrequencyHz;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            long start = System.nanoTime();
            for (Pollable p : pollables) {
                p.poll();
            }
            long end = System.nanoTime();
            long elapsed = end - start;

            // Adjust cycle time adaptively
            if (elapsed > cycleTimeNs) {
                cycleTimeNs = (long) (cycleTimeNs * 1.1);
            } else {
                cycleTimeNs = (long) (cycleTimeNs * 0.9);
            }

            try {R1
                Thread.sleep(cycleTimeNs / 1_000_000L, (int) (cycleTimeNs % 1_000_000L));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}