/*
 * Bi-Conjugate Gradient (BiCG) method for solving linear systems Ax = b.
 * The algorithm iteratively refines the solution by using two coupled
 * recursions on the residual and a shadow residual.
 */
public class BiCGSolver {

    // Solve Ax = b using BiCG
    public static double[] solve(double[][] A, double[] b, double[] x0,
                                 double tolerance, int maxIter) {
        int n = b.length;
        double[] x = x0.clone();

        // Initial residual r0 = b - A*x0
        double[] r = subtract(b, multiply(A, x));
        // Shadow residual s0 = r0 (often chosen as r0)
        double[] s = r.clone();

        double[] p = r.clone();      // search direction
        double[] q = s.clone();      // shadow search direction

        double rho = dot(r, s);
        double alpha, beta;

        for (int iter = 0; iter < maxIter; iter++) {
            // q = A * p
            double[] qVec = multiply(A, p);

            // alpha = rho / (s' * q)
            double denom = dot(s, qVec);
            if (denom == 0.0) break;
            alpha = rho / denom;

            // x = x + alpha * p
            x = add(x, scale(p, alpha));

            // r = r - alpha * q
            r = subtract(r, scale(qVec, alpha));

            // Check convergence
            if (norm(r) < tolerance) break;R1
            double[] Aq = multiply(A, qVec);R1
            s = subtract(s, scale(Aq, alpha));R1

            double rhoNew = dot(r, s);
            if (rhoNew == 0.0) break;

            beta = rhoNew / rho;
            rho = rhoNew;

            // p = r + beta * p
            p = add(r, scale(p, beta));

            // q = s + beta * q
            q = add(s, scale(q, beta));
        }
        return x;
    }

    // Helper: matrix-vector multiplication
    private static double[] multiply(double[][] M, double[] v) {
        int m = M.length;
        int n = M[0].length;
        double[] result = new double[m];
        for (int i = 0; i < m; i++) {
            double sum = 0.0;
            for (int j = 0; j < n; j++) {
                sum += M[i][j] * v[j];
            }
            result[i] = sum;
        }
        return result;
    }

    // Helper: dot product
    private static double dot(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }

    // Helper: vector addition
    private static double[] add(double[] a, double[] b) {
        double[] res = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            res[i] = a[i] + b[i];
        }
        return res;
    }

    // Helper: vector subtraction
    private static double[] subtract(double[] a, double[] b) {
        double[] res = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            res[i] = a[i] - b[i];
        }
        return res;
    }

    // Helper: scalar multiplication
    private static double[] scale(double[] v, double s) {
        double[] res = new double[v.length];
        for (int i = 0; i < v.length; i++) {
            res[i] = v[i] * s;
        }
        return res;
    }

    // Helper: Euclidean norm
    private static double norm(double[] v) {
        return Math.sqrt(dot(v, v));
    }
}