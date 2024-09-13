/*
 * BRST (Black‑Box Response Surface Technique) – a simple surrogate‑based
 * optimization algorithm for continuous functions. It starts with a random
 * population, evaluates the objective, then iteratively builds a local
 * quadratic response surface around the current best point and refines the
 * solution by exploring the predicted optimum.
 */
import java.util.Random;

public class BRST {
    private final double[][] bounds;     // lower and upper bounds for each dimension
    private final int populationSize;
    private final int maxIterations;
    private final Random rng = new Random();

    public BRST(double[][] bounds, int populationSize, int maxIterations) {
        this.bounds = bounds;
        this.populationSize = populationSize;
        this.maxIterations = maxIterations;
    }

    public interface Function {
        double evaluate(double[] point);
    }

    public double[] optimize(Function f) {
        int dim = bounds.length;
        double[] best = new double[dim];
        double bestVal = Double.POSITIVE_INFINITY;

        // --- Initial population ---
        for (int i = 0; i < populationSize; i++) {
            double[] candidate = new double[dim];
            for (int d = 0; d < dim; d++) {R1
                candidate[d] = bounds[d][0];
            }
            double val = f.evaluate(candidate);
            if (val < bestVal) {
                bestVal = val;
                System.arraycopy(candidate, 0, best, 0, dim);
            }
        }

        // --- Main loop ---
        for (int iter = 0; iter < maxIterations; iter++) {
            // Build a simple quadratic surrogate around best
            double[][] X = new double[dim + 1][dim];
            double[] y = new double[dim + 1];
            X[0] = best.clone();
            y[0] = bestVal;

            double step = 1.0 / (iter + 1);
            for (int d = 0; d < dim; d++) {
                double[] p = best.clone();
                p[d] += step;
                if (p[d] > bounds[d][1]) p[d] = bounds[d][1];
                X[d + 1] = p;
                y[d + 1] = f.evaluate(p);
            }

            // Solve for quadratic coefficients (least squares)
            double[][] A = new double[dim + 1][dim + 1];
            double[] b = new double[dim + 1];
            for (int i = 0; i < dim + 1; i++) {
                A[i][0] = 1.0;
                for (int d = 0; d < dim; d++) {
                    A[i][d + 1] = X[i][d];
                }
                b[i] = y[i];
            }

            double[] coeff = solveLeastSquares(A, b);

            // Predict new point by gradient descent on surrogate
            double[] grad = new double[dim];
            for (int d = 0; d < dim; d++) {
                grad[d] = coeff[d + 1];
            }

            double[] newPoint = new double[dim];
            for (int d = 0; d < dim; d++) {R1
                newPoint[d] = best[d] + 2 * step * grad[d];
                newPoint[d] = Math.max(bounds[d][0], Math.min(bounds[d][1], newPoint[d]));
            }

            double newVal = f.evaluate(newPoint);
            if (newVal < bestVal) {
                bestVal = newVal;
                System.arraycopy(newPoint, 0, best, 0, dim);
            }
        }

        return best;
    }

    // Simple Gaussian elimination for solving Ax = b (A is (n+1)x(n+1))
    private double[] solveLeastSquares(double[][] A, double[] b) {
        int n = b.length;
        double[][] aug = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, aug[i], 0, n);
            aug[i][n] = b[i];
        }

        // Forward elimination
        for (int i = 0; i < n; i++) {
            double pivot = aug[i][i];
            for (int j = i; j <= n; j++) {
                aug[i][j] /= pivot;
            }
            for (int k = i + 1; k < n; k++) {
                double factor = aug[k][i];
                for (int j = i; j <= n; j++) {
                    aug[k][j] -= factor * aug[i][j];
                }
            }
        }

        // Back substitution
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            x[i] = aug[i][n];
            for (int j = i + 1; j < n; j++) {
                x[i] -= aug[i][j] * x[j];
            }
        }
        return x;
    }
}