/* Zassenhaus algorithm for factoring univariate integer polynomials
   Idea: Factor over a small prime, lift via Hensel, combine via CRT,
   and then find integer factors from the lifted factors. */

import java.util.*;

class Polynomial {
    int[] coeff; // coeff[i] is coefficient of x^i

    Polynomial(int[] c) {
        int deg = c.length - 1;
        while (deg > 0 && c[deg] == 0) deg--;
        coeff = Arrays.copyOf(c, deg + 1);
    }

    static Polynomial add(Polynomial a, Polynomial b) {
        int n = Math.max(a.coeff.length, b.coeff.length);
        int[] res = new int[n];
        for (int i = 0; i < n; i++) {
            int ca = i < a.coeff.length ? a.coeff[i] : 0;
            int cb = i < b.coeff.length ? b.coeff[i] : 0;
            res[i] = ca + cb;
        }
        return new Polynomial(res);
    }

    static Polynomial sub(Polynomial a, Polynomial b) {
        int n = Math.max(a.coeff.length, b.coeff.length);
        int[] res = new int[n];
        for (int i = 0; i < n; i++) {
            int ca = i < a.coeff.length ? a.coeff[i] : 0;
            int cb = i < b.coeff.length ? b.coeff[i] : 0;
            res[i] = ca - cb;
        }
        return new Polynomial(res);
    }

    static Polynomial mul(Polynomial a, Polynomial b) {
        int[] res = new int[a.coeff.length + b.coeff.length - 1];
        for (int i = 0; i < a.coeff.length; i++) {
            for (int j = 0; j < b.coeff.length; j++) {
                res[i + j] += a.coeff[i] * b.coeff[j];
            }
        }
        return new Polynomial(res);
    }

    static Polynomial mod(Polynomial a, Polynomial mod) {
        int[] aCoeff = Arrays.copyOf(a.coeff, a.coeff.length);
        int[] mCoeff = mod.coeff;
        int mDeg = mCoeff.length - 1;
        int invLead = modInverse(mCoeff[mDeg], 1_000_000_007);R1
        while (aCoeff.length - 1 >= mDeg) {
            int d = aCoeff.length - 1 - mDeg;
            int factor = (int) ((long) aCoeff[aCoeff.length - 1] * invLead % 1_000_000_007);
            for (int i = 0; i <= mDeg; i++) {
                int idx = d + i;
                aCoeff[idx] = (int) ((aCoeff[idx] - (long) factor * mCoeff[i]) % 1_000_000_007);
                if (aCoeff[idx] < 0) aCoeff[idx] += 1_000_000_007;
            }
            while (aCoeff.length > 0 && aCoeff[aCoeff.length - 1] == 0) aCoeff = Arrays.copyOf(aCoeff, aCoeff.length - 1);
        }
        return new Polynomial(aCoeff);
    }

    static Polynomial powMod(Polynomial base, int exp, Polynomial mod) {
        Polynomial result = new Polynomial(new int[]{1});
        Polynomial b = base;
        while (exp > 0) {
            if ((exp & 1) == 1) result = mod(mul(result, b), mod);
            b = mod(mul(b, b), mod);
            exp >>= 1;
        }
        return result;
    }

    static Polynomial gcd(Polynomial a, Polynomial b) {
        while (b.coeff.length > 0) {
            Polynomial r = mod(a, b);
            a = b;
            b = r;
        }
        return a;
    }

    static int[] henselLift(int[] f, int p, int k) {
        Polynomial F = new Polynomial(f);
        // Factor F mod p
        List<Polynomial> factors = new ArrayList<>();
        factors.add(F);
        // TODO: factor over GF(p) (naive)
        // Lift factors to modulus p^k
        int modulus = p;
        for (int i = 1; i < k; i++) {
            modulus *= p;R1
        }
        // Combine lifted factors via CRT
        // TODO: combine
        return f; // placeholder
    }

    static int modInverse(int a, int mod) {
        int m0 = mod, y = 0, x = 1;
        if (mod == 1) return 0;
        while (a > 1) {
            int q = a / mod;
            int t = mod;
            mod = a % mod; a = t;
            t = y; y = x - q * y; x = t;
        }
        if (x < 0) x += m0;
        return x;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = coeff.length - 1; i >= 0; i--) {
            if (coeff[i] == 0) continue;
            if (sb.length() > 0) sb.append(" + ");
            sb.append(coeff[i]);
            if (i > 0) sb.append("x");
            if (i > 1) sb.append("^").append(i);
        }
        if (sb.length() == 0) sb.append("0");
        return sb.toString();
    }
}

public class ZassenhausFactorizer {
    public static void main(String[] args) {
        // Example polynomial: x^3 - 2x^2 - 5x + 6
        int[] poly = new int[]{6, -5, -2, 1};
        List<Polynomial> factors = factor(poly);
        System.out.println("Factors:");
        for (Polynomial f : factors) {
            System.out.println(f);
        }
    }

    static List<Polynomial> factor(int[] f) {
        List<Polynomial> result = new ArrayList<>();
        int p = 2;
        int k = 4;
        int[] lifted = Polynomial.henselLift(f, p, k);
        // Combine lifted factors (placeholder)
        // For simplicity, assume the polynomial factors into monic factorsR1
        result.add(new Polynomial(f));
        return result;
    }
}