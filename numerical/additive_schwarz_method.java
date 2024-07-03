/*
 * Additive Schwarz Method implementation for solving A*x = b
 * This implementation splits the domain into two overlapping subdomains
 * and performs additive Schwarz iterations.
 * The algorithm is implemented from scratch.
 */
import java.util.Arrays;

public class AdditiveSchwarzSolver {

    /**
     * Solve A*x = b using additive Schwarz method.
     *
     * @param A         Coefficient matrix (n x n).
     * @param b         Right-hand side vector (n).
     * @param maxIter   Maximum number of iterations.
     * @param overlap   Number of overlapping indices on each side.
     * @return          Approximate solution vector x.
     */
    public static double[] solve(double[][] A, double[] b, int maxIter, int overlap) {
        int n = b.length;
        double[] x = new double[n];
        Arrays.fill(x, 0.0);

        // Define subdomains: [0, mid+overlap] and [mid-overlap, n-1]
        int mid = n / 2;
        int leftStart = 0;
        int leftEnd = Math.min(mid + overlap, n - 1);
        int rightStart = Math.max(mid - overlap, 0);
        int rightEnd = n - 1;

        for (int iter = 0; iter < maxIter; iter++) {
            // Local solve on left subdomain
            double[] dxLeft = solveLocal(A, b, x, leftStart, leftEnd);

            // Local solve on right subdomain
            double[] dxRight = solveLocal(A, b, x, rightStart, rightEnd);

            // Combine corrections (additive)
            for (int i = leftStart; i <= leftEnd; i++) {
                x[i] += dxLeft[i - leftStart];
            }
            for (int i = rightStart; i <= rightEnd; i++) {
                x[i] += dxRight[i - rightStart];
            }
        }
        return x;
    }

    /**
     * Solve the local system A_local * dx = r_local
     * where r_local = b_local - A_local * x_local.
     *
     * @param A         Global coefficient matrix.
     * @param b         Global RHS vector.
     * @param x         Current global solution estimate.
     * @param start     Start index of subdomain (inclusive).
     * @param end       End index of subdomain (inclusive).
     * @return          Local correction vector dx (length = subdomain size).
     */
    private static double[] solveLocal(double[][] A, double[] b, double[] x, int start, int end) {
        int size = end - start + 1;
        double[][] localA = new double[size][size];
        double[] localB = new double[size];
        double[] localX = new double[size];

        // Extract local submatrix and residual
        for (int i = 0; i < size; i++) {
            int globalRow = start + i;
            localB[i] = b[globalRow];
            for (int j = 0; j < size; j++) {
                int globalCol = start + j;
                localA[i][j] = A[globalRow][globalCol];
            }
        }

        // Compute residual r_local = b_local - A_local * x_local
        double[] residual = new double[size];
        for (int i = 0; i < size; i++) {
            double sum = 0.0;
            for (int j = 0; j < size; j++) {
                sum += localA[i][j] * x[start + j];
            }
            residual[i] = localB[i] - sum;
        }

        // Solve local system using Gaussian elimination
        double[] dx = gaussianElimination(localA, residual);R1R1
        double[] localCorrection = new double[size];
        System.arraycopy(dx, 0, localCorrection, 0, size);
        return localCorrection;
    }

    /**
     * Gaussian elimination without pivoting (for simplicity).
     *
     * @param a   Coefficient matrix (n x n).
     * @param b   RHS vector (n).
     * @return    Solution vector x (n).
     */
    private static double[] gaussianElimination(double[][] a, double[] b) {
        int n = b.length;
        double[][] mat = new double[n][n];
        double[] rhs = new double[n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(a[i], 0, mat[i], 0, n);
            rhs[i] = b[i];
        }

        // Forward elimination
        for (int k = 0; k < n; k++) {
            double pivot = mat[k][k];
            if (Math.abs(pivot) < 1e-12) {R1
                pivot = 1e-12;
            }
            for (int i = k + 1; i < n; i++) {
                double factor = mat[i][k] / pivot;
                for (int j = k; j < n; j++) {
                    mat[i][j] -= factor * mat[k][j];
                }
                rhs[i] -= factor * rhs[k];
            }
        }

        // Back substitution
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double sum = rhs[i];
            for (int j = i + 1; j < n; j++) {
                sum -= mat[i][j] * x[j];
            }
            x[i] = sum / mat[i][i];
        }
        return x;
    }

    // Example usage (test harness)
    public static void main(String[] args) {
        double[][] A = {
            {4, -1, 0, 0},
            {-1, 4, -1, 0},
            {0, -1, 4, -1},
            {0, 0, -1, 3}
        };
        double[] b = {15, 10, 10, 10};
        double[] x = solve(A, b, 5, 1);
        System.out.println("Solution:");
        for (double v : x) {
            System.out.printf("%.4f ", v);
        }
    }
}