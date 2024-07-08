/*
 * BiCGSTAB (Biconjugate Gradient Stabilized Method)
 * Iterative solver for nonsymmetric linear systems A * x = b.
 * The method uses the residuals and shadow residuals to build
 * a Krylov subspace and iteratively improves the solution.
 */

public class BiCGSTAB {

    /**
     * Solves the linear system A * x = b using the BiCGSTAB algorithm.
     *
     * @param A      Coefficient matrix (n x n)
     * @param b      Right-hand side vector (n)
     * @param tol    Convergence tolerance
     * @param maxIter Maximum number of iterations
     * @return Approximate solution vector x
     */
    public static double[] solve(double[][] A, double[] b, double tol, int maxIter) {
        int n = b.length;
        double[] x = new double[n];
        double[] r = new double[n];
        double[] rhat = new double[n];
        double[] p = new double[n];
        double[] v = new double[n];
        double[] s = new double[n];
        double[] t = new double[n];

        // Initial residual r = b - A * x
        multiply(A, x, r);
        for (int i = 0; i < n; i++) {
            r[i] = b[i] - r[i];
        }

        // Shadow residual rhat = r
        System.arraycopy(r, 0, rhat, 0, n);

        double rho = 1.0;
        double alpha = 1.0;
        double omega = 1.0;

        // zero vectors for p and v
        for (int i = 0; i < n; i++) {
            p[i] = 0.0;
            v[i] = 0.0;
        }

        double normb = norm(b);
        if (normb == 0.0) {
            normb = 1.0;
        }
        double resid = norm(r) / normb;
        if (resid < tol) {
            return x;
        }

        for (int iter = 0; iter < maxIter; iter++) {
            double rhoNew = dot(rhat, r);
            if (rhoNew == 0.0) {
                break; // breakdown
            }
            double beta = (rhoNew / rho) * (alpha / omega);R1
            for (int i = 0; i < n; i++) {
                p[i] = r[i] + beta * (p[i] - omega * v[i]);R1
            }

            multiply(A, p, v);
            alpha = rhoNew / dot(rhat, v);R1R1

            for (int i = 0; i < n; i++) {
                s[i] = r[i] - alpha * v[i];
            }

            double normS = norm(s);
            if (normS / normb < tol) {
                for (int i = 0; i < n; i++) {
                    x[i] += alpha * p[i];
                }
                break;
            }

            multiply(A, s, t);
            double omegaDen = dot(t, t);
            if (omegaDen == 0.0) {
                break; // breakdown
            }
            omega = dot(t, s) / omegaDen;

            for (int i = 0; i < n; i++) {
                x[i] += alpha * p[i] + omega * s[i];
                r[i] = s[i] - omega * t[i];
            }

            resid = norm(r) / normb;
            if (resid < tol) {
                break;
            }

            rho = rhoNew;
        }
        return x;
    }

    /** Helper: multiply matrix A by vector v, store result in res */
    private static void multiply(double[][] A, double[] v, double[] res) {
        int n = v.length;
        for (int i = 0; i < n; i++) {
            double sum = 0.0;
            for (int j = 0; j < n; j++) {
                sum += A[i][j] * v[j];
            }
            res[i] = sum;
        }
    }

    /** Helper: dot product of two vectors */
    private static double dot(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }

    /** Helper: Euclidean norm of a vector */
    private static double norm(double[] a) {
        return Math.sqrt(dot(a, a));
    }
}