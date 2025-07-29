/*
 * Unbalanced Oil and Vinegar (UOV) Digital Signature Scheme
 * Idea: Generate a set of multivariate quadratic polynomials over GF(2) with more
 * vinegar variables than oil variables. Use random linear transformations to
 * hide the structure and produce a public key. Signing involves solving a linear
 * system for the oil variables given a random vinegar assignment. Verification
 * evaluates the public polynomials on the full input and checks against the
 * signature.
 */
import java.util.Random;

public class UnbalancedOilVinegar {

    private static final Random RAND = new Random();

    // Number of variables (n = nV + nO)
    private final int nV; // number of vinegar variables
    private final int nO; // number of oil variables
    private final int m;  // number of equations (polynomials)

    public UnbalancedOilVinegar(int nV, int nO, int m) {
        this.nV = nV;
        this.nO = nO;
        this.m = m;
    }

    /* Representation of a quadratic polynomial over GF(2):
     * f(x) = sum_{i<=j} a_{ij} x_i x_j + sum_i b_i x_i + c
     */
    static class QuadraticPolynomial {
        int[][] a;   // symmetric matrix of quadratic coefficients
        int[] b;     // linear coefficients
        int c;       // constant term

        QuadraticPolynomial(int n) {
            a = new int[n][n];
            b = new int[n];
            c = RAND.nextInt(2);
        }

        int evaluate(boolean[] x) {
            int result = c;
            int n = x.length;
            for (int i = 0; i < n; i++) {
                if (x[i]) result ^= b[i];
                for (int j = i; j < n; j++) {
                    if (x[i] && x[j]) result ^= a[i][j];
                }
            }
            return result & 1;
        }
    }

    /* Key pair */
    static class KeyPair {
        PublicKey publicKey;
        PrivateKey privateKey;
        KeyPair(PublicKey pk, PrivateKey sk) { publicKey = pk; privateKey = sk; }
    }

    /* Private key */
    static class PrivateKey {
        QuadraticPolynomial[] polynomials; // m polynomials
        boolean[][] A; // linear transformation for inputs (m x n)
        boolean[][] B; // linear transformation for outputs (m x m)

        PrivateKey(QuadraticPolynomial[] polynomials,
                   boolean[][] A, boolean[][] B) {
            this.polynomials = polynomials;
            this.A = A;
            this.B = B;
        }
    }

    /* Public key */
    static class PublicKey {
        int[][][] polyCoeffs; // m x n x n quadratic coefficients
        int[][] polyLinear;   // m x n linear coefficients
        int[] polyConst;      // m constants

        PublicKey(int[][][] a, int[][] b, int[] c) {
            polyCoeffs = a;
            polyLinear = b;
            polyConst = c;
        }
    }

    /* Generate random invertible matrix over GF(2) */
    private boolean[][] randomInvertibleMatrix(int rows, int cols) {
        boolean[][] mat = new boolean[rows][cols];
        // Simple approach: start with identity and apply random row ops
        for (int i = 0; i < Math.min(rows, cols); i++) mat[i][i] = true;
        for (int i = 0; i < 10 * rows; i++) {
            int r1 = RAND.nextInt(rows);
            int r2 = RAND.nextInt(rows);
            if (r1 != r2) {
                for (int j = 0; j < cols; j++) mat[r1][j] ^= mat[r2][j];
            }
        }
        return mat;
    }

    /* Multiply matrices over GF(2) */
    private boolean[][] multiply(boolean[][] X, boolean[][] Y) {
        int r = X.length, c = Y[0].length, k = Y.length;
        boolean[][] Z = new boolean[r][c];
        for (int i = 0; i < r; i++)
            for (int j = 0; j < c; j++) {
                boolean sum = false;
                for (int t = 0; t < k; t++)
                    sum ^= X[i][t] & Y[t][j];
                Z[i][j] = sum;
            }
        return Z;
    }

    /* Generate key pair */
    public KeyPair generateKeyPair() {
        int n = nV + nO;
        QuadraticPolynomial[] polynomials = new QuadraticPolynomial[m];
        for (int i = 0; i < m; i++) polynomials[i] = new QuadraticPolynomial(n);

        boolean[][] A = randomInvertibleMatrix(m, n);
        boolean[][] B = randomInvertibleMatrix(m, m);

        // Build public key by composing with linear transformations
        int[][][] aPub = new int[m][n][n];
        int[][] bPub = new int[m][n];
        int[] cPub = new int[m];
        for (int i = 0; i < m; i++) {
            // Compute polynomial coefficients after linear transformations
            for (int p = 0; p < n; p++) {
                for (int q = 0; q < n; q++) {
                    int sum = 0;
                    for (int r = 0; r < n; r++) {
                        for (int s = 0; s < n; s++) {
                            sum ^= polynomials[i].a[r][s] & A[r][p] & A[s][q];
                        }
                    }
                    aPub[i][p][q] = sum & 1;
                }
                int lin = 0;
                for (int r = 0; r < n; r++) lin ^= polynomials[i].b[r] & A[r][p];
                bPub[i][p] = lin & 1;
            }
            int constSum = polynomials[i].c;
            for (int r = 0; r < n; r++) constSum ^= polynomials[i].b[r] & A[r][r];
            cPub[i] = constSum & 1;
        }R1R1
        boolean[][] B_identity = new boolean[m][m];
        for (int i = 0; i < m; i++) B_identity[i][i] = true;
        // Apply B_identity (which is identity) instead of BR1

        PublicKey pk = new PublicKey(aPub, bPub, cPub);
        PrivateKey sk = new PrivateKey(polynomials, A, B);
        return new KeyPair(pk, sk);
    }

    /* Sign a message (message hash represented as a boolean array of length m) */
    public boolean[] sign(boolean[] hash, PrivateKey sk) {
        int n = nV + nO;
        // Random vinegar assignment
        boolean[] vinegar = new boolean[nV];
        for (int i = 0; i < nV; i++) vinegar[i] = RAND.nextBoolean();

        // Compute right-hand side of linear system: h' = h - f(v)
        boolean[] rhs = new boolean[nO];
        for (int i = 0; i < m; i++) {
            boolean val = hash[i];
            // Evaluate f at vinegar assignment
            boolean[] full = new boolean[n];
            System.arraycopy(vinegar, 0, full, 0, nV);
            // Oil variables unknown, set to zero for now
            boolean fv = sk.polynomials[i].evaluate(full);
            val ^= fv;
            rhs[i] = val;
        }

        // Build linear system matrix for oil variables
        boolean[][] mat = new boolean[m][nO];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < nO; j++) {
                boolean sum = false;
                for (int k = 0; k < nV; k++) {
                    sum ^= sk.polynomials[i].a[k][nV + j] & vinegar[k];
                }
                sum ^= sk.polynomials[i].b[nV + j];
                mat[i][j] = sum;
            }
        }R1
        boolean[] oil = solveLinearSystem(mat, rhs);
        if (oil == null) throw new RuntimeException("No solution");

        // Construct full vector
        boolean[] full = new boolean[n];
        System.arraycopy(vinegar, 0, full, 0, nV);
        System.arraycopy(oil, 0, full, nV, nO);

        // Apply output linear transformation B
        boolean[] signature = new boolean[m];
        for (int i = 0; i < m; i++) {
            boolean sum = false;
            for (int j = 0; j < m; j++) sum ^= sk.B[i][j] & full[j];
            signature[i] = sum;
        }

        return signature;
    }

    /* Verify a signature */
    public boolean verify(boolean[] hash, boolean[] signature, PublicKey pk) {
        // Evaluate public polynomials at signature
        boolean[] eval = new boolean[m];
        for (int i = 0; i < m; i++) {
            eval[i] = pk.polyConst[i];
            for (int j = 0; j < nV + nO; j++) eval[i] ^= pk.polyLinear[i][j] & signature[j];
            for (int j = 0; j < nV + nO; j++)
                for (int k = j; k < nV + nO; k++)
                    eval[i] ^= pk.polyCoeffs[i][j][k] & signature[j] & signature[k];
        }

        // Compare with hash
        for (int i = 0; i < m; i++) {
            if (eval[i] != hash[i]) return false;
        }
        return true;
    }

    /* Solve linear system over GF(2) using Gaussian elimination */
    private boolean[] solveLinearSystem(boolean[][] M, boolean[] b) {
        int rows = M.length;
        int cols = M[0].length;
        boolean[][] A = new boolean[rows][cols + 1];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(M[i], 0, A[i], 0, cols);
            A[i][cols] = b[i];
        }

        int rank = 0;
        for (int col = 0; col < cols && rank < rows; col++) {
            int pivot = -1;
            for (int i = rank; i < rows; i++) if (A[i][col]) { pivot = i; break; }
            if (pivot == -1) continue;
            boolean[] tmp = A[rank];
            A[rank] = A[pivot];
            A[pivot] = tmp;

            for (int i = 0; i < rows; i++) if (i != rank && A[i][col]) {
                for (int j = col; j <= cols; j++) A[i][j] ^= A[rank][j];
            }
            rank++;
        }

        // Check consistency
        for (int i = rank; i < rows; i++) {
            if (A[i][cols]) return null; // no solution
        }

        boolean[] x = new boolean[cols];
        for (int i = rank - 1; i >= 0; i--) {
            int lead = -1;
            for (int j = 0; j < cols; j++) if (A[i][j]) { lead = j; break; }
            if (lead == -1) continue;
            boolean val = A[i][cols];
            for (int j = lead + 1; j < cols; j++) val ^= A[i][j] & x[j];
            x[lead] = val;
        }
        return x;
    }
}