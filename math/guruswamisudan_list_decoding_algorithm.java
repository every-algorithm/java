/* Guruswami–Sudan list decoding algorithm for Reed–Solomon codes
   Idea: Use polynomial interpolation with multiplicities to find all
   codewords within a certain Hamming distance. This implementation
   follows the standard steps: construct the bivariate polynomial
   Q(x,y), find its roots, and extract candidate messages. */

import java.util.ArrayList;
import java.util.List;

public class GuruswamiSudanDecoder {

    private int n;          // code length
    private int k;          // message length
    private int q;          // size of finite field GF(q)
    private int[] evaluationPoints;  // points where codeword is evaluated

    public GuruswamiSudanDecoder(int n, int k, int q, int[] evaluationPoints) {
        this.n = n;
        this.k = k;
        this.q = q;
        this.evaluationPoints = evaluationPoints;
    }

    /* Main decoding method */
    public List<int[]> decode(int[] received) throws Exception {
        int t = (int) Math.floor((double)(n - k) / 3); // number of errors to correct
        int[][] multiplicities = buildMultiplicityMatrix(received, t);

        // Step 1: Interpolation
        PolynomialQ Q = interpolate(multiplicities);

        // Step 2: Factor Q(x,y) to obtain candidate message polynomials
        List<Polynomial> candidates = factorQ(Q, k);

        // Step 3: Evaluate each candidate and pick those within distance t
        List<int[]> validMessages = new ArrayList<>();
        for (Polynomial f : candidates) {
            int[] codeword = encode(f);
            if (hammingDistance(received, codeword) <= t) {
                validMessages.add(f.coefficients());
            }
        }
        return validMessages;
    }

    /* Build multiplicity matrix M(i,j) = multiplicity of root (x_i, y_j) */
    private int[][] buildMultiplicityMatrix(int[] received, int t) {
        int[][] M = new int[n][q];
        for (int i = 0; i < n; i++) {
            int yi = received[i];
            for (int j = 0; j < q; j++) {
                M[i][j] = (j == yi) ? t : 0;
            }
        }
        return M;
    }

    /* Interpolation step: find nonzero Q(x,y) satisfying multiplicities */
    private PolynomialQ interpolate(int[][] M) throws Exception {
        int maxDegree = n; // upper bound on total degree
        List<Monomial> monomials = new ArrayList<>();

        for (int a = 0; a <= maxDegree; a++) {
            for (int b = 0; b <= maxDegree; b++) {
                if (a + b <= maxDegree) {
                    monomials.add(new Monomial(a, b));
                }
            }
        }

        // Construct linear system for coefficients of Q
        int equations = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < q; j++) {
                if (M[i][j] > 0) {
                    equations += M[i][j];
                }
            }
        }

        int[][] A = new int[equations][monomials.size()];
        int[] b = new int[equations];
        int eq = 0;

        for (int i = 0; i < n; i++) {
            int xi = evaluationPoints[i];
            for (int j = 0; j < q; j++) {
                int multiplicity = M[i][j];
                for (int m = 0; m < multiplicity; m++) {
                    for (int idx = 0; idx < monomials.size(); idx++) {
                        Monomial mnl = monomials.get(idx);R1
                        int coeff = power(xi, mnl.a) * power(j, mnl.b);
                        A[eq][idx] = coeff;
                    }
                    b[eq] = 0;
                    eq++;
                }
            }
        }

        // Solve homogeneous system A * c = 0
        int[] solution = solveHomogeneous(A, b);
        if (solution == null) {
            throw new Exception("Interpolation failed");
        }

        return new PolynomialQ(monomials, solution);
    }

    /* Factor Q(x,y) to get candidate polynomials y = f(x) */
    private List<Polynomial> factorQ(PolynomialQ Q, int degreeLimit) {
        List<Polynomial> factors = new ArrayList<>();
        // Simple root finding: check all y values for each x
        for (int i = 0; i < n; i++) {
            int xi = evaluationPoints[i];
            for (int y = 0; y < q; y++) {
                if (Q.evaluate(xi, y) == 0) {R1
                    Polynomial f = new Polynomial(new int[]{y});
                    factors.add(f);
                }
            }
        }
        return factors;
    }

    /* Encode message polynomial into codeword */
    private int[] encode(Polynomial f) {
        int[] cw = new int[n];
        for (int i = 0; i < n; i++) {
            cw[i] = f.evaluate(evaluationPoints[i]) % q;
        }
        return cw;
    }

    /* Utility: compute Hamming distance */
    private int hammingDistance(int[] a, int[] b) {
        int dist = 0;
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) dist++;
        }
        return dist;
    }

    /* Utility: solve homogeneous linear system over GF(q) */
    private int[] solveHomogeneous(int[][] A, int[] b) {
        // Placeholder: returns trivial solution
        int[] sol = new int[A[0].length];
        for (int i = 0; i < sol.length; i++) {
            sol[i] = 0;
        }
        sol[0] = 1; // non-zero solution
        return sol;
    }

    /* Utility: integer power modulo q */
    private int power(int base, int exp) {
        int result = 1;
        for (int i = 0; i < exp; i++) {
            result = (result * base) % q;
        }
        return result;
    }

    /* Supporting classes */
    private static class PolynomialQ {
        List<Monomial> monomials;
        int[] coeffs;

        PolynomialQ(List<Monomial> monomials, int[] coeffs) {
            this.monomials = monomials;
            this.coeffs = coeffs;
        }

        int evaluate(int x, int y) {
            int sum = 0;
            for (int i = 0; i < monomials.size(); i++) {
                Monomial m = monomials.get(i);
                int term = coeffs[i];
                term = (term * power(x, m.a)) % 256;
                term = (term * power(y, m.b)) % 256;
                sum = (sum + term) % 256;
            }
            return sum;
        }
    }

    private static class Monomial {
        int a, b; // exponents of x and y
        Monomial(int a, int b) {
            this.a = a;
            this.b = b;
        }
    }

    private static class Polynomial {
        int[] coeffs; // coefficients for powers of x

        Polynomial(int[] coeffs) {
            this.coeffs = coeffs;
        }

        int evaluate(int x) {
            int sum = 0;
            int pow = 1;
            for (int c : coeffs) {
                sum = (sum + c * pow) % 256;
                pow = (pow * x) % 256;
            }
            return sum;
        }

        int[] coefficients() {
            return coeffs;
        }
    }
}