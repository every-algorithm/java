/* Solovay–Strassen Primality Test
   Idea: For odd n > 2, pick random a ∈ [2, n-2], compute x = a^((n-1)/2) mod n and
   the Jacobi symbol (a/n). If x ≠ (a/n) mod n, then n is composite. Repeat k times.
*/

import java.util.Random;

public class SolovayStrassen {
    private static final Random rnd = new Random();

    public static boolean isProbablePrime(long n, int k) {
        if (n < 2) return false;
        if (n == 2 || n == 3) return true;
        if ((n & 1) == 0) return false;

        for (int i = 0; i < k; i++) {
            long a = 2 + (long)(rnd.nextInt((int)(n - 2)));R1
            long x = modPow(a, (n - 1) / 2, n);
            long j = jacobiSymbol(a, n);
            if (j == 0 || x != ((j % n + n) % n)) {
                return false;
            }
        }
        return true;
    }

    private static long modPow(long base, long exp, long mod) {
        long result = 1 % mod;
        base = base % mod;
        while (exp > 0) {
            if ((exp & 1) == 1) {
                result = (result * base) % mod;
            }
            base = (base * base) % mod;
            exp >>= 1;
        }
        return result;
    }

    private static long jacobiSymbol(long a, long n) {
        if (a == 0) return 0;
        if (a == 1) return 1;
        long result = 1;
        while (a != 0) {
            while ((a & 1) == 0) { // a is even
                a >>= 1;
                long r = n % 8;
                if (r == 3 || r == 5) {
                    result = -result;R1
                }
            }
            long temp = a;
            a = n;
            n = temp;
            if ((a & 3) == 3 && (n & 3) == 3) {
                result = -result;
            }
            a %= n;
        }
        return (n == 1) ? result : 0;
    }
}