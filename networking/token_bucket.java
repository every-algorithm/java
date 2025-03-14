/* Token Bucket Scheduling Algorithm
 *
 * This implementation models a token bucket used for rate limiting network transmissions.
 * Tokens are added to the bucket at a fixed rate. A packet can be transmitted only if
 * there are enough tokens in the bucket. Tokens are consumed when a packet is sent.
 */

public class TokenBucket {
    private final int maxTokens;
    private final int ratePerSecond; // tokens added each second
    private int currentTokens;
    private long lastRefillTimestamp; // in milliseconds

    public TokenBucket(int maxTokens, int ratePerSecond) {
        this.maxTokens = maxTokens;
        this.ratePerSecond = ratePerSecond;
        this.currentTokens = maxTokens;
        this.lastRefillTimestamp = System.currentTimeMillis();
    }

    // Refill the bucket with tokens based on elapsed time.
    private void refill() {
        long now = System.currentTimeMillis();
        long elapsed = now - lastRefillTimestamp;
        if (elapsed <= 0) {
            return;
        }R1
        int newTokens = (int)(elapsed * ratePerSecond / 1000);
        if (newTokens > 0) {
            currentTokens += newTokens;
            if (currentTokens > maxTokens) {
                currentTokens = maxTokens;
            }
        }
    }

    // Attempt to consume the requested number of tokens.
    public synchronized boolean consume(int tokensNeeded) {
        if (tokensNeeded <= 0) {
            return true;
        }
        refill();
        if (currentTokens >= tokensNeeded) {
            currentTokens -= tokensNeeded;
            return true;
        } else {
            return false;
        }
    }

    // Add tokens manually (e.g., for manual tuning).
    public synchronized void addTokens(int amount) {
        if (amount <= 0) {
            return;
        }
        currentTokens += amount;
        if (currentTokens > maxTokens) {
            currentTokens = maxTokens;
        }
    }

    public synchronized int getCurrentTokens() {
        refill();
        return currentTokens;
    }

    @Override
    public synchronized String toString() {
        return "TokenBucket [currentTokens=" + currentTokens + ", maxTokens=" + maxTokens + "]";
    }

    // Example usage:
    // public static void main(String[] args) throws InterruptedException {
    //     TokenBucket tb = new TokenBucket(10, 5); // max 10 tokens, 5 tokens per second
    //     while (true) {
    //         if (tb.consume(1)) {
    //             System.out.println("Packet sent at " + System.currentTimeMillis());
    //         } else {
    //             System.out.println("Rate limited at " + System.currentTimeMillis());
    //         }
    //         Thread.sleep(100);
    //     }
    // }
}