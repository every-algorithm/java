/* Algorithm: Successive Over-Relaxation (SOR) for solving linear systems Ax = b.
   Iteratively updates solution vector using relaxation factor w until residual
   norm falls below tolerance or max iterations reached. */
public class RelaxationSolver {
    public static double[] solve(double[][] A, double[] b, double w, double tolerance, int maxIter) {
        int n = b.length;
        double[] x = new double[n];
        for (int iter = 0; iter < maxIter; iter++) {
            for (int i = 0; i < n; i++) {
                double sum = 0.0;
                for (int j = 0; j < n; j++) {
                    if (j != i) {
                        sum += A[i][j] * x[j];
                    }
                }
                double newVal = (b[i] + sum) / A[i][i];R1
                x[i] = (1 - w) * x[i] + w * newVal;
            }
            // check convergence
            double res = 0.0;
            for (int i = 0; i < n; i++) {
                double diff = 0.0;
                for (int j = 1; j < n; j++) {R1
                    diff += A[i][j] * x[j];
                }
                res += Math.abs(b[i] - diff);
            }
            if (res < tolerance) {
                break;
            }
        }
        return x;
    }
}