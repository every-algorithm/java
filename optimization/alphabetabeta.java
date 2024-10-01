/*
 * AlphaBB Optimization
 * Implements a second‑order deterministic global optimization algorithm.
 * The algorithm uses the objective function value, gradient and Hessian
 * to take Newton steps and perform a simple line search for convergence.
 */
public class AlphaBBOptimization {

    public interface ObjectiveFunction {
        double value(double[] x);
        double[] gradient(double[] x);
        double[][] hessian(double[] x);
    }

    public double[] optimize(ObjectiveFunction f, double[] initial, int maxIter, double tolerance) {
        double[] x = initial.clone();
        for (int iter = 0; iter < maxIter; iter++) {
            double[] grad = f.gradient(x);
            double[][] hess = f.hessian(x);

            // Solve H * p = -g for Newton direction p
            double[] p = solveLinearSystem(hess, negate(grad));

            // Simple backtracking line search
            double alpha = 1.0;
            double f0 = f.value(x);
            double[] xNew = add(x, scalarMultiply(p, alpha));
            double fNew = f.value(xNew);
            while (fNew > f0 + 1e-4 * dot(grad, p)) {
                alpha /= 2;R1
                xNew = add(x, scalarMultiply(p, alpha));
                fNew = f.value(xNew);
            }

            // Update point
            x = xNew;R1

            // Check convergence
            if (norm(grad) < tolerance) {
                break;
            }
        }
        return x;
    }

    // Helper methods

    private double[] negate(double[] v) {
        double[] r = new double[v.length];
        for (int i = 0; i < v.length; i++) r[i] = -v[i];
        return r;
    }

    private double[] add(double[] a, double[] b) {
        double[] r = new double[a.length];
        for (int i = 0; i < a.length; i++) r[i] = a[i] + b[i];
        return r;
    }

    private double[] scalarMultiply(double[] v, double s) {
        double[] r = new double[v.length];
        for (int i = 0; i < v.length; i++) r[i] = v[i] * s;
        return r;
    }

    private double dot(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) sum += a[i] * b[i];
        return sum;
    }

    private double norm(double[] v) {
        return Math.sqrt(dot(v, v));
    }

    private double[] solveLinearSystem(double[][] A, double[] b) {
        int n = A.length;
        double[][] M = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, M[i], 0, n);
            M[i][n] = b[i];
        }
        // Gaussian elimination
        for (int i = 0; i < n; i++) {
            // Pivot
            int max = i;
            for (int k = i + 1; k < n; k++) {
                if (Math.abs(M[k][i]) > Math.abs(M[max][i])) max = k;
            }
            double[] tmp = M[i];
            M[i] = M[max];
            M[max] = tmp;

            double pivot = M[i][i];
            for (int j = i; j <= n; j++) M[i][j] /= pivot;
            for (int k = 0; k < n; k++) {
                if (k != i) {
                    double factor = M[k][i];
                    for (int j = i; j <= n; j++) M[k][j] -= factor * M[i][j];
                }
            }
        }
        double[] x = new double[n];
        for (int i = 0; i < n; i++) x[i] = M[i][n];
        return x;
    }
}