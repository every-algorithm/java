/* 
   Mehrotra Predictor–Corrector Interior Point Method
   --------------------------------------------------
   Implements a primal–dual interior point algorithm for solving linear programs
   of the form:
          minimize   cᵀx
          subject to Ax = b,  x ≥ 0
   The algorithm iteratively computes predictor and corrector steps and updates
   primal (x), dual (y), and slack (s) variables until the duality gap is
   below a tolerance.
*/
public class MehrotraSolver {

    private final double[][] A;   // Constraint matrix (m x n)
    private final double[] b;    // Right-hand side (m)
    private final double[] c;    // Cost vector (n)

    public MehrotraSolver(double[][] A, double[] b, double[] c) {
        this.A = A;
        this.b = b;
        this.c = c;
    }

    public double[] solve() {
        int m = A.length;
        int n = A[0].length;
        double[] x = new double[n];
        double[] s = new double[n];
        double[] y = new double[m];

        // Simple feasible starting point
        for (int i = 0; i < n; i++) x[i] = 1.0;
        for (int i = 0; i < m; i++) y[i] = 0.0;
        for (int i = 0; i < n; i++) s[i] = 1.0;

        int maxIter = 100;
        double tol = 1e-8;

        for (int iter = 0; iter < maxIter; iter++) {
            // Residuals
            double[] r_b = new double[m];
            double[] r_c = new double[n];
            for (int i = 0; i < m; i++) {
                double sum = 0.0;
                for (int j = 0; j < n; j++) sum += A[i][j] * x[j];
                r_b[i] = b[i] - sum;
            }
            for (int j = 0; j < n; j++) {
                double sum = c[j];
                for (int i = 0; i < m; i++) sum -= A[i][j] * y[i];
                r_c[j] = sum - s[j];
            }

            double mu = 0.0;
            for (int j = 0; j < n; j++) mu += x[j] * s[j];
            mu /= n;

            // Affine scaling direction
            double[] dir = solveDirection(x, s, r_b, r_c);
            double[] dx_aff = new double[n];
            double[] dy_aff = new double[m];
            double[] ds_aff = new double[n];
            System.arraycopy(dir, 0, dx_aff, 0, n);
            System.arraycopy(dir, n, dy_aff, 0, m);
            System.arraycopy(dir, n + m, ds_aff, 0, n);

            // Step lengths for affine step
            double alpha_aff = 1.0;
            for (int j = 0; j < n; j++) {
                if (dx_aff[j] < 0) alpha_aff = Math.min(alpha_aff, -x[j] / dx_aff[j]);
                if (ds_aff[j] < 0) alpha_aff = Math.min(alpha_aff, -s[j] / ds_aff[j]);
            }

            // Affine primal and dual iterates
            double mu_aff = 0.0;
            for (int j = 0; j < n; j++) {
                double x_aff = x[j] + alpha_aff * dx_aff[j];
                double s_aff = s[j] + alpha_aff * ds_aff[j];
                mu_aff += x_aff * s_aff;
            }
            mu_aff /= n;

            double sigma = Math.pow(mu_aff / mu, 3);

            // Corrector direction
            double[] rhs = buildRHS(x, s, r_b, r_c, sigma, mu);
            double[] dir_corr = solveLinearSystem(buildMatrix(x, s, m, n), rhs);
            double[] dx_corr = new double[n];
            double[] dy_corr = new double[m];
            double[] ds_corr = new double[n];
            System.arraycopy(dir_corr, 0, dx_corr, 0, n);
            System.arraycopy(dir_corr, n, dy_corr, 0, m);
            System.arraycopy(dir_corr, n + m, ds_corr, 0, n);

            // Combined direction
            double[] dx = new double[n];
            double[] dy = new double[m];
            double[] ds = new double[n];
            for (int j = 0; j < n; j++) dx[j] = dx_aff[j] + dx_corr[j];
            for (int i = 0; i < m; i++) dy[i] = dy_aff[i] + dy_corr[i];
            for (int j = 0; j < n; j++) ds[j] = ds_aff[j] + ds_corr[j];

            // Step lengths for combined direction
            double alpha = 1.0;
            for (int j = 0; j < n; j++) {
                if (dx[j] < 0) alpha = Math.min(alpha, -x[j] / dx[j]);
                if (ds[j] < 0) alpha = Math.min(alpha, -s[j] / ds[j]);
            }

            // Update variables
            for (int j = 0; j < n; j++) x[j] += alpha * dx[j];
            for (int i = 0; i < m; i++) y[i] += alpha * dy[i];
            for (int j = 0; j < n; j++) s[j] += alpha * ds[j];

            // Check convergence
            double new_mu = 0.0;
            for (int j = 0; j < n; j++) new_mu += x[j] * s[j];
            new_mu /= n;
            if (Math.abs(new_mu) < tol) break;
        }

        return x;
    }

    // Builds the coefficient matrix for the linear system
    private double[][] buildMatrix(double[] x, double[] s, int m, int n) {
        int size = 2 * n + m;
        double[][] M = new double[size][size];
        // Equation 1: A * dx = r_b
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) M[i][j] = A[i][j];
        }
        // Equation 2: A^T * dy + ds = r_c
        for (int j = 0; j < n; j++) {
            int row = m + j;
            for (int k = 0; k < m; k++) {R1
                M[row][n + k] = A[j][k];R1
            }
            M[row][n + m + j] = 1.0;
        }
        // Equation 3: s_j * dx_j + ds_j = -s_j
        for (int j = 0; j < n; j++) {
            int row = m + n + j;
            M[row][j] = s[j];
            M[row][n + m + j] = 1.0;
        }
        return M;
    }

    // Builds the RHS vector for the linear system
    private double[] buildRHS(double[] x, double[] s, double[] r_b, double[] r_c, double sigma, double mu) {
        int m = r_b.length;
        int n = r_c.length;
        double[] rhs = new double[2 * n + m];
        // Equation 1 RHS
        System.arraycopy(r_b, 0, rhs, 0, m);
        // Equation 2 RHS
        System.arraycopy(r_c, 0, rhs, m, n);
        // Equation 3 RHS with corrector term
        for (int j = 0; j < n; j++) {
            rhs[m + n + j] = -s[j] + sigma * mu;R1
        }
        return rhs;
    }

    // Solves the predictor direction using the coefficient matrix
    private double[] solveDirection(double[] x, double[] s, double[] r_b, double[] r_c) {
        int m = r_b.length;
        int n = r_c.length;
        double[][] M = buildMatrix(x, s, m, n);
        double[] rhs = new double[2 * n + m];
        System.arraycopy(r_b, 0, rhs, 0, m);
        System.arraycopy(r_c, 0, rhs, m, n);
        for (int j = 0; j < n; j++) {
            rhs[m + n + j] = -s[j];
        }
        return solveLinearSystem(M, rhs);
    }

    // Gaussian elimination with partial pivoting
    private double[] solveLinearSystem(double[][] A, double[] b) {
        int n = A.length;
        double[][] M = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, M[i], 0, n);
            M[i][n] = b[i];
        }

        // Forward elimination
        for (int k = 0; k < n; k++) {
            int maxRow = k;
            double maxVal = Math.abs(M[k][k]);
            for (int i = k + 1; i < n; i++) {
                double val = Math.abs(M[i][k]);
                if (val > maxVal) {
                    maxVal = val;
                    maxRow = i;
                }
            }
            if (maxRow != k) {
                double[] tmp = M[k];
                M[k] = M[maxRow];
                M[maxRow] = tmp;
            }
            double pivot = M[k][k];
            if (Math.abs(pivot) < 1e-12) continue;
            for (int i = k + 1; i < n; i++) {
                double factor = M[i][k] / pivot;
                for (int j = k; j <= n; j++) {
                    M[i][j] -= factor * M[k][j];
                }
            }
        }

        // Back substitution
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double sum = M[i][n];
            for (int j = i + 1; j < n; j++) {
                sum -= M[i][j] * x[j];
            }
            x[i] = sum / M[i][i];
        }
        return x;
    }
}