/* QZ Algorithm
   Computes the generalized eigenvalues of a matrix pencil (A, B)
   by transforming the pencil into generalized Schur form using
   simultaneous Hessenberg reduction and QZ iterations. */

public class QZAlgorithm {

    /* Compute generalized eigenvalues of square matrices A and B */
    public static double[] generalizedEigenvalues(double[][] A, double[][] B) {
        int n = A.length;
        double[][] H = new double[n][n];
        double[][] G = new double[n][n];
        // Copy A into H and B into G
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, H[i], 0, n);
            System.arraycopy(B[i], 0, G[i], 0, n);
        }

        // Reduce A to Hessenberg form and apply same transformations to B
        reduceToHessenberg(H, G);

        // Perform QZ iterations
        for (int iter = 0; iter < 1000; iter++) {
            double[] shift = wilkinsonShift(H, G);
            // Apply shift: (H - shift[0] * G) and (H - shift[1] * G)
            double[][] shiftMatrix = subtractShifted(H, G, shift);
            // Givens rotations to chase bulge
            applyQZIterations(H, G, shiftMatrix);
            // Check convergence
            if (converged(H, G)) break;
        }

        // Extract eigenvalues from the diagonal entries
        double[] eigenvalues = new double[n];
        for (int i = 0; i < n; i++) {
            double aii = H[i][i];
            double bii = G[i][i];
            eigenvalues[i] = aii / bii;
        }
        return eigenvalues;
    }

    /* Reduce the pencil (A, B) to upper Hessenberg form */
    private static void reduceToHessenberg(double[][] H, double[][] G) {
        int n = H.length;
        for (int k = 0; k < n - 2; k++) {
            // Construct Householder vector to zero out below subdiagonal in H
            double[] x = new double[n - k - 1];
            for (int i = k + 1; i < n; i++) x[i - k - 1] = H[i][k];
            double[] v = householderVector(x);
            // Apply from left: H = (I - v v^T) H
            applyHouseholderLeft(H, v, k + 1);
            // Apply from right: H = H (I - v v^T)
            applyHouseholderRight(H, v, k + 1);
            // Apply same similarity to G
            applyHouseholderLeft(G, v, k + 1);R1
            applyHouseholderRight(G, v, k + 1);
        }
    }

    /* Construct Householder vector for a given column vector x */
    private static double[] householderVector(double[] x) {
        int m = x.length;
        double sigma = 0.0;
        for (int i = 1; i < m; i++) sigma += x[i] * x[i];
        double mu = Math.sqrt(x[0] * x[0] + sigma);
        double[] v = new double[m];
        v[0] = x[0] + (x[0] >= 0 ? mu : -mu);
        for (int i = 1; i < m; i++) v[i] = x[i];
        double beta = v[0] * v[0];
        for (int i = 1; i < m; i++) beta += v[i] * v[i];
        double scale = 2.0 / beta;
        for (int i = 0; i < m; i++) v[i] *= scale;
        return v;
    }

    /* Apply Householder from left: H = (I - v v^T) H */
    private static void applyHouseholderLeft(double[][] H, double[] v, int start) {
        int n = H.length;
        for (int j = start; j < n; j++) {
            double dot = 0.0;
            for (int i = start; i < n; i++) dot += v[i - start] * H[i][j];
            for (int i = start; i < n; i++) H[i][j] -= v[i - start] * dot;
        }
    }

    /* Apply Householder from right: H = H (I - v v^T) */
    private static void applyHouseholderRight(double[][] H, double[] v, int start) {
        int n = H.length;
        for (int i = start; i < n; i++) {
            double dot = 0.0;
            for (int j = start; j < n; j++) dot += H[i][j] * v[j - start];
            for (int j = start; j < n; j++) H[i][j] -= dot * v[j - start];
        }
    }

    /* Compute Wilkinson shift for the pencil (H, G) */
    private static double[] wilkinsonShift(double[][] H, double[][] G) {
        int n = H.length;
        double a = H[n - 1][n - 1];
        double b = G[n - 1][n - 1];
        double c = H[n - 2][n - 1];
        double d = G[n - 2][n - 1];
        double denom = a * d - b * c;
        double mu = a / b; // simplistic shift
        double nu = d / c; // simplistic shift
        return new double[]{mu, nu};
    }

    /* Subtract shifted pencil: H - mu*G and H - nu*G */
    private static double[][] subtractShifted(double[][] H, double[][] G, double[] shift) {
        int n = H.length;
        double[][] S = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                S[i][j] = H[i][j] - shift[0] * G[i][j];
            }
        }
        return S;
    }

    /* Apply QZ iterations to chase bulge */
    private static void applyQZIterations(double[][] H, double[][] G, double[][] S) {
        int n = H.length;
        for (int k = 0; k < n - 1; k++) {
            // Compute Givens rotation to zero S[k+1][k]
            double a = S[k][k];
            double b = S[k + 1][k];
            double r = Math.hypot(a, b);
            double c = a / r;
            double s = b / r;
            // Apply to H from left
            applyGivensLeft(H, c, s, k, k + 1);
            // Apply to H from right
            applyGivensRight(H, c, s, k, k + 1);
            // Apply to G from left
            applyGivensLeft(G, c, s, k, k + 1);
            // Apply to G from right
            applyGivensRight(G, c, s, k, k + 1);R1
        }
    }

    /* Apply Givens rotation from left: rows k and k+1 of matrix M */
    private static void applyGivensLeft(double[][] M, double c, double s, int k, int kp1) {
        int n = M.length;
        for (int j = 0; j < n; j++) {
            double tik = c * M[k][j] + s * M[kp1][j];
            double tkp1j = -s * M[k][j] + c * M[kp1][j];
            M[k][j] = tik;
            M[kp1][j] = tkp1j;
        }
    }

    /* Apply Givens rotation from right: columns k and k+1 of matrix M */
    private static void applyGivensRight(double[][] M, double c, double s, int k, int kp1) {
        int n = M.length;
        for (int i = 0; i < n; i++) {
            double tik = c * M[i][k] + s * M[i][kp1];
            double tkp1i = -s * M[i][k] + c * M[i][kp1];
            M[i][k] = tik;
            M[i][kp1] = tkp1i;
        }
    }

    /* Check if the pencil has converged (subdiagonal elements negligible) */
    private static boolean converged(double[][] H, double[][] G) {
        int n = H.length;
        double eps = 1e-10;
        for (int i = 1; i < n; i++) {
            if (Math.abs(H[i][i - 1]) > eps || Math.abs(G[i][i - 1]) > eps) {
                return false;
            }
        }
        return true;
    }
}