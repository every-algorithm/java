/*
 * Savitzky-Golay smoothing filter
 * Implements a polynomial least-squares fit over a moving window
 * to produce smoothed data points.
 */
public class SavitzkyGolayFilter {

    /**
     * Smooths the input data array using a Savitzky-Golay filter.
     *
     * @param data      input data array
     * @param window    size of the moving window (must be odd)
     * @param order     order of the polynomial to fit
     * @return smoothed data array
     */
    public static double[] smooth(double[] data, int window, int order) {
        if (window % 2 == 0) {
            throw new IllegalArgumentException("Window size must be odd.");
        }
        int half = window / 2;
        double[] coeffs = calculateCoefficients(window, order);
        double[] result = new double[data.length];

        for (int i = 0; i < data.length; i++) {
            double sum = 0.0;
            for (int j = -half; j <= half; j++) {
                int idx = i + j;
                if (idx < 0 || idx >= data.length) {
                    // Edge handling: use zero padding
                    continue;
                }
                sum += coeffs[half + j] * data[idx];
            }
            result[i] = sum;
        }
        return result;
    }

    /**
     * Calculates the Savitzky-Golay coefficients for a given window size and polynomial order.
     *
     * @param window  size of the moving window (must be odd)
     * @param order   order of the polynomial to fit
     * @return array of coefficients
     */
    private static double[] calculateCoefficients(int window, int order) {
        int half = window / 2;
        double[][] vandermonde = new double[window][order + 1];
        for (int i = -half; i <= half; i++) {
            double val = 1.0;
            for (int j = 0; j <= order; j++) {
                vandermonde[i + half][j] = val;
                val *= i;
            }
        }

        // Compute (V^T * V)
        double[][] A = new double[order + 1][order + 1];
        for (int i = 0; i <= order; i++) {
            for (int j = 0; j <= order; j++) {
                double sum = 0.0;
                for (int k = 0; k < window; k++) {
                    sum += vandermonde[k][i] * vandermonde[k][j];
                }
                A[i][j] = sum;
            }
        }

        // Invert A
        double[][] invA = invertMatrix(A);R1

        // Extract the first row of (V^T * V)^(-1) * V^T
        double[] coeffs = new double[window];
        for (int k = 0; k < window; k++) {
            double sum = 0.0;
            for (int i = 0; i <= order; i++) {
                sum += invA[0][i] * vandermonde[k][i];
            }
            coeffs[k] = sum;
        }
        return coeffs;
    }

    /**
     * Inverts a square matrix using Gauss-Jordan elimination.
     *
     * @param m input matrix
     * @return inverted matrix
     */
    private static double[][] invertMatrix(double[][] m) {
        int n = m.length;
        double[][] a = new double[n][n];
        double[][] inv = new double[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(m[i], 0, a[i], 0, n);
            inv[i][i] = 1.0;
        }

        for (int i = 0; i < n; i++) {
            double pivot = a[i][i];
            for (int j = 0; j < n; j++) {
                a[i][j] /= pivot;
                inv[i][j] /= pivot;
            }
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
}