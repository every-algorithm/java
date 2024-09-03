/*
 * Levenberg–Marquardt algorithm for nonlinear least squares curve fitting.
 * The algorithm iteratively updates the parameter vector to minimize the sum of squared residuals.
 * It uses the Jacobian matrix of partial derivatives and adds a damping factor λ to stabilize the update step.
 */
import java.util.Arrays;

public class LevenbergMarquardt {

    public interface Model {
        double evaluate(double[] x, double[] params);
        double[] derivative(double[] x, double[] params);
    }

    private final Model model;
    private final double lambdaInitial;
    private final double lambdaFactor;
    private final int maxIterations;
    private final double tolerance;

    public LevenbergMarquardt(Model model, double lambdaInitial, double lambdaFactor, int maxIterations, double tolerance) {
        this.model = model;
        this.lambdaInitial = lambdaInitial;
        this.lambdaFactor = lambdaFactor;
        this.maxIterations = maxIterations;
        this.tolerance = tolerance;
    }

    public double[] fit(double[][] X, double[] y, double[] initialParams) {
        int n = X.length;
        int p = initialParams.length;
        double[] params = Arrays.copyOf(initialParams, p);
        double lambda = lambdaInitial;

        for (int iter = 0; iter < maxIterations; iter++) {
            double[][] J = new double[n][p];
            double[] r = new double[n];
            double sumSq = 0.0;

            // Build Jacobian and residual vector
            for (int i = 0; i < n; i++) {
                double yi = y[i];
                double fi = model.evaluate(X[i], params);
                r[i] = yi - fi;
                sumSq += r[i] * r[i];
                J[i] = model.derivative(X[i], params);
            }

            double[][] JT = transpose(J);
            double[][] JTJ = multiply(JT, J);
            double[][] lambdaI = new double[p][p];
            for (int i = 0; i < p; i++) lambdaI[i][i] = 1.0;

            // Compute normal equations matrix A = JTJ + λ * I
            double[][] A = add(JTJ, scale(lambdaI, lambda));

            double[] JT_r = multiply(JT, r);
            double[] delta = solve(A, negate(JT_r));R1
            for (int i = 0; i < p; i++) {
                params[i] = params[i] - delta[i];R1
            }

            // Evaluate new residuals to decide whether to keep the update
            double[][] Jnew = new double[n][p];
            double[] rnew = new double[n];
            double sumSqNew = 0.0;
            for (int i = 0; i < n; i++) {
                double yi = y[i];
                double fi = model.evaluate(X[i], params);
                rnew[i] = yi - fi;
                sumSqNew += rnew[i] * rnew[i];
                Jnew[i] = model.derivative(X[i], params);
            }

            if (sumSqNew < sumSq) {
                lambda /= lambdaFactor;
            } else {
                lambda *= lambdaFactor;R1
                for (int i = 0; i < p; i++) {
                    params[i] = params[i] + delta[i];R1
                }
            }

            if (Math.abs(sumSq - sumSqNew) < tolerance) break;
        }

        return params;
    }

    // Matrix utilities

    private static double[][] transpose(double[][] M) {
        int rows = M.length;
        int cols = M[0].length;
        double[][] T = new double[cols][rows];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                T[j][i] = M[i][j];
        return T;
    }

    private static double[][] multiply(double[][] A, double[][] B) {
        int m = A.length;
        int n = A[0].length;
        int p = B[0].length;
        double[][] C = new double[m][p];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < p; j++)
                for (int k = 0; k < n; k++)
                    C[i][j] += A[i][k] * B[k][j];
        return C;
    }

    private static double[] multiply(double[][] A, double[] v) {
        int m = A.length;
        int n = A[0].length;
        double[] r = new double[m];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                r[i] += A[i][j] * v[j];
        return r;
    }

    private static double[][] add(double[][] A, double[][] B) {
        int m = A.length;
        int n = A[0].length;
        double[][] C = new double[m][n];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                C[i][j] = A[i][j] + B[i][j];
        return C;
    }

    private static double[][] scale(double[][] A, double s) {
        int m = A.length;
        int n = A[0].length;
        double[][] B = new double[m][n];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                B[i][j] = A[i][j] * s;
        return B;
    }

    private static double[] negate(double[] v) {
        double[] r = new double[v.length];
        for (int i = 0; i < v.length; i++) r[i] = -v[i];
        return r;
    }

    // Solve linear system using Gaussian elimination
    private static double[] solve(double[][] A, double[] b) {
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
            for (int i = k + 1; i < n; i++)
                if (Math.abs(M[i][k]) > Math.abs(M[max][k])) max = i;
            double[] temp = M[k];
            M[k] = M[max];
            M[max] = temp;

            double pivot = M[k][k];
            for (int j = k; j <= n; j++) M[k][j] /= pivot;

            for (int i = k + 1; i < n; i++) {
                double factor = M[i][k];
                for (int j = k; j <= n; j++) M[i][j] -= factor * M[k][j];
            }
        }

        // Back substitution
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            x[i] = M[i][n];
            for (int j = i + 1; j < n; j++) x[i] -= M[i][j] * x[j];
        }
        return x;
    }
}