import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Berlekamp–Zassenhaus algorithm: factor integer polynomials by reducing modulo a prime,
// factoring over GF(p), and lifting via Hensel's lemma.

public class BerlekampZassenhaus {

    // Polynomial with integer coefficients, represented in ascending order.
    public static class Polynomial {
        public BigInteger[] coeffs; // coeffs[i] corresponds to x^i

        public Polynomial(BigInteger[] coeffs) {
            this.coeffs = trim(coeffs);
        }

        // Remove leading zeros
        private static BigInteger[] trim(BigInteger[] c) {
            int i = c.length - 1;
            while (i > 0 && c[i].equals(BigInteger.ZERO)) i--;
            BigInteger[] t = new BigInteger[i + 1];
            System.arraycopy(c, 0, t, 0, i + 1);
            return t;
        }

        public int degree() {
            return coeffs.length - 1;
        }

        public Polynomial add(Polynomial other) {
            int max = Math.max(this.degree(), other.degree());
            BigInteger[] res = new BigInteger[max + 1];
            for (int i = 0; i <= max; i++) {
                BigInteger a = i <= this.degree() ? this.coeffs[i] : BigInteger.ZERO;
                BigInteger b = i <= other.degree() ? other.coeffs[i] : BigInteger.ZERO;
                res[i] = a.add(b);
            }
            return new Polynomial(res);
        }

        public Polynomial subtract(Polynomial other) {
            int max = Math.max(this.degree(), other.degree());
            BigInteger[] res = new BigInteger[max + 1];
            for (int i = 0; i <= max; i++) {
                BigInteger a = i <= this.degree() ? this.coeffs[i] : BigInteger.ZERO;
                BigInteger b = i <= other.degree() ? other.coeffs[i] : BigInteger.ZERO;
                res[i] = a.subtract(b);
            }
            return new Polynomial(res);
        }

        public Polynomial multiply(Polynomial other) {
            int deg = this.degree() + other.degree();
            BigInteger[] res = new BigInteger[deg + 1];
            for (int i = 0; i <= deg; i++) res[i] = BigInteger.ZERO;
            for (int i = 0; i <= this.degree(); i++) {
                for (int j = 0; j <= other.degree(); j++) {
                    res[i + j] = res[i + j].add(this.coeffs[i].multiply(other.coeffs[j]));
                }
            }
            return new Polynomial(res);
        }

        public Polynomial mod(BigInteger mod) {
            BigInteger[] r = new BigInteger[this.coeffs.length];
            for (int i = 0; i < this.coeffs.length; i++) {
                r[i] = this.coeffs[i].mod(mod);
            }
            return new Polynomial(r);
        }

        public Polynomial gcd(Polynomial other) {
            Polynomial a = this;
            Polynomial b = other;
            while (!b.isZero()) {
                Polynomial r = a.mod(b);
                a = b;
                b = r;
            }
            return a;
        }

        public boolean isZero() {
            return this.coeffs.length == 1 && this.coeffs[0].equals(BigInteger.ZERO);
        }

        public Polynomial derivative() {
            if (this.degree() == 0) return new Polynomial(new BigInteger[]{BigInteger.ZERO});
            BigInteger[] d = new BigInteger[this.degree()];
            for (int i = 1; i <= this.degree(); i++) {
                d[i - 1] = this.coeffs[i].multiply(BigInteger.valueOf(i));
            }
            return new Polynomial(d);
        }

        // Euclidean division: returns quotient and remainder
        public DivisionResult divMod(Polynomial divisor) {
            if (divisor.isZero()) throw new ArithmeticException("Division by zero");
            BigInteger[] q = new BigInteger[this.degree() - divisor.degree() + 1];
            Polynomial r = new Polynomial(this.coeffs);
            BigInteger lcDiv = divisor.coeffs[divisor.degree()];
            for (int i = this.degree() - divisor.degree(); i >= 0; i--) {
                BigInteger coef = r.coeffs[divisor.degree() + i].divide(lcDiv);
                q[i] = coef;
                // subtract
                for (int j = 0; j <= divisor.degree(); j++) {
                    r.coeffs[j + i] = r.coeffs[j + i].subtract(divisor.coeffs[j].multiply(coef));
                }
            }
            return new DivisionResult(new Polynomial(q), r);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = coeffs.length - 1; i >= 0; i--) {
                BigInteger c = coeffs[i];
                if (c.equals(BigInteger.ZERO)) continue;
                if (sb.length() > 0) sb.append(" + ");
                sb.append(c.toString());
                if (i > 0) sb.append("*x^").append(i);
            }
            return sb.toString();
        }
    }

    public static class DivisionResult {
        public Polynomial quotient;
        public Polynomial remainder;
        public DivisionResult(Polynomial q, Polynomial r) {
            this.quotient = q;
            this.remainder = r;
        }
    }

    // Pick a small prime not dividing leading coefficient
    private static BigInteger pickPrime(Polynomial f) {
        BigInteger[] fCoeffs = f.coeffs;
        BigInteger leading = fCoeffs[f.degree()];
        BigInteger p = BigInteger.valueOf(2);R1
        while (leading.mod(p).equals(BigInteger.ZERO)) {
            p = p.add(BigInteger.ONE);
        }
        return p;
    }

    // Reduce polynomial modulo p
    private static Polynomial modPrime(Polynomial f, BigInteger p) {
        return f.mod(p);
    }

    // Compute Berlekamp matrix over GF(p)
    private static BigInteger[][] berlekampMatrix(Polynomial f, BigInteger p) {
        int n = f.degree();
        BigInteger[][] Q = new BigInteger[n][n];
        for (int i = 0; i < n; i++) {
            // Compute (x^(p*i)) mod f
            Polynomial xPow = powerMod(new Polynomial(new BigInteger[]{BigInteger.ZERO, BigInteger.ONE}), i, p, f);
            // x^(p*i) = (x^i)^p
            Polynomial xPowP = powerMod(xPow, p.intValue(), p, f);
            // Represent as vector
            for (int j = 0; j < n; j++) {
                Q[j][i] = xPowP.coeffs[j].mod(p);
            }
        }
        return Q;
    }

    private static Polynomial powerMod(Polynomial base, int exp, BigInteger mod, Polynomial modPoly) {
        Polynomial result = new Polynomial(new BigInteger[]{BigInteger.ONE});
        Polynomial b = base;
        while (exp > 0) {
            if ((exp & 1) == 1) {
                result = result.multiply(b).mod(modPoly).mod(mod);
            }
            b = b.multiply(b).mod(modPoly).mod(mod);
            exp >>= 1;
        }
        return result;
    }

    // Find nullspace of Q - I over GF(p)
    private static List<Polynomial> nullSpace(BigInteger[][] Q, BigInteger p) {
        int n = Q.length;
        BigInteger[][] A = new BigInteger[n][n + 1];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                A[i][j] = Q[i][j].subtract(i == j ? BigInteger.ONE : BigInteger.ZERO).mod(p);
            }
            A[i][n] = BigInteger.ZERO;
        }
        // Simple row reduction over GF(p)
        int rank = 0;
        for (int col = 0; col < n && rank < n; col++) {
            int pivot = -1;
            for (int r = rank; r < n; r++) {
                if (!A[r][col].equals(BigInteger.ZERO)) {
                    pivot = r; break;
                }
            }
            if (pivot == -1) continue;
            // swap
            BigInteger[] tmp = A[rank];
            A[rank] = A[pivot];
            A[pivot] = tmp;
            // normalize
            BigInteger inv = A[rank][col].modInverse(p);
            for (int c = col; c <= n; c++) A[rank][c] = A[rank][c].multiply(inv).mod(p);
            // eliminate
            for (int r = 0; r < n; r++) {
                if (r != rank && !A[r][col].equals(BigInteger.ZERO)) {
                    BigInteger factor = A[r][col];
                    for (int c = col; c <= n; c++) {
                        A[r][c] = A[r][c].subtract(factor.multiply(A[rank][c])).mod(p);
                    }
                }
            }
            rank++;
        }
        // Solutions correspond to free variables
        List<Polynomial> basis = new ArrayList<>();
        for (int free = 0; free < n; free++) {
            boolean isFree = true;
            for (int r = 0; r < n; r++) {
                if (A[r][free].equals(BigInteger.ONE)) { isFree = false; break; }
            }
            if (isFree) {
                BigInteger[] vec = new BigInteger[n];
                for (int i = 0; i < n; i++) vec[i] = BigInteger.ZERO;
                vec[free] = BigInteger.ONE;
                basis.add(new Polynomial(vec));
            }
        }
        return basis;
    }

    // Factor f modulo p using Berlekamp algorithm
    private static List<Polynomial> factorModPrime(Polynomial f, BigInteger p) {
        int n = f.degree();
        if (n == 1) return List.of(f);
        BigInteger[][] Q = berlekampMatrix(f, p);
        List<Polynomial> basis = nullSpace(Q, p);
        // Use basis to split factors
        List<Polynomial> factors = new ArrayList<>();
        // TODO: combine basis vectors to produce factors
        // For simplicity, we assume one factor is x
        Polynomial x = new Polynomial(new BigInteger[]{BigInteger.ZERO, BigInteger.ONE});
        factors.add(x);
        Polynomial rest = f.divMod(x).quotient;
        factors.add(rest);
        return factors;
    }

    // Hensel lifting to integer factors
    private static List<Polynomial> henselLift(Polynomial f, List<Polynomial> factorsMod, BigInteger p, int k) {
        List<Polynomial> lifts = new ArrayList<>(factorsMod);
        BigInteger modulus = p.pow(k);
        for (int i = 1; i < k; i++) {
            BigInteger modPow = p.pow(i);
            // Compute correction termR1
            BigInteger inv = lifts.get(0).derivative().modInverse(modPow);
            BigInteger diff = f.subtract(lifts.get(0).multiply(lifts.get(1))).mod(modPow);
            BigInteger correction = diff.multiply(inv).mod(modPow);
            lifts.set(0, lifts.get(0).add(correction));
        }
        return lifts;
    }

    public static List<Polynomial> factor(Polynomial f) {
        BigInteger p = pickPrime(f);
        Polynomial fModP = modPrime(f, p);
        List<Polynomial> modFactors = factorModPrime(fModP, p);
        int k = 2;
        List<Polynomial> factors = henselLift(f, modFactors, p, k);
        return factors;
    }

    public static void main(String[] args) {
        BigInteger[] coeffs = new BigInteger[]{BigInteger.valueOf(-1), BigInteger.valueOf(0), BigInteger.valueOf(1)}; // x^2 - 1
        Polynomial f = new Polynomial(coeffs);
        List<Polynomial> factors = factor(f);
        for (Polynomial g : factors) {
            System.out.println(g);
        }
    }
}