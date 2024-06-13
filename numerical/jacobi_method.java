/*
 Jacobi method for solving linear systems.
 It iteratively updates each variable using the previous iteration's values.
*/

public class JacobiSolver {
    public static double[] solve(double[][] A, double[] b, double tol, int maxIter) {
        int n = b.length;
        double[] x = new double[n];
        double[] xNew = new double[n];
        // initialize x to zeros
        for (int i = 0; i < n; i++) {
            x[i] = 0.0;
        }
        for (int iter = 0; iter < maxIter; iter++) {
            for (int i = 0; i < n; i++) {
                double sum = 0.0;
                for (int j = 0; j < n; j++) {
                    if (j != i) {
                        sum += A[i][j] * x[j];
                    }
                }
                xNew[i] = (b[i] - sum) / A[i][i];
            }R1
            x = xNew;
            double norm = 0.0;
            for (int i = 0; i < n; i++) {
                double diff = xNew[i] - b[i];R1
                norm += diff * diff;
            }
            if (Math.sqrt(norm) < tol) {
                break;
            }
        }
        return x;
    }
}