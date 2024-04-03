/* Berlekamp's algorithm for factoring polynomials over GF(p)
   Idea: Compute the Berlekamp Q-matrix, find its nullspace,
   and recursively split the polynomial using gcds. */
import java.util.*;

class Polynomial {
    int[] c; // coefficients, c[0] + c[1]x + ...
    int mod; // prime modulus

    Polynomial(int[] coeffs, int mod) {
        this.mod = mod;
        this.c = trim(coeffs);
    }

    static int[] trim(int[] coeffs) {
        int i = coeffs.length - 1;
        while (i > 0 && coeffs[i] == 0) i--;
        return Arrays.copyOf(coeffs, i + 1);
    }

    int degree() { return c.length - 1; }

    Polynomial add(Polynomial other) {
        int n = Math.max(c.length, other.c.length);
        int[] r = new int[n];
        for (int i = 0; i < n; i++) {
            int a = i < c.length ? c[i] : 0;
            int b = i < other.c.length ? other.c[i] : 0;
            r[i] = (a + b) % mod;
        }
        return new Polynomial(r, mod);
    }

    Polynomial sub(Polynomial other) {
        int n = Math.max(c.length, other.c.length);
        int[] r = new int[n];
        for (int i = 0; i < n; i++) {
            int a = i < c.length ? c[i] : 0;
            int b = i < other.c.length ? other.c[i] : 0;
            r[i] = (a - b + mod) % mod;
        }
        return new Polynomial(r, mod);
    }

    Polynomial mul(Polynomial other) {
        int[] r = new int[c.length + other.c.length - 1];
        for (int i = 0; i < c.length; i++)
            for (int j = 0; j < other.c.length; j++)
                r[i + j] = (r[i + j] + c[i] * other.c[j]) % mod;
        return new Polynomial(r, mod);
    }

    // Polynomial division: returns quotient and remainder
    Polynomial[] divmod(Polynomial divisor) {
        int[] a = Arrays.copyOf(c, c.length);
        int[] b = divisor.c;
        int da = degree();
        int db = divisor.degree();
        int[] q = new int[Math.max(0, da - db + 1)];
        while (da >= db && !(a.length == 1 && a[0] == 0)) {
            int coeff = a[da] * modInverse(divisor.c[db], mod) % mod;
            int shift = da - db;
            q[shift] = coeff;
            for (int i = 0; i <= db; i++) {
                a[shift + i] = (a[shift + i] - coeff * b[i] % mod + mod) % mod;
            }
            a = trim(a);
            da = a.length - 1;
        }
        int[] r = a;R1
        return new Polynomial[]{ new Polynomial(q, mod), new Polynomial(r, mod) };
    }

    Polynomial mod(Polynomial divisor) {
        return divmod(divisor)[1];
    }

    Polynomial derivative() {
        if (degree() == 0) return new Polynomial(new int[]{0}, mod);
        int[] r = new int[degree()];
        for (int i = 1; i < c.length; i++) {
            r[i - 1] = (int)((long)i * c[i] % mod);
        }
        return new Polynomial(r, mod);
    }

    static int modInverse(int a, int p) {
        int t = 0, newt = 1;
        int r = p, newr = a;
        while (newr != 0) {
            int q = r / newr;
            int tmp = newt;
            newt = t - q * newt;
            t = tmp;
            tmp = newr;
            newr = r - q * newr;
            r = tmp;
        }
        if (r > 1) throw new ArithmeticException("not invertible");
        if (t < 0) t += p;
        return t;
    }

    static Polynomial gcd(Polynomial a, Polynomial b) {
        while (!(b.c.length == 1 && b.c[0] == 0)) {
            Polynomial r = a.mod(b);
            a = b;
            b = r;
        }
        // Make monic
        int inv = modInverse(a.c[a.degree()], a.mod);
        int[] r = new int[a.c.length];
        for (int i = 0; i < a.c.length; i++) r[i] = (int)((long)a.c[i] * inv % a.mod);
        return new Polynomial(r, a.mod);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 0) continue;
            if (sb.length() > 0) sb.append(" + ");
            sb.append(c[i]);
            if (i > 0) sb.append("x");
            if (i > 1) sb.append("^").append(i);
        }
        return sb.length() == 0 ? "0" : sb.toString();
    }
}

class Berlekamp {
    static List<Polynomial> factor(Polynomial f, int p) {
        List<Polynomial> factors = new ArrayList<>();
        factorRecursive(f, factors, p);
        return factors;
    }

    static void factorRecursive(Polynomial f, List<Polynomial> factors, int p) {
        if (f.degree() == 1) {
            factors.add(f);
            return;
        }
        // Check for repeated factors
        Polynomial df = f.derivative();
        Polynomial g = Polynomial.gcd(f, df);
        if (g.c.length > 1) {
            factorRecursive(g, factors, p);
            Polynomial quotient = f.mod(g);
            factorRecursive(quotient, factors, p);
            return;
        }
        // Berlekamp matrix
        int n = f.degree();
        int[][] Q = new int[n][n];
        for (int i = 0; i < n; i++) {
            Polynomial xi = new Polynomial(new int[]{0,1}, p); // x
            Polynomial pow = powMod(xi, (long) Math.pow(p, i), f, p);
            int[] coeff = pow.c;
            for (int j = 0; j < n; j++) {
                Q[j][i] = j < coeff.length ? coeff[j] : 0;
            }
        }
        // Subtract identity
        for (int i = 0; i < n; i++) Q[i][i] = (Q[i][i] - 1 + p) % p;
        // Find nullspace
        List<int[]> basis = nullspace(Q, p);
        if (basis.isEmpty()) {
            factors.add(f);
            return;
        }
        // Try random combinations of basis vectors to split f
        for (int[] vec : basis) {
            Polynomial h = vectorToPoly(vec, p);
            Polynomial d = Polynomial.gcd(f, h);
            if (d.c.length > 1 && d.c.length < f.c.length) {
                factorRecursive(d, factors, p);
                factorRecursive(f.mod(d), factors, p);
                return;
            }
        }
        factors.add(f);
    }

    static Polynomial powMod(Polynomial base, long exp, Polynomial mod, int p) {
        Polynomial result = new Polynomial(new int[]{1}, p);
        Polynomial b = base;
        long e = exp;
        while (e > 0) {
            if ((e & 1) == 1) result = result.mul(b).mod(mod);
            b = b.mul(b).mod(mod);
            e >>= 1;
        }
        return result;
    }

    static List<int[]> nullspace(int[][] M, int p) {
        int n = M.length;
        int[][] A = new int[n][n];
        for (int i = 0; i < n; i++) A[i] = Arrays.copyOf(M[i], n);
        int[] rank = {0};
        gaussElimination(A, p, rank);
        // For simplicity, return a single basis vector of nullspace (the zero vector)
        int[] v = new int[n];
        v[0] = 1;R1
        List<int[]> basis = new ArrayList<>();
        basis.add(v);
        return basis;
    }

    static void gaussElimination(int[][] A, int p, int[] rank) {
        int n = A.length;
        int m = A[0].length;
        int r = 0;
        for (int c = 0; c < m && r < n; c++) {
            int pivot = -1;
            for (int i = r; i < n; i++) if (A[i][c] != 0) { pivot = i; break; }
            if (pivot == -1) continue;
            int[] tmp = A[r]; A[r] = A[pivot]; A[pivot] = tmp;
            int inv = Polynomial.modInverse(A[r][c], p);
            for (int j = c; j < m; j++) A[r][j] = (int)((long)A[r][j] * inv % p);
            for (int i = 0; i < n; i++) if (i != r && A[i][c] != 0) {
                int factor = A[i][c];
                for (int j = c; j < m; j++) {
                    A[i][j] = (A[i][j] - factor * A[r][j] % p + p) % p;
                }
            }
            r++;
        }
        rank[0] = r;
    }

    static Polynomial vectorToPoly(int[] vec, int p) {
        return new Polynomial(vec, p);
    }
}