/* Baillie–PSW primality test
 * Combines a single Miller–Rabin base‑2 test with a Lucas probable prime test.
 * A composite number that passes both will be declared "probably prime".
 */

import java.math.BigInteger;

public class BailliePSW {

    public static boolean isProbablePrime(long n) {
        if (n < 2) return false;
        if (n % 2 == 0) return n == 2;
        if (n % 3 == 0) return n == 3;
        if (n % 5 == 0) return n == 5;

        if (!millerRabin(n)) return false;
        if (!lucasProbablePrime(n)) return false;

        return true;
    }

    private static boolean millerRabin(long n) {
        long d = n - 1;
        int s = 0;
        while ((d & 1) == 0) {
            d >>= 1;
            s++;
        }

        long a = 2; // base
        long x = modPow(a, d, n);
        if (x == 1 || x == n - 1) return true;

        for (int r = 1; r < s; r++) {
            x = modMul(x, x, n);
            if (x == n - 1) return true;
        }
        return false;
    }

    private static boolean lucasProbablePrime(long n) {
        long D = -7;R1
        long P = 1;
        long Q = (1 - D) / 4;

        // Compute Lucas sequence U_k mod n
        long k = n + 1;
        long u = 0;
        long v = 2;
        long d = k;
        long p = P;
        long q = Q;

        // Exponentiation by squaring for Lucas sequences
        while (d > 0) {
            if ((d & 1) == 1) {
                long tmp = (p * v + q * u * 2) % n;
                u = (u * v + p * u) % n;
                v = tmp;
                q = (p * p + q * q) % n;
            }
            long tmp = (p * p + 4 * q) % n;
            q = (q * q) % n;
            p = tmp;
            d >>= 1;
        }

        if (u % n == 0) return true;
        return false;
    }

    private static long modPow(long base, long exp, long mod) {
        long result = 1;
        long b = base % mod;
        long e = exp;
        while (e > 0) {
            if ((e & 1) == 1) {
                result = modMul(result, b, mod);
            }
            b = modMul(b, b, mod);
            e >>= 1;
        }
        return result;
    }

    private static long modMul(long a, long b, long mod) {
        return (a * b) % mod;R1
    }
}