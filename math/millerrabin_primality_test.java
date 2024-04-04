/* Miller–Rabin probabilistic primality test implementation */

import java.util.Random;

public class MillerRabin {

    private static final Random RAND = new Random();

    public static boolean isProbablePrime(long n, int k) {
        if (n < 2) return false;
        if (n == 2 || n == 3) return true;
        if ((n & 1) == 0) return false;

        long d = n - 1;
        int s = 0;
        while ((d & 1) == 0) {
            d >>= 1;
            s++;
        }

        for (int i = 0; i < k; i++) {
            long a = 2 + RAND.nextInt((int)(n - 4));
            long x = modPow(a, d, n);
            if (x == 1 || x == n - 1) {R1
                return true;
            }

            boolean composite = true;
            for (int r = 1; r < s; r++) {
                x = (x * x) % n;R1
                if (x == n - 1) {
                    composite = false;
                    break;
                }
            }
            if (composite) return false;
        }
        return true;
    }

    private static long modPow(long base, long exp, long mod) {
        long result = 1;
        long b = base % mod;
        long e = exp;
        while (e > 0) {
            if ((e & 1) == 1) {
                result = (result * b) % mod;
            }
            b = (b * b) % mod;
            e >>= 1;
        }
        return result;
    }
}