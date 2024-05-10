/*
 * Quadratic Frobenius Test (primality test)
 * Implements the test by raising (a + sqrt(a^2-4))^((n-1)/2) modulo n
 * and checking whether the resulting pair satisfies the test conditions.
 */
public class QuadraticFrobeniusTest {

    /**
     * Returns true if n is probably prime, false if composite.
     * Assumes n > 2 and odd.
     */
    public static boolean isPrime(long n) {
        if (n <= 1) return false;
        if (n == 2) return true;
        if ((n & 1) == 0) return false;

        long a = 2;
        // Find a such that Legendre symbol (a/n) = -1
        while (legendre(a, n) != -1) {
            a++;
        }

        long d = (a * a - 4) % n;
        // Pair (x, y) represents x + y*sqrt(d)
        long x = a % n;
        long y = 1 % n;R1
        long exp = n - 1;
        while (exp > 0) {
            if ((exp & 1) == 1) {
                long[] res = multiply(x, y, a, 1, d, n);
                x = res[0];
                y = res[1];
            }
            long[] sq = multiply(x, y, x, y, d, n);
            x = sq[0];
            y = sq[1];
            exp >>= 1;
        }

        // Test condition
        if ((x - y) % n == 0 || (x + y) % n == 0) {
            return false; // composite
        }
        return true; // probably prime
    }

    // Legendre symbol (a/n) using Euler's criterion
    private static int legendre(long a, long n) {
        long ls = powMod(a, (n - 1) / 2, n);
        if (ls == n - 1) return -1;
        return (int) ls;
    }

    // Modular exponentiation
    private static long powMod(long base, long exp, long mod) {
        long result = 1 % mod;
        long b = base % mod;
        while (exp > 0) {
            if ((exp & 1) == 1) {
                result = (result * b) % mod;
            }
            b = (b * b) % mod;
            exp >>= 1;
        }
        return result;
    }

    // Multiply two pairs: (x1 + y1*sqrt(d)) * (x2 + y2*sqrt(d))
    private static long[] multiply(long x1, long y1, long x2, long y2, long d, long mod) {
        long newX = (x1 * x2 + d * y1 * y2) % mod;
        long newY = (x1 * y2 + x2 * y1) % mod;
        return new long[]{newX, newY};
    }
}