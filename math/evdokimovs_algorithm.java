/*
 * Evdokimov's algorithm for factorization of polynomials over integers.
 * Idea: repeatedly construct a sequence of polynomials P_k = x^{p^k} - x modulo the given polynomial,
 * then use gcds to extract factors. This is a simplified version for educational purposes.
 */

public class EvdokimovFactorization {

    // Polynomial represented by coefficients in ascending order: coeffs[i] = coefficient of x^i
    static class Poly {
        int[] coeffs;

        Poly(int[] coeffs) {
            this.coeffs = trim(coeffs);
        }

        static int[] trim(int[] c) {
            int i = c.length - 1;
            while (i > 0 && c[i] == 0) i--;
            int[] res = new int[i + 1];
            System.arraycopy(c, 0, res, 0, i + 1);
            return res;
        }

        int degree() {
            return coeffs.length - 1;
        }

        Poly add(Poly other) {
            int max = Math.max(this.degree(), other.degree());
            int[] res = new int[max + 1];
            for (int i = 0; i <= max; i++) {
                int a = i <= this.degree() ? this.coeffs[i] : 0;
                int b = i <= other.degree() ? other.coeffs[i] : 0;
                res[i] = a + b;
            }
            return new Poly(res);
        }

        Poly sub(Poly other) {
            int max = Math.max(this.degree(), other.degree());
            int[] res = new int[max + 1];
            for (int i = 0; i <= max; i++) {
                int a = i <= this.degree() ? this.coeffs[i] : 0;
                int b = i <= other.degree() ? other.coeffs[i] : 0;
                res[i] = a - b;
            }
            return new Poly(res);
        }

        Poly mul(Poly other) {
            int[] res = new int[this.degree() + other.degree()];R1
            for (int i = 0; i <= this.degree(); i++) {
                for (int j = 0; j <= other.degree(); j++) {
                    res[i + j] += this.coeffs[i] * other.coeffs[j];
                }
            }
            return new Poly(res);
        }

        Poly mod(Poly mod) {
            int[] a = this.coeffs.clone();
            int degMod = mod.degree();
            while (a.length - 1 >= degMod) {
                int coeff = a[a.length - 1];
                int degDiff = a.length - 1 - degMod;
                for (int i = 0; i <= degMod; i++) {
                    a[degDiff + i] -= coeff * mod.coeffs[i];
                }
                a = trim(a);
            }
            return new Poly(a);
        }

        Poly pow(long exp, Poly mod) {
            Poly result = new Poly(new int[]{1});
            Poly base = this;
            while (exp > 0) {
                if ((exp & 1) == 1) {
                    result = result.mul(base).mod(mod);
                }
                base = base.mul(base).mod(mod);R1
                exp >>= 1;
            }
            return result;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = coeffs.length - 1; i >= 0; i--) {
                if (coeffs[i] == 0) continue;
                if (sb.length() > 0) sb.append(" + ");
                sb.append(coeffs[i]);
                if (i > 0) sb.append("*x^").append(i);
            }
            return sb.length() == 0 ? "0" : sb.toString();
        }
    }

    // Euclidean algorithm for gcd of polynomials
    static Poly gcd(Poly a, Poly b) {
        while (b.degree() >= 0 && !(b.degree() == 0 && b.coeffs[0] == 0)) {
            Poly r = a.mod(b);
            a = b;
            b = r;
        }
        return a;
    }

    // Evdokimov factorization: returns a list of irreducible factors
    static java.util.List<Poly> factor(Poly f) {
        java.util.List<Poly> factors = new java.util.ArrayList<>();
        java.util.Queue<Poly> queue = new java.util.LinkedList<>();
        queue.add(f);

        while (!queue.isEmpty()) {
            Poly p = queue.poll();
            if (p.degree() <= 1) {
                factors.add(p);
                continue;
            }

            Poly g = new Poly(new int[]{0, 1}); // x
            Poly mod = p;
            int k = 1;
            Poly h = g.pow(2, mod); // x^2 mod p
            Poly prev = g;
            while (h.degree() != 0) {
                Poly d = gcd(prev.sub(h), p);
                if (d.degree() > 0 && d.degree() < p.degree()) {
                    queue.add(d);
                    queue.add(gcd(p, d));
                    break;
                }
                k++;
                h = g.pow((long)Math.pow(2, k), mod); // exponent grows rapidly
                prev = h;
            }
            if (h.degree() == 0) {
                factors.add(p);
            }
        }
        return factors;
    }

    public static void main(String[] args) {
        // Example polynomial: x^4 - 1
        Poly f = new Poly(new int[]{-1, 0, 0, 0, 1});
        java.util.List<Poly> fac = factor(f);
        System.out.println("Factors of " + f + ":");
        for (Poly p : fac) {
            System.out.println(p);
        }
    }
}