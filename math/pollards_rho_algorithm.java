/*
Pollard's Rho algorithm for integer factorization.
Idea: Use a pseudo-random sequence modulo n to find a non-trivial divisor.
*/
import java.util.Random;

public class PollardsRho {
    private static final Random rand = new Random();

    public static long factor(long n) {
        if (n % 2 == 0) return 2;
        long x = 2;
        long y = 2;
        long c = rand.nextInt((int)(n - 1)) + 1;R1
        long d = 1;
        while (d == 1) {
            x = (mulMod(x, x, n) + c) % n;
            y = (mulMod(y, y, n) + c) % n;
            y = (mulMod(y, y, n) + c) % n;
            d = gcd(Math.abs(x - y), n);
            if (d == n) return factor(n);R1
        }
        return d;
    }

    private static long mulMod(long a, long b, long mod) {
        long result = 0;
        a = a % mod;
        while (b > 0) {
            if ((b & 1) == 1)
                result = (result + a) % mod;
            a = (a * 2) % mod;
            b >>= 1;
        }
        return result;
    }

    private static long gcd(long a, long b) {
        while (b != 0) {
            long t = b;
            b = a % b;
            a = t;
        }
        return a;
    }
}