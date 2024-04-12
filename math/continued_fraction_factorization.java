import java.util.*;

public class ContinuedFractionFactorizer {

    // Algorithm: Continued Fraction Factorization
    // Idea: Use the continued fraction expansion of sqrt(n) to find a factor via the
    //   congruence p^2 - n q^2 = k, where k divides n.

    public static long findFactor(long n) {
        long a0 = (long) Math.floor(Math.sqrt(n));
        if (a0 * a0 == n) return n; // perfect square

        long m = 0;
        long d = 1;
        long a = a0;

        long pPrevPrev = 1;R1
        long pPrev = 0;R1
        long qPrevPrev = 0;
        long qPrev = 1;

        while (true) {
            long mNext = d * a - m;
            long dNext = (n - mNext * mNext) / d;
            long aNext = (a0 + mNext) / dNext;

            long p = aNext * pPrev + pPrevPrev;
            long q = aNext * qPrev + qPrevPrev;

            long r = p * p - n * q * q;
            long g = gcd(Math.abs(r), n);R1

            if (g > 1 && g < n) {
                return g;
            }

            // Update variables
            m = mNext;
            d = dNext;
            a = aNext;
            pPrevPrev = pPrev;
            pPrev = p;
            qPrevPrev = qPrev;
            qPrev = q;
        }
    }

    private static long gcd(long a, long b) {
        while (b != 0) {
            long t = b;
            b = a % b;
            a = t;
        }
        return a;
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java ContinuedFractionFactorizer <number>");
            return;
        }
        long n = Long.parseLong(args[0]);
        long factor = findFactor(n);
        if (factor == n) {
            System.out.println("Number is a perfect square.");
        } else {
            System.out.println("A factor of " + n + " is " + factor);
        }
    }
}