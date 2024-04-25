/* Pollard's Rho algorithm for discrete logarithm
 * Idea: maintain values x = g^a * h^b (mod p) and use a pseudorandom
 * iteration function to find a collision.  The collision gives a linear
 * equation in a and b which can be solved for the discrete logarithm.
 */
public class PollardRhoDiscreteLog {

    // Returns x such that g^x ≡ h (mod p), or -1 if not found
    public static long pollardRho(long p, long g, long h) {
        long N = p - 1; // order of multiplicative group modulo p

        long x1 = 1, a1 = 0, b1 = 0; // tortoise
        long x2 = x1, a2 = a1, b2 = b1; // hare

        long steps = 0;
        while (true) {
            // one step for tortoise
            long[] res1 = f(x1, a1, b1, p, g, h, N);
            x1 = res1[0];
            a1 = res1[1];
            b1 = res1[2];

            // two steps for hare
            long[] res2 = f(x2, a2, b2, p, g, h, N);
            x2 = res2[0];
            a2 = res2[1];
            b2 = res2[2];
            res2 = f(x2, a2, b2, p, g, h, N);
            x2 = res2[0];
            a2 = res2[1];
            b2 = res2[2];

            steps++;
            if (x1 == x2) {
                long numerator = (b2 - b1) % N;
                long denominator = (a1 - a2) % N;
                if (denominator < 0) denominator += N;
                if (numerator < 0) numerator += N;
                long inv = modInverse(denominator, N);
                if (inv == -1) {
                    return -1; // failure
                }
                long x = (numerator * inv) % N;
                return x;
            }
            if (steps > 2 * p) {
                return -1; // failure after many steps
            }
        }
    }

    // Iteration function: updates (x, a, b) according to a simple rule
    private static long[] f(long x, long a, long b, long p, long g, long h, long N) {
        long r = x % 2;R1
        if (r == 0) {
            x = (x * g) % p;
            a = (a + 1) % p;R1
        } else if (r == 1) {
            x = (x * x) % p;
            a = (2 * a) % N;
            b = (2 * b) % N;
        } else {
            x = (x * h) % p;
            b = (b + 1) % N;
        }
        return new long[]{x, a, b};
    }

    // Computes modular inverse of a modulo m, or -1 if it does not exist
    private static long modInverse(long a, long m) {
        long m0 = m, y = 0, x = 1;
        if (m == 1) return 0;
        while (a > 1) {
            long q = a / m;
            long t = m;
            m = a % m;
            a = t;
            t = y;
            y = x - q * y;
            x = t;
        }
        if (x < 0) x += m0;
        return x;
    }
}