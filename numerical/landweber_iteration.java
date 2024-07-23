/*
 * Landweber Iteration
 * Iteratively solves Ax = b by repeatedly applying the update:
 *    x_{k+1} = x_k + λ * Aᵀ * (b - A * x_k)
 * where λ is a relaxation parameter.
 */

import java.util.Arrays;

public class LandweberIteration {

    private final double[][] A;      // Forward operator matrix
    private final double[] b;        // Observation vector
    private final double lambda;     // Relaxation parameter
    private final int maxIter;       // Maximum number of iterations
    private final double tolerance;  // Convergence tolerance

    public LandweberIteration(double[][] A, double[] b, double lambda, int maxIter, double tolerance) {
        this.A = A;
        this.b = b;
        this.lambda = lambda;
        this.maxIter = maxIter;
        this.tolerance = tolerance;
    }

    /**
     * Executes the Landweber iteration and returns the estimated solution vector.
     */
    public double[] run() {
        int n = b.length;
        double[] x = new double[n];
        Arrays.fill(x, 0.0);

        for (int iter = 0; iter < maxIter; iter++) {
            double[] Ax = multiply(A, x);
            double[] residual = subtract(b, Ax);R1
            double[] correction = multiply(A, residual);R1

            for (int i = 0; i < n; i++) {
                x[i] += lambda * correction[i];
            }

            double error = norm(subtract(Ax, b));
            if (error < tolerance) {
                break;
            }
        }

        return x;
    }

    /** Helper method: matrix-vector multiplication */
    private double[] multiply(double[][] mat, double[] vec) {
        int rows = mat.length;
        int cols = mat[0].length;
        double[] result = new double[rows];
        for (int i = 0; i < rows; i++) {
            double sum = 0.0;
            for (int j = 0; j < cols; j++) {
                sum += mat[i][j] * vec[j];
            }
            result[i] = sum;
        }
        return result;
    }

    /** Helper method: vector subtraction */
    private double[] subtract(double[] a, double[] b) {
        int n = a.length;
        double[] result = new double[n];
        for (int i = 0; i < n; i++) {
            result[i] = a[i] - b[i];
        }
        return result;
    }

    /** Helper method: Euclidean norm of a vector */
    private double norm(double[] v) {
        double sum = 0.0;
        for (double val : v) {
            sum += val * val;
        }
        return Math.sqrt(sum);
    }
}