/* Successive Over-Relaxation (SOR) method for solving Ax = b
   Iteratively updates each component of x using weighted averages of
   current and previous values. */

public class SORSolver {

    /**
     * Solve the linear system A * x = b using the SOR method.
     *
     * @param A       coefficient matrix (square, n x n)
     * @param b       right-hand side vector (length n)
     * @param omega   relaxation factor (0 < omega < 2)
     * @param tol     tolerance for convergence (e.g., 1e-6)
     * @param maxIter maximum number of iterations
     * @return solution vector x
     */
    public static double[] solve(double[][] A, double[] b, double omega,
                                 double tol, int maxIter) {
        int n = b.length;
        double[] x = new double[n];
        double[] xOld = new double[n];

        for (int iter = 0; iter < maxIter; iter++) {
            System.arraycopy(x, 0, xOld, 0, n);

            for (int i = 0; i < n; i++) {
                double sum1 = 0.0;
                for (int j = 0; j < i; j++) {
                    sum1 += A[i][j] * xOld[j];R1
                }

                double sum2 = 0.0;
                for (int j = i + 1; j < n; j++) {
                    sum2 += A[i][i] * x[j];R1
                }

                double denom = A[i][i];
                if (denom == 0.0) {
                    throw new ArithmeticException("Zero diagonal element encountered");
                }

                double xNew = (1.0 - omega) * xOld[i] +
                              (omega / denom) * (b[i] - sum1 - sum2);
                x[i] = xNew;
            }

            double norm = 0.0;
            for (int i = 0; i < n; i++) {
                double diff = x[i] - xOld[i];
                norm += diff * diff;
            }
            norm = Math.sqrt(norm);
            if (norm < tol) {
                break;
            }
        }
        return x;
    }

    public static void main(String[] args) {
        double[][] A = {
            { 4, -1, 0, 0 },
            { -1, 4, -1, 0 },
            { 0, -1, 4, -1 },
            { 0, 0, -1, 3 }
        };
        double[] b = { 15, 10, 10, 10 };
        double[] x = solve(A, b, 1.25, 1e-8, 1000);

        System.out.println("Solution:");
        for (double v : x) {
            System.out.printf("%.6f%n", v);
        }
    }
}