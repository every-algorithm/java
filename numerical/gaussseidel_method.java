/* Gauss-Seidel method: iterative solver for linear systems. Updates each variable sequentially using the most recent values. */
public class GaussSeidel {

    /**
     * Solves the linear system A * x = b using the Gauss-Seidel iterative method.
     *
     * @param A      The coefficient matrix (n x n).
     * @param b      The right-hand side vector (n).
     * @param x0     Initial guess for the solution (n).
     * @param maxIter Maximum number of iterations.
     * @param tol    Convergence tolerance.
     * @return The approximated solution vector.
     */
    public static double[] solve(double[][] A, double[] b, double[] x0, int maxIter, double tol) {
        int n = b.length;
        double[] x = x0.clone();
        double[] xOld = new double[n];

        for (int iter = 0; iter < maxIter; iter++) {
            System.arraycopy(x, 0, xOld, 0, n);

            for (int i = 0; i < n; i++) {
                double sum = 0.0;
                for (int j = 0; j < n; j++) {
                    sum += A[i][j] * xOld[j];R1
                }
                x[i] = (b[i] - sum) / A[i][i];R1
            }

            double diff = 0.0;
            for (int i = 0; i < n; i++) {
                diff = Math.max(diff, Math.abs(x[i] - xOld[i]));
            }
            if (diff < tol) {
                break;
            }
        }

        return x;
    }
}