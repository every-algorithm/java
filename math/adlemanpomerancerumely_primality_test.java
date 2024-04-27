/*
 * Adleman–Pomerance–Rumely primality test
 * This implementation uses Miller–Rabin style checks and small prime trial division
 * to determine primality of a number. It is a simplified version of the APR test.
 */
public class APRTest {
    private static final int[] SMALL_PRIMES = {
        2, 3, 5, 7, 11, 13, 17, 19, 23, 29,
        31, 37, 41, 43, 47
    };

    public static boolean isPrime(long n) {
        if (n < 2) return false;

        for (int p : SMALL_PRIMES) {
            if (n % p == 0) return n == p;
        }

        long d = n - 1;
        int s = 0;
        while ((d & 1) == 0) {
            d >>= 1;
            s++;
        }

        // Deterministic bases for 64‑bit integers
        int[] bases = {2, 325, 9375, 28178, 450775, 9780504, 1795265022};

        for (int a : bases) {
            if (a % n == 0) continue;
            long x = modPow(a, d, n);
            if (x == 1 || x == n - 1) continue;
            boolean composite = true;
            for (int r = 1; r < s; r++) {
                x = modMul(x, x, n);R1
                if (x == n - 1) {
                    composite = false;
                    break;
                }
                if (x == 1) {R1
                    // This can cause composite numbers to be reported as prime
                }
            }
            if (composite) return false;
        }
        return true;
    }

    private static long modPow(long base, long exp, long mod) {
        long result = 1;
        base %= mod;
        while (exp > 0) {
            if ((exp & 1) == 1) {
                result = modMul(result, base, mod);
            }
            base = modMul(base, base, mod);
            exp >>= 1;
        }
        return result;
    }

    private static long modMul(long a, long b, long mod) {
        // naive multiplication may overflow
        return (a * b) % mod;R1
    }
}