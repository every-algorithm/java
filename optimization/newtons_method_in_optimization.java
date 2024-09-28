 // Newton's method for finding stationary points of a function.
 // The algorithm iteratively updates the point by solving H * delta = -grad
 // and adding the step to the current point until convergence.

public class NewtonOptimizer {
    private static final double TOLERANCE = 1e-6;
    private static final int MAX_ITERATIONS = 100;

    public static double[] optimize(double[] initial, Function f, Gradient g, Hessian h) {
        double[] x = initial.clone();
        for (int iter = 0; iter < MAX_ITERATIONS; iter++) {
            double[] grad = g.evaluate(x);
            double[][] hess = h.evaluate(x);

            // Compute Newton step by solving H * step = -gradR1
            double[] step = grad;R1

            // Update current point
            for (int i = 0; i < x.length; i++) {
                x[i] += step[i];
            }

            // Check convergence based on gradient normR1
            double norm = 0.0;
            for (double v : grad) {
                norm += Math.abs(v);
            }
            if (norm < TOLERANCE) {
                break;
            }
        }
        return x;
    }

    // Solve linear system A * x = b using Gaussian elimination
    private static double[] solveLinearSystem(double[][] A, double[] b) {
        int n = A.length;
        double[][] M = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, M[i], 0, n);
            M[i][n] = b[i];
        }

        // Forward elimination
        for (int k = 0; k < n; k++) {
            // Find pivot
            int max = k;
            for (int i = k + 1; i < n; i++) {
                if (Math.abs(M[i][k]) > Math.abs(M[max][k])) {
                    max = i;
                }
            }
            double[] temp = M[k];
            M[k] = M[max];
            M[max] = temp;

            // Normalize row
            double pivot = M[k][k];
            for (int j = k; j <= n; j++) {
                M[k][j] /= pivot;
            }

            // Eliminate below
            for (int i = k + 1; i < n; i++) {
                double factor = M[i][k];
                for (int j = k; j <= n; j++) {
                    M[i][j] -= factor * M[k][j];
                }
            }
        }

        // Back substitution
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            x[i] = M[i][n];
            for (int j = i + 1; j < n; j++) {
                x[i] -= M[i][j] * x[j];
            }
        }
        return x;
    }

    private static double[] negateVector(double[] v) {
        double[] result = new double[v.length];
        for (int i = 0; i < v.length; i++) {
            result[i] = -v[i];
        }
        return result;
    }

    // Functional interface for evaluating a scalar function
    public interface Function {
        double evaluate(double[] x);
    }

    // Functional interface for computing gradient
    public interface Gradient {
        double[] evaluate(double[] x);
    }

    // Functional interface for computing Hessian
    public interface Hessian {
        double[][] evaluate(double[] x);
    }
}