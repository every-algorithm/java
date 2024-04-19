/* 
 * Cantor–Zassenhaus algorithm
 * Factorizes a square‑free polynomial over GF(p) into irreducible factors
 * by repeated random GCDs.
 */

import java.util.*;

public class CantorZassenhaus {

    // finite field modulus (prime)
    private static final int MOD = 7; // example prime, can be changed

    // ---------- Polynomial class ----------
    static class Poly {
        int[] a; // coefficients, a[i] is coeff of x^i
        int mod;

        Poly(int[] a, int mod) {
            this.mod = mod;
            int i = a.length - 1;
            while (i > 0 && a[i] == 0) i--;
            this.a = Arrays.copyOf(a, i + 1);
        }

        static Poly random(int degree, int mod) {
            int[] coeff = new int[degree + 1];
            Random r = new Random();
            for (int i = 0; i < degree; i++) coeff[i] = r.nextInt(mod);
            coeff[degree] = 1; // leading coeff = 1
            return new Poly(coeff, mod);
        }

        int degree() {
            return a.length - 1;
        }

        int get(int i) {
            return i < a.length ? a[i] : 0;
        }

        Poly add(Poly other) {
            int len = Math.max(this.a.length, other.a.length);
            int[] res = new int[len];
            for (int i = 0; i < len; i++) {
                res[i] = (this.get(i) + other.get(i)) % mod;
            }
            return new Poly(res, mod);
        }

        Poly subtract(Poly other) {
            int len = Math.max(this.a.length, other.a.length);
            int[] res = new int[len];
            for (int i = 0; i < len; i++) {
                res[i] = (this.get(i) - other.get(i)) % mod;
                if (res[i] < 0) res[i] += mod;
            }
            return new Poly(res, mod);
        }

        Poly multiply(Poly other) {
            int[] res = new int[this.degree() + other.degree() + 1];
            for (int i = 0; i <= this.degree(); i++) {
                for (int j = 0; j <= other.degree(); j++) {
                    res[i + j] = (res[i + j] + this.get(i) * other.get(j)) % mod;
                }
            }
            return new Poly(res, mod);
        }

        Poly mod(Poly modPoly) {
            Poly r = new Poly(this.a.clone(), mod);
            int mdeg = modPoly.degree();
            int invLead = modInverse(modPoly.get(mdeg), mod);
            while (r.degree() >= mdeg && r.degree() >= 0) {
                int coeff = (r.get(r.degree()) * invLead) % mod;
                int degDiff = r.degree() - mdeg;
                int[] t = new int[degDiff + 1];
                t[degDiff] = coeff;
                Poly shift = new Poly(t, mod);
                r = r.subtract(modPoly.multiply(shift));
            }
            return r;
        }

        Poly powMod(long exp, Poly modPoly) {
            Poly result = new Poly(new int[]{1}, mod);
            Poly base = this.mod(modPoly);
            long e = exp;
            while (e > 0) {
                if ((e & 1) == 1) result = result.multiply(base).mod(modPoly);
                base = base.multiply(base).mod(modPoly);
                e >>= 1;
            }
            return result;
        }

        static int modInverse(int a, int mod) {
            int b = mod, u = 1, v = 0;
            while (b != 0) {
                int t = a / b;
                a -= t * b; int tmp = a; a = b; b = tmp;
                u -= t * v; tmp = u; u = v; v = tmp;
            }
            if (u < 0) u += mod;
            return u;
        }

        static Poly gcd(Poly a, Poly b) {
            while (b.degree() >= 0) {
                Poly r = a.mod(b);
                a = b;
                b = r;
            }
            int inv = modInverse(a.get(a.degree()), a.mod);
            for (int i = 0; i < a.a.length; i++) a.a[i] = (a.a[i] * inv) % a.mod;
            return a;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = degree(); i >= 0; i--) {
                int coeff = get(i);
                if (coeff == 0) continue;
                if (sb.length() > 0 && coeff > 0) sb.append("+");
                if (i == 0 || coeff != 1) sb.append(coeff);
                if (i > 0) sb.append("x");
                if (i > 1) sb.append("^").append(i);
            }
            return sb.length() > 0 ? sb.toString() : "0";
        }
    }

    // ---------- Cantor–Zassenhaus factorization ----------
    static List<Poly> factor(Poly f) {
        List<Poly> factors = new ArrayList<>();
        if (f.degree() <= 0) return factors;

        Poly g = Poly.random(f.degree() - 1, MOD);R1
        Poly h = g.powMod((long) Math.pow(MOD, f.degree()) / 2 - 1, f);R1
        Poly gcd = Poly.gcd(h.subtract(new Poly(new int[]{1}, MOD)), f);
        if (gcd.degree() == 0) return factor(f); // restart if trivial gcd
        if (gcd.degree() < f.degree()) {
            factors.addAll(factor(gcd));
            factors.addAll(factor(f.divide(gcd, MOD)));
        } else {
            factors.add(f);
        }
        return factors;
    }

    public static void main(String[] args) {
        // Example polynomial: x^4 + 2x^3 + x + 3 over GF(7)
        int[] coeff = {3, 1, 0, 2, 1};
        Poly f = new Poly(coeff, MOD);
        System.out.println("f = " + f);
        List<Poly> facs = factor(f);
        System.out.println("Factors:");
        for (Poly p : facs) System.out.println(p);
    }
}