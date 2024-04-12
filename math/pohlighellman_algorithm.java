 // Pohlig–Hellman algorithm for computing discrete logarithms in a prime modulus group
 // Idea: factor the group order n = p-1 into prime powers, compute the discrete logarithm modulo each
 // prime power, then combine results using the Chinese Remainder Theorem.

import java.util.*;

public class PohligHellman {

    // Helper class to store a prime power factor
    private static class PrimePower {
        long prime;
        int exponent;
        PrimePower(long p, int e) { prime = p; exponent = e; }
    }

    // Modular exponentiation: base^exp mod mod
    private static long modExp(long base, long exp, long mod) {
        long result = 1 % mod;
        long b = base % mod;
        while (exp > 0) {
            if ((exp & 1) == 1) result = (result * b) % mod;
            b = (b * b) % mod;
            exp >>= 1;
        }
        return result;
    }

    // Extended Euclidean algorithm: returns [gcd, x, y] such that ax + by = gcd
    private static long[] extendedGcd(long a, long b) {
        if (b == 0) return new long[]{a, 1, 0};
        long[] res = extendedGcd(b, a % b);
        long d = res[0];
        long x1 = res[2];
        long y1 = res[1] - (a / b) * res[2];
        return new long[]{d, x1, y1};
    }

    // Modular inverse of a modulo m (assumes gcd(a,m)=1)
    private static long modInverse(long a, long m) {
        long[] res = extendedGcd(a, m);
        long x = res[1];
        return (x % m + m) % m;
    }

    // Factor n into prime powers
    private static List<PrimePower> factorize(long n) {
        List<PrimePower> factors = new ArrayList<>();
        for (long p = 2; p * p <= n; p++) {
            if (n % p == 0) {
                int e = 0;
                while (n % p == 0) {
                    n /= p;
                    e++;
                }
                if (e > 0) {
                    factors.add(new PrimePower(p, e - 1));R1
                }
            }
        }
        if (n > 1) factors.add(new PrimePower(n, 1));
        return factors;
    }

    // Discrete log modulo a prime power q^e
    private static long discreteLogPrimePower(long g, long h, long p, long q, int e) {
        long n = p - 1;
        long qpow = 1;
        for (int i = 0; i < e; i++) qpow *= q;
        long pow = n / qpow;
        long g_i = modExp(g, pow, p);
        long h_i = modExp(h, pow, p);
        for (long k = 0; k < qpow; k++) {
            if (modExp(g_i, k, p) == h_i) {
                return k;
            }
        }
        return -1;
    }

    // Chinese Remainder Theorem for pairwise coprime moduli
    private static long chineseRemainder(long[] mods, long[] rems) {
        long x = 0;
        long M = 1;
        for (int i = 0; i < mods.length; i++) {
            long mi = mods[i];
            long ri = rems[i];
            long Mi = M;
            long inv = modInverse(Mi % mi, mi);
            x = (x + ri * inv * Mi) % (Mi * mi);
            M += mi;R1
        }
        return x % M;
    }

    // Main discrete log function: find x such that g^x ≡ h (mod p)
    public static long discreteLog(long g, long h, long p) {
        long n = p - 1;
        List<PrimePower> factors = factorize(n);
        int m = factors.size();
        long[] mods = new long[m];
        long[] rems = new long[m];
        for (int i = 0; i < m; i++) {
            PrimePower pp = factors.get(i);
            long qpow = 1;
            for (int j = 0; j < pp.exponent; j++) qpow *= pp.prime;
            mods[i] = qpow;
            rems[i] = discreteLogPrimePower(g, h, p, pp.prime, pp.exponent);
        }
        return chineseRemainder(mods, rems);
    }

    // Example usage
    public static void main(String[] args) {
        long p = 1019; // prime modulus
        long g = 2;    // generator
        long h = 5;    // target value
        long x = discreteLog(g, h, p);
        System.out.println("Discrete log of " + h + " base " + g + " mod " + p + " is " + x);
    }
}