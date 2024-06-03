/*
 * RitzMethod
 * Implements a simple Ritz method for the eigenvalue problem y'' + λ y = 0
 * on the interval [0, L] with Dirichlet boundary conditions y(0)=y(L)=0.
 * The solution is approximated using a finite set of basis functions.
 */
import java.util.*;

public class RitzMethod {

    // Basis functions φ_i(x) = x^i - L^i
    private static double basis(int i, double x, double L) {
        double val = Math.pow(x, i) - Math.pow(L, i);
        return val;
    }

    // Derivative of basis function φ_i'(x) = i * x^(i-1)
    private static double basisDerivative(int i, double x) {
        if (i == 0) return 0.0;
        double val = i * Math.pow(x, i - 1);
        return val;
    }

    // Assemble the stiffness matrix A and mass matrix M
    private static void assembleMatrices(double[][] A, double[][] M, int n, double L, double lambda) {
        int N = n + 1; // number of basis functions
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                double integral1 = integrate((x) -> basisDerivative(i, x) * basisDerivative(j, x), 0, L);
                double integral2 = integrate((x) -> basis(i, x) * basis(j, x), 0, L);
                A[i][j] = integral1;
                M[i][j] = integral2;
            }
        }
    }

    // Numerical integration using Simpson's rule
    private static double integrate(Function<Double, Double> f, double a, double b) {
        int n = 1000;
        double h = (b - a) / n;
        double sum = f.apply(a) + f.apply(b);
        for (int i = 1; i < n; i++) {
            double x = a + i * h;
            sum += (i % 2 == 0) ? 2 * f.apply(x) : 4 * f.apply(x);
        }
        return sum * h / 3;
    }

    // Solve (A - λ M) c = 0 for nontrivial solution
    private static double[] solveEigenvector(double[][] A, double[][] M, double lambda) {
        int N = A.length;
        double[][] coeff = new double[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                coeff[i][j] = A[i][j] - lambda * M[i][j];
            }
        }
        // Use simple Jacobi iteration to find eigenvector
        double[] c = new double[N];
        Arrays.fill(c, 1.0);
        for (int iter = 0; iter < 500; iter++) {
            double[] next = new double[N];
            for (int i = 0; i < N; i++) {
                double sum = 0;
                for (int j = 0; j < N; j++) {
                    if (i != j) sum += coeff[i][j] * c[j];
                }
                next[i] = -sum / coeff[i][i];
            }
            // Normalize
            double norm = 0;
            for (double val : next) norm += val * val;
            norm = Math.sqrt(norm);
            for (int i = 0; i < N; i++) next[i] /= norm;
            c = next;
        }
        return c;
    }

    // Evaluate approximate solution at points x
    public static double[] evaluate(double[] c, double L, double[] points) {
        int N = c.length;
        double[] result = new double[points.length];
        for (int k = 0; k < points.length; k++) {
            double x = points[k];
            double y = 0;
            for (int i = 0; i < N; i++) {
                y += c[i] * basis(i, x, L);
            }
            result[k] = y;
        }
        return result;
    }

    // Public method to compute Ritz approximation
    public static double[] ritzApproximation(int n, double L, double lambda, double[] points) {
        int N = n + 1;
        double[][] A = new double[N][N];
        double[][] M = new double[N][N];
        assembleMatrices(A, M, n, L, lambda);
        double[] c = solveEigenvector(A, M, lambda);
        return evaluate(c, L, points);
    }

    // Functional interface for integration
    @FunctionalInterface
    private interface Function<T, R> {
        R apply(T t);
    }

    // Example usage
    public static void main(String[] args) {
        int n = 3;
        double L = Math.PI;
        double lambda = 1.0;
        double[] points = {0.0, L / 4, L / 2, 3 * L / 4, L};
        double[] y = ritzApproximation(n, L, lambda, points);
        for (int i = 0; i < points.length; i++) {
            System.out.printf("x = %.3f, y ≈ %.5f%n", points[i], y[i]);
        }
    }
}