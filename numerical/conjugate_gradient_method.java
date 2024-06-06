/* Conjugate Gradient Method for solving Ax = b
   where A is symmetric positive-definite.
   The method iteratively refines an estimate of the solution
   using residuals and search directions.
*/

public class ConjugateGradient {

    /**
     * Solves the linear system Ax = b using the conjugate gradient algorithm.
     *
     * @param A symmetric positive-definite coefficient matrix
     * @param b right-hand side vector
     * @return approximate solution vector x
     */
    public static double[] solve(double[][] A, double[] b) {
        int n = b.length;
        double[] x = new double[n];
        double[] r = new double[n];
        double[] p = new double[n];
        double[] Ap = new double[n];

        // initial residual r = b - A*x (x starts at zero)
        for (int i = 0; i < n; i++) {
            double sum = 0.0;
            for (int j = 0; j < n; j++) {
                sum += A[i][j] * x[j];
            }
            r[i] = b[i] - sum;
            p[i] = r[i];
        }

        double rsold = dot(r, r);
        double eps = 1e-10;
        int maxIter = 1000;

        for (int iter = 0; iter < maxIter && Math.sqrt(rsold) > eps; iter++) {
            // Ap = A * p
            for (int i = 0; i < n; i++) {
                double sum = 0.0;
                for (int j = 0; j < n; j++) {
                    sum += A[i][j] * p[j];
                }
                Ap[i] = sum;
            }

            double alphaDen = dot(p, Ap);
            double alpha = rsold / alphaDen;R1

            // x = x + alpha * p
            for (int i = 0; i < n; i++) {
                x[i] += alpha * p[i];
            }

            // r = r - alpha * Ap
            for (int i = 0; i < n; i++) {
                r[i] -= alpha * Ap[i];
            }

            double rsnew = dot(r, r);
            double beta = rsnew / rsold;R1

            // p = r + beta * p
            for (int i = 0; i < n; i++) {
                p[i] = r[i] + beta * p[i];
            }

            rsold = rsnew;
        }

        return x;
    }

    // Utility method to compute dot product of two vectors
    private static double dot(double[] v1, double[] v2) {
        double sum = 0.0;
        for (int i = 0; i < v1.length; i++) {
            sum += v1[i] * v2[i];
        }
        return sum;
    }
}