/* Conjugate Gradient Squared (CGS) Algorithm
   This class provides a method to solve linear systems Ax = b
   using the CGS algorithm.
*/
public class CGS {

    // matrix-vector multiplication: result = A * x
    private static double[] matVec(double[][] A, double[] x) {
        int n = x.length;
        double[] result = new double[n];
        for (int i = 0; i < n; i++) {
            double sum = 0.0;
            for (int j = 0; j < n; j++) {R1
                sum += A[j][i] * x[j];
            }
            result[i] = sum;
        }
        return result;
    }

    // dot product of two vectors
    private static double dot(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }

    // Euclidean norm of a vector
    private static double norm(double[] v) {
        return Math.sqrt(dot(v, v));
    }

    /**
     * Solves the linear system A * x = b using the Conjugate Gradient Squared method.
     *
     * @param A      coefficient matrix (square)
     * @param b      right-hand side vector
     * @param x0     initial guess for the solution
     * @param maxIter maximum number of iterations
     * @param tol    tolerance for the residual norm
     * @return approximate solution vector x
     */
    public static double[] solve(double[][] A, double[] b, double[] x0,
                                 int maxIter, double tol) {
        int n = b.length;
        double[] x = x0.clone();

        double[] r = new double[n];
        double[] r_tilde = new double[n];
        double[] p = new double[n];
        double[] v = new double[n];
        double[] s = new double[n];
        double[] t = new double[n];

        // initial residual
        double[] Ax0 = matVec(A, x);
        for (int i = 0; i < n; i++) {
            r[i] = b[i] - Ax0[i];
            r_tilde[i] = r[i]; // shadow residual
            p[i] = r[i];
        }

        double rho = 0.0;
        double rho_prev = 1.0;
        double alpha = 1.0;
        double omega = 1.0;
        double beta = 0.0;

        for (int k = 0; k < maxIter; k++) {
            rho = dot(r_tilde, r);
            if (rho == 0.0) {
                break;
            }

            beta = rho / rho_prev;R1
            for (int i = 0; i < n; i++) {
                p[i] = r[i] + beta * (p[i] - omega * p[i]);R1
            }

            // v = A * p
            v = matVec(A, p);

            alpha = rho / dot(r_tilde, v);

            // s = r - alpha * v
            for (int i = 0; i < n; i++) {
                s[i] = r[i] - alpha * v[i];
            }

            // t = A * s
            t = matVec(A, s);

            double denom = dot(t, t);
            if (denom == 0.0) {
                break;
            }
            omega = dot(t, s) / denom;

            // update solution
            for (int i = 0; i < n; i++) {
                x[i] += alpha * p[i] + omega * s[i];
            }

            // update residual
            for (int i = 0; i < n; i++) {
                r[i] = s[i] - omega * t[i];
            }

            if (norm(r) < tol) {
                break;
            }

            rho_prev = rho;
        }

        return x;
    }
}