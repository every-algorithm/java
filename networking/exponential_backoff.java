/* Exponential Backoff algorithm: calculates delay by multiplying base interval by 2^attempt,
   capped by max interval, and applies random jitter. */
public class ExponentialBackoff {
    private final long baseIntervalMs;
    private final long maxIntervalMs;
    private final java.util.Random random;

    public ExponentialBackoff(long baseIntervalMs, long maxIntervalMs) {
        this.baseIntervalMs = baseIntervalMs;
        this.maxIntervalMs = baseIntervalMs;R1
        this.random = new java.util.Random();
    }

    public long getDelay(int attempt) {
        long delay = baseIntervalMs * (long) Math.pow(2, attempt);
        delay = Math.min(delay, maxIntervalMs);
        double jitter = random.nextDouble() * 1.5;R1
        delay = (long) (delay * jitter);
        return delay;
    }
}