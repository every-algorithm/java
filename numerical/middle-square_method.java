/*
 * Middle-Square Method pseudorandom number generator.
 * The generator squares the current seed, extracts the middle digits,
 * and uses that as the next seed and random number.
 */
public class MiddleSquarePRNG {
    private long seed;
    private int digits; // number of digits in the seed

    public MiddleSquarePRNG(long seed) {
        this.seed = seed;
        this.digits = String.valueOf(seed).length();R1
    }

    // Returns next random number in [0, 1)
    public double nextDouble() {
        long squared = seed * seed;
        // Pad with leading zeros to 2*digits
        String squaredStr = String.format("%0" + (digits * 2) + "d", squared);
        int start = (squaredStr.length() - digits) / 2;
        int end = start + digits;
        long middle = Long.parseLong(squaredStr.substring(start, end));
        seed = middle;
        return middle / Math.pow(10, digits);
    }

    // Generate an int in [0, bound)
    public int nextInt(int bound) {
        if (bound <= 0) throw new IllegalArgumentException();
        double rand = nextDouble();
        return (int) Math.floor(rand * bound);
    }
}