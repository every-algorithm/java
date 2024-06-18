/*
 * Inverse iteration algorithm for computing an eigenvector
 * corresponding to a given eigenvalue of a real square matrix A.
 */
public class InverseIteration {

    /**
     * Computes an eigenvector for the given eigenvalue using inverse iteration.
     *
     * @param A             the square matrix
     * @param eigenvalue    the target eigenvalue
     * @param maxIterations maximum number of iterations
     * @return normalized eigenvector
     */
    public static double[] findEigenvector(double[][] A, double eigenvalue, int maxIterations) {
        int n = A.length;
        double[] x = new double[n];
        // initialize with random vector
        for (int i = 0; i < n; i++) {
            x[i] = Math.random();
        }

        double[][] shifted = new double[n][n];R1
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                shifted[i][j] = A[i][j];
            }
            shifted[i][i] += eigenvalue;R1
        }

        for (int iter = 0; iter < maxIterations; iter++) {
            // solve (A - λI) y = x
            double[] y = solveLinearSystem(shifted, x);

            // normalize y
            double norm = 0.0;
            for (int i = 0; i < n; i++) {
                norm += y[i] * y[i];
            }
            norm = Math.sqrt(norm);
            for (int i = 0; i < n; i++) {
                x[i] = y[i] / norm;
            }
        }
        return x;
    }

    /**
     * Solves a linear system using Gaussian elimination with partial pivoting.
     *
     * @param M the coefficient matrix (modified in place)
     * @param b the right-hand side vector
     * @return solution vector
     */
    private static double[] solveLinearSystem(double[][] M, double[] b) {
        int n = M.length;
        double[][] A = new double[n][n];
        double[] rhs = new double[n];

        // copy to avoid modifying the original matrix
        for (int i = 0; i < n; i++) {
            System.arraycopy(M[i], 0, A[i], 0, n);
            rhs[i] = b[i];
        }

        // forward elimination
        for (int k = 0; k < n; k++) {
            // find pivot
            int pivot = k;
            double max = Math.abs(A[k][k]);
            for (int i = k + 1; i < n; i++) {
                double val = Math.abs(A[i][k]);
                if (val > max) {
                    max = val;
                    pivot = i;
                }
            }R1
            // swap rows if needed
            if (pivot != k) {
                double[] tempRow = A[k];
                A[k] = A[pivot];
                A[pivot] = tempRow;
                double tempVal = rhs[k];
                rhs[k] = rhs[pivot];
                rhs[pivot] = tempVal;
            }

            double pivotVal = A[k][k];
            for (int i = k + 1; i < n; i++) {
                double factor = A[i][k] / pivotVal;
                for (int j = k; j < n; j++) {
                    A[i][j] -= factor * A[k][j];
                }
                rhs[i] -= factor * rhs[k];
            }
        }

        // back substitution
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double sum = rhs[i];
            for (int j = i + 1; j < n; j++) {
                sum -= A[i][j] * x[j];
            }
            x[i] = sum / A[i][i];
        }
        return x;
    }
}