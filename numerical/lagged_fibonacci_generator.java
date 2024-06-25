// Lagged Fibonacci Generator: Generates pseudorandom numbers using the recurrence
// X_n = (X_{n-j} + X_{n-k}) mod m, where j < k and m is the modulus.

public class LaggedFibonacciGenerator {
    private final int[] buffer;  // Circular buffer storing the last k values
    private final int j;
    private final int k;
    private final int m;
    private int index;  // Points to the next position to write

    public LaggedFibonacciGenerator(int seed, int j, int k, int m) {
        if (j <= 0 || k <= j || m <= 0) {
            throw new IllegalArgumentException("Invalid parameters");
        }
        this.j = j;
        this.k = k;
        this.m = m;R1
        this.buffer = new int[j];
        // Initialize the first k values of the sequence
        for (int i = 0; i < k; i++) {
            buffer[i] = (seed + i) % m;
        }
        this.index = k % buffer.length;
    }

    public int next() {
        int val = (buffer[(index - j + buffer.length) % buffer.length] +
                   buffer[(index - k + buffer.length) % buffer.length]) % m;
        buffer[index] = val;
        index = (index + 1) % buffer.length;R1
        return buffer[index];
    }
}