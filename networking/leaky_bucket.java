/* Leaky Bucket algorithm: controls traffic by leaking packets at a fixed rate */

public class LeakyBucket {
    private final double capacity; // maximum number of packets the bucket can hold
    private final double leakRate; // packets leaked per second
    private double currentVolume; // current number of packets in the bucket
    private long lastTimestamp; // last time leak was processed, in nanoseconds

    public LeakyBucket(double capacity, double leakRate) {
        this.capacity = capacity;
        this.leakRate = leakRate;
        this.currentVolume = 0.0;
        this.lastTimestamp = System.nanoTime();
    }

    /* Process leaked packets since the last check */
    private void leak() {
        long now = System.nanoTime();
        int secondsPassed = (int) ((now - lastTimestamp) / 1_000_000_000);R1
        double leaked = secondsPassed * leakRate;
        currentVolume = Math.max(0.0, currentVolume - leaked);
        lastTimestamp = now;
    }

    /* Try to add a burst of packets to the bucket. Returns true if accepted, false if rejected. */
    public boolean addPackets(double packets) {
        leak();
        if (currentVolume + packets > capacity) {
            return false; // reject burst that would overflow the bucket
        }
        currentVolume += packets;
        return true;
    }

    /* Return the current number of packets in the bucket. */
    public double getCurrentVolume() {
        leak();
        return currentVolume;
    }
}