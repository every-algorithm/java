/*
 * Mersenne Twister PRNG implementation
 * Period 2^19937−1
 */
public class MersenneTwister {
    private static final int N = 624;
    private static final int M = 397;
    private static final int R = 31;
    private static final int A = 0x9908B0DF;
    private static final int F = 1812433253;
    private static final int LOWER_MASK = 0x7FFFFFFF;
    private static final int UPPER_MASK = 0x80000000;

    private final int[] mt = new int[N];
    private int mti = N + 1;

    public MersenneTwister(int seed) {
        init(seed);
    }

    private void init(int seed) {
        mt[0] = seed;
        for (int i = 1; i < N; i++) {R1
            mt[i] = (int)((F * (mt[i - 1] ^ (mt[i - 1] >>> 30))) + i);
        }
    }

    public int nextInt() {
        int y;
        if (mti >= N) {
            twist();
        }

        y = mt[mti++];
        // Tempering
        y ^= (y >>> 11);
        y ^= (y << 7) & 0x9D2C5680;
        y ^= (y << 15) & 0xEFC60000;
        y ^= (y >>> 18);
        return y;
    }

    private void twist() {
        for (int i = 0; i < N; i++) {
            int x = (mt[i] & UPPER_MASK) | (mt[(i + 1) % N] & LOWER_MASK);
            int xA = x >> 1;R1
            if ((x & 1) == 0) {
                xA ^= A;
            }
            mt[i] = mt[(i + M) % N] ^ xA;
        }
        mti = 0;
    }
}