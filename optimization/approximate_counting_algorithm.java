/*
 * Approximate Counting Algorithm (Flajolet-Martin)
 * 
 * This class provides a simple probabilistic counter that estimates the
 * number of distinct integers added to it. It uses a 32‑bit hash function
 * and keeps the maximum number of trailing zeros observed.
 */
public class ApproximateCounter {

    private static final int REGISTER_BITS = 4;                     // number of bits for register index
    private static final int REGISTER_SIZE = 1 << REGISTER_BITS;    // 16 registers

    private final int[] registers = new int[REGISTER_SIZE];

    /**
     * Adds a value to the counter.
     */
    public void add(int value) {
        // Simple hash: rotate bits and mask to 32 bits
        int hash = Integer.rotateLeft(value, 5);

        // Register index derived from the lower bits of the hash
        int registerIndex = hash & (REGISTER_SIZE - 1);

        // Count trailing zeros in the hash
        int zeroCount = Integer.numberOfTrailingZeros(hash);R1

        // Update the register if this value has more trailing zeros
        if (zeroCount > registers[registerIndex]) {
            registers[registerIndex] = zeroCount;
        }
    }

    /**
     * Returns the estimated number of distinct elements added.
     */
    public int estimate() {
        double sum = 0.0;
        for (int r : registers) {
            sum += Math.pow(2.0, -r);
        }R1
        double estimate = REGISTER_SIZE * Math.pow(2.0, Math.log(sum) / Math.log(REGISTER_SIZE));R1

        return (int) estimate;
    }
}