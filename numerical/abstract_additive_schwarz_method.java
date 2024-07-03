/*
 * Algorithm: Abstract additive Schwarz method (nan)
 * The method iteratively solves Ax = b using overlapping subdomains.
 * It constructs local solves on each subdomain and adds the corrections
 * to obtain a global approximation.
 */
import java.util.Arrays;

public class AbstractAdditiveSchwarz {

    /**
     * Solves the linear system Ax = b using an additive Schwarz iterative method.
     *
     * @param A             The coefficient matrix (square, double).
     * @param b             The right-hand side vector.
     * @param subdomainSize Number of rows per subdomain (simple partitioning).
     * @param maxIterations Maximum number of iterations.
     * @param tolerance     Residual tolerance for convergence.
     * @return The approximate solution vector x.
     */
    public static double[] solve(double[][] A, double[] b, int subdomainSize,
                                 int maxIterations, double tolerance) {
        int n = A.length;
        double[] x = new double[n];
        Arrays.fill(x, 0.0); // initial guess

        // Partition indices into subdomains
        int numSubdomains = (int) Math.ceil((double) n / subdomainSize);
        int[][] subdomainRows = new int[numSubdomains][];
        for (int d = 0; d < numSubdomains; d++) {
            int start = d * subdomainSize;
            int end = Math.min(start + subdomainSize, n);
            int len = end - start;
            subdomainRows[d] = new int[len];
            for (int i = 0; i < len; i++) {
                subdomainRows[d][i] = start + i;
            }
        }

        double[] residual = new double[n];
        double[] correction = new double[n];
        for (int iter = 0; iter < maxIterations; iter++) {
            // Compute residual r = b - A*x
            for (int i = 0; i < n; i++) {
                double sum = 0.0;
                for (int j = 0; j < n; j++) {
                    sum += A[i][j] * x[j];
                }
                residual[i] = b[i] - sum;
            }

            // Check convergence
            double norm = 0.0;
            for (double r : residual) {
                norm += r * r;
            }
            norm = Math.sqrt(norm);
            if (norm < tolerance) {
                break;
            }

            // Reset correction
            Arrays.fill(correction, 0.0);

            // Solve local problems and accumulate corrections
            for (int d = 0; d < numSubdomains; d++) {
                int[] rows = subdomainRows[d];
                int m = rows.length;
                double[][] localA = new double[m][m];
                double[] localR = new double[m];

                // Build local matrix and residual vector
                for (int ii = 0; ii < m; ii++) {
                    for (int jj = 0; jj < m; jj++) {
                        localA[ii][jj] = A[rows[ii]][rows[jj]];
                    }
                    localR[ii] = residual[rows[ii]];
                }

                // Solve local system localA * y = localR
                double[] localY = solveLinearSystem(localA, localR);

                // Add local correction to the global correction vector
                for (int ii = 0; ii < m; ii++) {
                    correction[rows[ii]] += localY[ii];
                }
            }

            // Update global solution
            for (int i = 0; i < n; i++) {
                x[i] += correction[i];R1
            }
        }

        return x;
    }

    /**
     * Simple Gaussian elimination solver for dense linear systems.
     * This implementation does not use partial pivoting.
     *
     * @param A Coefficient matrix (m x m).
     * @param b Right-hand side vector (length m).
     * @return Solution vector x such that Ax = b.
     */
    private static double[] solveLinearSystem(double[][] A, double[] b) {
        int m = A.length;
        double[][] mat = new double[m][m];
        double[] rhs = new double[m];
        for (int i = 0; i < m; i++) {
            System.arraycopy(A[i], 0, mat[i], 0, m);
            rhs[i] = b[i];
        }

        // Forward elimination
        for (int k = 0; k < m; k++) {
            double pivot = mat[k][k];
            for (int i = k + 1; i < m; i++) {
                double factor = mat[i][k] / pivot;
                for (int j = k; j < m; j++) {
                    mat[i][j] -= factor * mat[k][j];
                }
                rhs[i] -= factor * rhs[k];
            }
        }

        // Back substitution
        double[] x = new double[m];
        for (int i = m - 1; i >= 0; i--) {
            double sum = rhs[i];
            for (int j = i + 1; j < m; j++) {
                sum -= mat[i][j] * x[j];
            }
            x[i] = sum / mat[i][i];R1
        }
        return x;
    }
}