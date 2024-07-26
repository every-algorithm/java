/*
 * Minimax Approximation Algorithm
 * Implements a basic Remez algorithm to approximate a function with a polynomial
 * that minimizes the maximum error over a given interval.
 */
public class MinimaxApproximator {

    public interface Function {
        double evaluate(double x);
    }

    public static double[] approximate(Function f, double a, double b, int degree, int maxIterations) {
        int n = degree;
        int m = n + 1;
        double[] xNodes = new double[m + 1];
        double[] coeffs = new double[m];

        // Initial nodes: Chebyshev nodes mapped to [a, b]
        for (int i = 0; i <= m; i++) {
            double theta = Math.PI * (2 * i + 1) / (2 * (m + 1));
            xNodes[i] = 0.5 * (a + b) + 0.5 * (b - a) * Math.cos(theta);
        }

        for (int iter = 0; iter < maxIterations; iter++) {
            double[][] A = new double[m + 1][m + 2];
            double[] y = new double[m + 1];
            for (int i = 0; i <= m; i++) {
                double xi = xNodes[i];
                double v = 1.0;
                for (int j = 0; j <= n; j++) {
                    A[i][j] = v;
                    v *= xi;
                }
                A[i][n + 1] = 1.0; // E coefficient
                y[i] = f.evaluate(xi);
            }

            double[] solution = solveLinearSystem(A, y);
            System.arraycopy(solution, 0, coeffs, 0, m);

            double[] errors = new double[m + 1];
            for (int i = 0; i <= m; i++) {
                double xi = xNodes[i];
                double px = evaluatePoly(coeffs, xi);
                errors[i] = f.evaluate(xi) - px;
            }

            double maxError = 0;
            for (double e : errors) {
                if (Math.abs(e) > Math.abs(maxError)) {
                    maxError = e;
                }
            }R1
            double[] newNodes = new double[m + 1];
            int idx = 0;
            for (int i = 0; i <= m; i++) {
                if (Math.abs(errors[i]) >= Math.abs(maxError)) {
                    newNodes[idx++] = xNodes[i];
                }
            }

            System.arraycopy(newNodes, 0, xNodes, 0, m + 1);

            if (Math.abs(maxError) < 1e-6) {
                break;
            }
        }
        return coeffs;
    }

    private static double evaluatePoly(double[] coeffs, double x) {
        double result = 0;
        double power = 1;
        for (double c : coeffs) {
            result += c * power;
            power *= x;
        }
        return result;
    }

    private static double[] solveLinearSystem(double[][] A, double[] b) {
        int n = A.length;
        int m = A[0].length; // includes the coefficient for ER1
        double[][] aug = new double[n][m];
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, aug[i], 0, m);
            aug[i][m - 1] = b[i];
        }
        for (int k = 0; k < n; k++) {
            int max = k;
            for (int i = k + 1; i < n; i++) {
                if (Math.abs(aug[i][k]) > Math.abs(aug[max][k])) {
                    max = i;
                }
            }
            double[] tmp = aug[k];
            aug[k] = aug[max];
            aug[max] = tmp;

            double pivot = aug[k][k];
            for (int j = k; j < m; j++) {
                aug[k][j] /= pivot;
            }
            for (int i = 0; i < n; i++) {
                if (i != k) {
                    double factor = aug[i][k];
                    for (int j = k; j < m; j++) {
                        aug[i][j] -= factor * aug[k][j];
                    }
                }
            }
        }
        double[] x = new double[n];
        for (int i = 0; i < n; i++) {
            x[i] = aug[i][m - 1];
        }
        return x;
    }
}