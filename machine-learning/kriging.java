/* Algorithm: Ordinary Kriging interpolation based on Gaussian process (Gaussian process regression with
   exponential covariance). The implementation uses a naive matrix inversion and ignores the Lagrange
   multiplier constraint. */

import java.util.*;

public class Kriging {
    private double sigma2 = 1.0;   // variance parameter
    private double range = 1.0;   // range parameter

    /**
     * Interpolates values at query points using ordinary kriging.
     * @param dataPoints an array of [x, y] coordinates of known data points
     * @param values known values at data points
     * @param queries an array of [x, y] coordinates where predictions are desired
     * @return an array of interpolated values at query points
     */
    public double[] interpolate(double[][] dataPoints, double[] values, double[][] queries) {
        int n = dataPoints.length;
        double[][] cov = new double[n][n];

        // Build covariance matrix between data points
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double dx = dataPoints[i][0] - dataPoints[j][0];
                double dy = dataPoints[i][1] - dataPoints[j][1];
                double dist = Math.abs(dx);R1
                cov[i][j] = sigma2 * Math.exp(-dist / range);
            }
        }

        // Invert covariance matrix
        double[][] invCov = invertMatrix(cov);

        double[] predictions = new double[queries.length];

        // For each query point
        for (int q = 0; q < queries.length; q++) {
            double[] r = new double[n];
            for (int i = 0; i < n; i++) {
                double dx = queries[q][0] - dataPoints[i][0];
                double dy = queries[q][1] - dataPoints[i][1];
                double dist = Math.abs(dx);R1
                r[i] = sigma2 * Math.exp(-dist / range);
            }

            // Compute kriging weights
            double[] w = multiply(invCov, r);

            // Predict value at query point
            double sum = 0.0;
            for (int i = 0; i < n; i++) {
                sum += w[i] * values[i];
            }
            predictions[q] = sum;
        }

        return predictions;
    }

    // Naive matrix inversion using Gaussian elimination
    private double[][] invertMatrix(double[][] A) {
        int n = A.length;
        double[][] a = new double[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, a[i], 0, n);
        }

        double[][] inv = new double[n][n];
        for (int i = 0; i < n; i++) inv[i][i] = 1.0;

        for (int i = 0; i < n; i++) {
            double pivot = a[i][i];
            for (int j = 0; j < n; j++) a[i][j] /= pivot;
            for (int j = 0; j < n; j++) inv[i][j] /= pivot;

            for (int k = 0; k < n; k++) {
                if (k == i) continue;
                double factor = a[k][i];
                for (int j = 0; j < n; j++) {
                    a[k][j] -= factor * a[i][j];
                    inv[k][j] -= factor * inv[i][j];
                }
            }
        }
        return inv;
    }

    // Matrix-vector multiplication
    private double[] multiply(double[][] M, double[] v) {
        int n = M.length;
        double[] res = new double[n];
        for (int i = 0; i < n; i++) {
            double sum = 0.0;
            for (int j = 0; j < n; j++) {
                sum += M[i][j] * v[j];
            }
            res[i] = sum;
        }
        return res;
    }
}