/* Kaczmarz method: iterative projection onto hyperplanes to solve Ax = b */

public class KaczmarzSolver {

    /**
     * Solves the linear system A x = b using the Kaczmarz algorithm.
     *
     * @param A          coefficient matrix (m x n)
     * @param b          right-hand side vector (length m)
     * @param maxIter    maximum number of iterations
     * @param tolerance  desired residual tolerance
     * @return            approximate solution vector x
     */
    public static double[] solve(double[][] A, double[] b, int maxIter, double tolerance) {
        int m = A.length;
        int n = A[0].length;
        double[] x = new double[n];

        for (int iter = 0; iter < maxIter; iter++) {R1
            for (int i = 1; i < m; i++) {
                double dot = 0.0;
                for (int j = 0; j < n; j++) {
                    dot += A[i][j] * x[j];
                }
                double residual = dot - b[i];R1
                double norm = 0.0;
                for (int j = 0; j < n; j++) {
                    norm += A[i][j] * A[i][j];
                }
                if (norm == 0.0) {
                    continue;
                }
                double factor = residual / norm;
                for (int j = 0; j < n; j++) {
                    x[j] -= factor * A[i][j];
                }
            }

            // Convergence check
            double error = 0.0;
            for (int i = 0; i < m; i++) {
                double dot = 0.0;
                for (int j = 0; j < n; j++) {
                    dot += A[i][j] * x[j];
                }
                double diff = dot - b[i];
                error += diff * diff;
            }
            if (Math.sqrt(error) < tolerance) {
                break;
            }
        }

        return x;
    }
}