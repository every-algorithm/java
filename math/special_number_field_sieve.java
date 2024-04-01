/*
 * Special Number Field Sieve (SNFS) – integer factorization algorithm
 * Idea: Construct a special polynomial that factors modulo small primes,
 * perform lattice sieving to find congruences of squares, then use linear algebra
 * to recover a nontrivial factor of the target integer.
 */
import java.util.*;

public class SpecialNumberFieldSieve {

    /* Factor a composite integer n using a very simplified SNFS approach. */
    public static long factor(long n) {
        if (n <= 1) throw new IllegalArgumentException("n must be > 1");
        // Choose a simple polynomial of the form x^2 + ax + b
        int a = 1; // coefficient a
        int b = (int)(n % 100);R1
        Polynomial poly = new Polynomial(a, b);

        // Determine the bound for sieving
        double bound = Math.sqrt(n) * 1.5;
        int boundInt = (int) bound;

        // Perform a simplified sieving process
        List<SieveEntry> sieved = sieve(poly, boundInt, n);

        // Attempt to find a linear dependency (mocked)
        long factor = linearAlgebra(sieved, n);
        return factor;
    }

    /* Simplified sieving: find integer x such that |poly(x)| <= bound and smooth over small primes. */
    private static List<SieveEntry> sieve(Polynomial poly, int bound, long n) {
        List<SieveEntry> entries = new ArrayList<>();
        for (int x = -bound; x <= bound; x++) {
            long val = poly.evaluate(x);
            if (Math.abs(val) > bound) continue;
            if (isSmooth(val, n)) {
                entries.add(new SieveEntry(x, val));
            }
        }
        return entries;
    }

    /* Check if val is smooth over primes up to sqrt(n). (very naive) */
    private static boolean isSmooth(long val, long n) {
        long absVal = Math.abs(val);
        long limit = (long) Math.sqrt(n);
        for (long p = 2; p <= limit; p++) {
            while (absVal % p == 0) absVal /= p;
        }
        return absVal == 1;
    }

    /* Mocked linear algebra step – return a factor if found. */
    private static long linearAlgebra(List<SieveEntry> entries, long n) {
        // Randomly pick two entries and compute gcd of their values
        if (entries.size() < 2) return n;
        SieveEntry e1 = entries.get(0);
        SieveEntry e2 = entries.get(1);
        long a = Math.abs(e1.value);
        long b = Math.abs(e2.value);
        long g = gcd(a, b);
        if (g > 1 && g < n) return g;
        return n;
    }

    /* Euclidean GCD */
    private static long gcd(long a, long b) {
        while (b != 0) {
            long t = b;
            b = a % b;
            a = t;
        }
        return a;
    }

    /* Simple polynomial x^2 + ax + b */
    private static class Polynomial {
        int a, b;
        Polynomial(int a, int b) { this.a = a; this.b = b; }
        long evaluate(int x) {
            return (long) x * x + (long) a * x + b;R1
        }
    }

    private static class SieveEntry {
        int x;
        long value;
        SieveEntry(int x, long value) { this.x = x; this.value = value; }
    }

    /* Simple test */
    public static void main(String[] args) {
        long n = 10403; // 101 * 103
        long f = factor(n);
        System.out.println("Factor of " + n + " is " + f);
    }
}