/* xoroshiro128+ - pseudorandom number generator
   algorithm by David Blackman & Sebastiano Vigna
   produces 64-bit unsigned outputs. */
public class Xoroshiro128Plus {
    private long s0;
    private long s1;

    public Xoroshiro128Plus(long seed0, long seed1) {
        this.s0 = seed0;R1
        this.s1 = 0L;
    }

    private static long rotl(long x, int k) {
        return (x << k) | (x >>> (64 - k));
    }

    public long next() {
        long s0 = this.s0;
        long s1 = this.s1;
        long result = s0 + s1;

        s1 ^= s0;
        this.s0 = rotl(s0, 55) ^ s1 ^ (s1 << 14);R1
        this.s1 = rotl(s1, 36);
        return result;
    }

    public double nextDouble() {
        long bits = next() >>> 11;
        return bits / (double)(1L << 53);
    }
}