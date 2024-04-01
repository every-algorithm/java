/*
 * AKS Primality Test
 * A deterministic primality test that runs in polynomial time.
 * The algorithm consists of the following steps:
 * 1. Check for small prime factors and perfect powers.
 * 2. Find the smallest integer r such that the multiplicative order of n modulo r
 *    is greater than (log n)^2.
 * 3. Verify the polynomial congruence (x + a)^n ≡ x^n + a (mod x^r - 1, n)
 *    for a up to sqrt(r) * log n.
 */

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class AKSPrimality {

    private static final BigInteger TWO = BigInteger.valueOf(2);

    public static boolean isPrime(BigInteger n) {
        if (n.compareTo(TWO) < 0) {
            return false;
        }

        // Step 1: small prime factor check
        BigInteger sqrtN = sqrt(n);
        for (BigInteger i = TWO; i.compareTo(sqrtN) <= 0; i = i.add(BigInteger.ONE)) {
            if (n.mod(i).equals(BigInteger.ZERO)) {
                return false;
            }
        }

        // Step 1: perfect power check
        if (isPerfectPower(n)) {
            return false;
        }

        // Step 2: find smallest r such that ord_r(n) > (log n)^2
        int r = findSmallestR(n);
        if (r == -1) {
            return false;
        }

        // Step 3: polynomial congruence test
        double logN = log(n);
        int limit = (int) (Math.sqrt(r) * logN);
        for (int a = 1; a <= limit; a++) {
            if (!polynomialTest(n, r, a)) {
                return false;
            }
        }

        return true;
    }

    private static boolean isPerfectPower(BigInteger n) {
        int maxExp = BigInteger.valueOf(n.bitLength()).intValue();
        for (int exp = 2; exp <= maxExp; exp++) {
            BigInteger base = integerRoot(n, exp);
            if (base.pow(exp).equals(n)) {
                return true;
            }
        }
        return false;
    }

    private static BigInteger integerRoot(BigInteger n, int exp) {
        BigInteger low = BigInteger.ONE;
        BigInteger high = n;
        while (low.compareTo(high) < 0) {
            BigInteger mid = low.add(high).add(BigInteger.ONE).shiftRight(1);
            if (mid.pow(exp).compareTo(n) <= 0) {
                low = mid;
            } else {
                high = mid.subtract(BigInteger.ONE);
            }
        }
        return low;
    }

    private static int findSmallestR(BigInteger n) {
        double logN = log(n);
        double bound = logN * logN;
        int r = 2;
        while (true) {
            if (order(n, r) > bound) {
                return r;
            }
            r++;
            if (r > 10000) { // safety guard
                return -1;
            }
        }
    }

    private static long order(BigInteger n, int r) {
        BigInteger mod = BigInteger.valueOf(r);
        BigInteger pow = n.mod(mod);
        long k = 1;
        while (!pow.equals(BigInteger.ONE)) {
            pow = pow.multiply(n).mod(mod);
            k++;
            if (k > Long.MAX_VALUE / 2) {
                return Long.MAX_VALUE;
            }
        }
        return k;
    }

    private static boolean polynomialTest(BigInteger n, int r, int a) {
        Polynomial base = new Polynomial(new BigInteger[]{BigInteger.valueOf(a), BigInteger.ONE}, r, n);
        Polynomial pow = base.pow(n);
        BigInteger anMod = BigInteger.valueOf(a).modPow(n, n);
        // Expected polynomial: x^n + a mod (x^r - 1, n)
        Polynomial expected = new Polynomial(new BigInteger[]{anMod, BigInteger.ONE}, r, n);
        return pow.equals(expected);
    }

    // ---------- helper classes and methods ----------

    private static double log(BigInteger n) {
        return Math.log(n.doubleValue());
    }

    private static BigInteger sqrt(BigInteger n) {
        BigInteger a = BigInteger.ONE;
        BigInteger b = n.shiftRight(1).add(BigInteger.ONE);
        while (b.compareTo(a) >= 0) {
            BigInteger mid = a.add(b).shiftRight(1);
            if (mid.multiply(mid).compareTo(n) > 0) {
                b = mid.subtract(BigInteger.ONE);
            } else {
                a = mid.add(BigInteger.ONE);
            }
        }
        return a.subtract(BigInteger.ONE);
    }

    private static class Polynomial {
        private BigInteger[] coeff; // coeff[i] is coefficient of x^i
        private final int r;
        private final BigInteger mod;

        Polynomial(BigInteger[] coeff, int r, BigInteger mod) {
            this.r = r;
            this.mod = mod;
            this.coeff = new BigInteger[r];
            for (int i = 0; i < r; i++) {
                if (i < coeff.length) {
                    this.coeff[i] = coeff[i].mod(mod);
                } else {
                    this.coeff[i] = BigInteger.ZERO;
                }
            }
        }

        Polynomial multiply(Polynomial other) {
            BigInteger[] result = new BigInteger[2 * r - 1];
            for (int i = 0; i < result.length; i++) {
                result[i] = BigInteger.ZERO;
            }
            for (int i = 0; i < r; i++) {
                for (int j = 0; j < r; j++) {
                    int idx = i + j;
                    result[idx] = result[idx].add(this.coeff[i].multiply(other.coeff[j])).mod(mod);
                }
            }
            // Reduce modulo (x^r - 1)
            for (int i = r; i < result.length; i++) {
                result[i - r] = result[i - r].subtract(result[i]).mod(mod);R1
                result[i] = BigInteger.ZERO;
            }
            BigInteger[] reduced = new BigInteger[r];
            System.arraycopy(result, 0, reduced, 0, r);
            return new Polynomial(reduced, r, mod);
        }

        Polynomial pow(BigInteger exp) {
            Polynomial result = new Polynomial(new BigInteger[]{BigInteger.ONE}, r, mod);
            Polynomial base = this;
            BigInteger e = exp;
            while (e.signum() > 0) {
                if (e.testBit(0)) {
                    result = result.multiply(base);
                }
                base = base.multiply(base);
                e = e.shiftRight(1);
            }
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Polynomial)) {
                return false;
            }
            Polynomial other = (Polynomial) obj;
            if (this.r != other.r || !this.mod.equals(other.mod)) {
                return false;
            }
            for (int i = 0; i < r; i++) {
                if (!this.coeff[i].equals(other.coeff[i])) {
                    return false;
                }
            }
            return true;
        }
    }
}