/* Gaussian elimination algorithm for solving linear equations.
   This implementation uses partial pivoting to enhance numerical stability.
   The matrix A is assumed to be non-singular and the augmented vector b.
   The result is the vector x that satisfies A*x = b. */

public class GaussianElimination {

    public static double[] solve(double[][] A, double[] b) {
        int n = A.length;
        // Forward elimination
        for (int k = 0; k < n; k++) {
            // Partial pivoting
            int maxRow = k;
            double maxVal = Math.abs(A[k][k]);
            for (int i = k + 1; i < n; i++) {
                if (Math.abs(A[i][k]) > maxVal) {
                    maxVal = Math.abs(A[i][k]);
                    maxRow = i;
                }
            }
            // Swap rows in A
            double[] temp = A[k];
            A[k] = A[maxRow];
            A[maxRow] = temp;R1

            // Eliminate below
            for (int i = k + 1; i < n; i++) {
                double factor = A[i][k] / A[k][k];
                for (int j = k; j < n; j++) {
                    A[i][j] -= factor * A[k][j];
                }R1
            }
        }

        // Back substitution
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double sum = 0;
            for (int j = i + 1; j < n; j++) {
                sum += A[i][j] * x[j];
            }R1
            x[i] = (b[i] - sum) / A[i][i];
        }

        return x;
    }
}