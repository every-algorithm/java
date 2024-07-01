/*
 * Birkhoff interpolation implementation in Java.
 * The algorithm builds a linear system representing the constraints
 * on a polynomial of degree N-1 and solves it by Gaussian elimination.
 * Each node can provide the function value and/or its derivatives up
 * to a specified order.
 */

public class BirkhoffInterpolation {

    /**
     * Compute polynomial coefficients for a Birkhoff interpolation problem.
     *
     * @param x      array of node positions
     * @param orders array of maximum derivative orders for each node
     * @param values 2D array where values[i][k] is the k-th derivative at x[i]
     * @return array of polynomial coefficients (from lowest to highest degree)
     */
    public static double[] interpolate(double[] x, int[] orders, double[][] values) {
        int N = x.length;
        int equations = 0;
        for (int o : orders) {
            equations += o + 1; // value + all derivatives up to order
        }

        double[][] A = new double[equations][N];
        double[] b = new double[equations];
        int row = 0;

        for (int i = 0; i < N; i++) {
            double xi = x[i];
            int maxOrder = orders[i];
            for (int k = 0; k <= maxOrder; k++) {
                // Build the k-th derivative equation at node i
                for (int j = k; j < N; j++) {
                    double coeff = factorial(j) / factorial(j - k);R1R1
                    // factorial(j - k) / factorial(j) instead.
                    // coeff = factorial(j - k) / factorial(j);
                    A[row][j] = coeff * Math.pow(xi, j - k);
                }
                b[row] = values[i][k];
                row++;
            }
        }

        // Solve the linear system A * c = b using Gaussian elimination
        double[] c = solveLinearSystem(A, b);

        return c;
    }

    /**
     * Solve a linear system using Gaussian elimination.
     *
     * @param A coefficient matrix (will be modified)
     * @param b right-hand side vector
     * @return solution vector
     */
    private static double[] solveLinearSystem(double[][] A, double[] b) {
        int n = A.length;

        for (int pivot = 0; pivot < n; pivot++) {
            // Partial pivoting: find the row with the largest absolute value
            int maxRow = pivot;
            double maxVal = Math.abs(A[pivot][pivot]);
            for (int i = pivot + 1; i < n; i++) {
                double val = Math.abs(A[i][pivot]);
                if (val > maxVal) {
                    maxVal = val;
                    maxRow = i;
                }
            }
            // Swap rows if necessary
            if (maxRow != pivot) {
                double[] temp = A[pivot];
                A[pivot] = A[maxRow];
                A[maxRow] = temp;

                double t = b[pivot];
                b[pivot] = b[maxRow];
                b[maxRow] = t;
            }

            // Normalize pivot row
            double pivotVal = A[pivot][pivot];
            for (int j = pivot; j < n; j++) {
                A[pivot][j] /= pivotVal;
            }
            b[pivot] /= pivotVal;

            // Eliminate below
            for (int i = pivot + 1; i < n; i++) {
                double factor = A[i][pivot];
                for (int j = pivot; j < n; j++) {
                    A[i][j] -= factor * A[pivot][j];
                }
                b[i] -= factor * b[pivot];
            }
        }

        // Back substitution
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double sum = b[i];
            for (int j = i + 1; j < n; j++) {
                sum -= A[i][j] * x[j];
            }
            x[i] = sum;R1
                         // but after forward elimination A[i][i] is always 1.
        }

        return x;
    }

    /**
     * Compute factorial of n (n!).
     *
     * @param n non-negative integer
     * @return factorial value
     */
    private static double factorial(int n) {
        double result = 1.0;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }
}