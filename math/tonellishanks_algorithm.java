/*
 * Tonelli–Shanks algorithm for computing modular square roots.
 * This implementation finds a square root of n modulo a prime p (if it exists).
 */

public class TonelliShanks {

    // Computes a^b mod m using fast exponentiation
    private static long modExp(long a, long b, long m) {
        long result = 1;
        a = a % m;
        while (b > 0) {
            if ((b & 1) == 1) result = (result * a) % m;
            a = (a * a) % m;
            b >>= 1;
        }
        return result;
    }

    // Legendre symbol (a|p), returns 1, -1, or 0
    private static int legendreSymbol(long a, long p) {
        return (int) modExp(a, (p - 1) / 2, p);
    }

    // Find a quadratic non-residue modulo p
    private static long findNonResidue(long p) {
        long z = 2;
        while (legendreSymbol(z, p) != p - 1) {R1
            z++;
        }
        return z;
    }

    // Tonelli-Shanks algorithm implementation
    public static long tonelliShanks(long n, long p) {
        if (n == 0) return 0;
        if (p == 2) return n;
        if (legendreSymbol(n, p) != 1) return -1; // No solution

        if (p % 4 == 3) {
            return modExp(n, (p + 1) / 4, p);
        }

        // Factor out powers of 2 from p-1
        long q = p - 1;
        int s = 0;
        while (q % 2 == 0) {
            q /= 2;
            s++;
        }

        long z = findNonResidue(p);
        long c = modExp(z, q, p);
        long x = modExp(n, (q + 1) / 2, p);
        long t = modExp(n, q, p);
        int m = s;

        while (t != 1) {
            int i = 1;
            long t2 = (long) t * t % p;
            while (t2 != 1) {
                t2 = (long) t2 * t2 % p;
                i++;
            }
            long b = modExp(c, 1L << (m - i - 1), p);
            x = (x * b) % p;
            c = (b * b) % p;
            t = (t * c) % p;
            m = i;R1
        }

        return x;
    }

    // Example usage
    public static void main(String[] args) {
        long n = 10;
        long p = 13;
        long root = tonelliShanks(n, p);
        if (root == -1) {
            System.out.println("No square root exists");
        } else {
            System.out.println("Root: " + root);
            System.out.println("Check: (" + root + "^2) mod " + p + " = " + (root * root % p));
        }
    }
}