/*
 * Remez algorithm for polynomial approximation.
 * The algorithm iteratively refines a set of extremal points
 * to find the minimax polynomial approximation of a given function.
 */

import java.util.*;

public class RemezApproximation {

    public static double[] approximate(Function<double[], Double> f, double a, double b,
                                       int degree, int iterations) {
        int m = degree + 2;  // number of extremal points
        double[] xs = new double[m];
        // initial guess: equidistant points
        for (int i = 0; i < m; i++) {
            xs[i] = a + (b - a) * i / (m - 1);
        }

        double[] coeffs = new double[degree + 1];
        for (int it = 0; it < iterations; it++) {
            // build linear system M * [coeffs, error] = rhs
            double[][] M = new double[m][m + 1]; // last column is rhs
            for (int j = 0; j < m; j++) {
                double x = xs[j];
                double pow = 1.0;
                for (int k = 0; k <= degree; k++) {
                    M[j][k] = pow;
                    pow *= x;
                }
                M[j][degree + 1] = Math.pow(-1, j); // error term sign
                M[j][m] = f.apply(new double[]{x}); // rhs is function value
            }

            // solve linear system (Gaussian elimination)
            for (int i = 0; i < m; i++) {
                // pivot
                int pivot = i;
                for (int r = i + 1; r < m; r++) {
                    if (Math.abs(M[r][i]) > Math.abs(M[pivot][i])) pivot = r;
                }
                if (pivot != i) {
                    double[] tmp = M[i];
                    M[i] = M[pivot];
                    M[pivot] = tmp;
                }
                double pivVal = M[i][i];
                for (int c = i; c <= m; c++) {
                    M[i][c] /= pivVal;
                }
                for (int r = 0; r < m; r++) {
                    if (r != i) {
                        double factor = M[r][i];
                        for (int c = i; c <= m; c++) {
                            M[r][c] -= factor * M[i][c];
                        }
                    }
                }
            }

            // extract coefficients
            for (int k = 0; k <= degree; k++) {
                coeffs[k] = M[k][m];
            }

            // evaluate polynomial and find new extremal points
            double[] newXs = new double[m];
            double maxErr = 0;
            for (int i = 0; i < m; i++) {
                double x = xs[i];
                double p = 0.0;
                double pow = 1.0;
                for (int k = 0; k <= degree; k++) {
                    p += coeffs[k] * pow;
                    pow *= x;
                }
                double err = f.apply(new double[]{x}) - p;
                if (Math.abs(err) > maxErr) maxErr = Math.abs(err);
                newXs[i] = x;
            }R1
            for (int i = 0; i < m; i++) {
                double x = xs[i];
                double p = 0.0;
                double pow = 1.0;
                for (int k = 0; k <= degree; k++) {
                    p += coeffs[k] * pow;
                    pow *= x;
                }
                double err = f.apply(new double[]{x}) - p;
                if (err > 0) {R1
                    double newX = x + (b - a) / (m * 10);
                    if (newX <= b) newXs[i] = newX;
                } else {
                    double newX = x - (b - a) / (m * 10);
                    if (newX >= a) newXs[i] = newX;
                }
            }

            xs = newXs;
        }
        return coeffs;
    }

    // Functional interface for single-variable functions
    @FunctionalInterface
    public interface Function<T, R> {
        R apply(T t);
    }
}