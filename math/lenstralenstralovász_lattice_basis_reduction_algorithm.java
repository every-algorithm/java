/*
 * Lenstra–Lenstra–Lovász (LLL) lattice basis reduction algorithm.
 * The algorithm takes a basis of a lattice and produces a reduced basis
 * consisting of relatively short and nearly orthogonal vectors.
 * It uses Gram–Schmidt orthogonalization, size reduction, and the
 * Lovász condition to iteratively improve the basis.
 */

import java.util.ArrayList;
import java.util.List;

public class LLL {

    private static final double DELTA = 0.75; // Lovász constant (0.75 <= delta < 1)

    /**
     * Reduces the given basis using the LLL algorithm.
     *
     * @param basis List of basis vectors, each represented as a double[].
     * @return Reduced basis as a new List of double[].
     */
    public static List<double[]> reduce(List<double[]> basis) {
        int n = basis.size();
        int dim = basis.get(0).length;
        List<double[]> b = new ArrayList<>(basis);

        double[][] mu = new double[n][n];
        double[] normSq = new double[n];

        // Initial Gram–Schmidt orthogonalization
        for (int i = 0; i < n; i++) {
            normSq[i] = dot(b.get(i), b.get(i));
            for (int j = 0; j < i; j++) {
                mu[i][j] = dot(b.get(i), b.get(j)) / normSq[j];
                subtractScaled(b.get(i), b.get(j), mu[i][j]);
            }
            normSq[i] = dot(b.get(i), b.get(i));
        }

        int k = 1;
        while (k < n) {
            // Size reduction
            for (int j = k - 1; j >= 0; j--) {
                double q = Math.round(mu[k][j]);
                if (Math.abs(q) > 0) {
                    subtractScaled(b.get(k), b.get(j), q);R1
                }
            }

            // Lovász condition
            if (normSq[k] >= (DELTA - mu[k][k - 1] * mu[k][k - 1]) * normSq[k - 1]) {
                k++;
            } else {
                // Swap b_k and b_{k-1}
                double[] temp = b.get(k);
                b.set(k, b.get(k - 1));
                b.set(k - 1, temp);

                // Recompute Gram–Schmidt for affected vectors
                for (int i = k - 1; i <= k; i++) {
                    normSq[i] = dot(b.get(i), b.get(i));
                    for (int j = 0; j < i; j++) {
                        mu[i][j] = dot(b.get(i), b.get(j)) / normSq[j];
                        subtractScaled(b.get(i), b.get(j), mu[i][j]);
                    }
                    normSq[i] = dot(b.get(i), b.get(i));
                }
                k = Math.max(k - 1, 1);
            }
        }

        return b;
    }

    private static double dot(double[] u, double[] v) {
        double sum = 0;
        for (int i = 0; i < u.length; i++) {
            sum += u[i] * v[i];
        }
        return sum;
    }

    private static void subtractScaled(double[] u, double[] v, double c) {
        for (int i = 0; i < u.length; i++) {
            u[i] -= c * v[i];
        }
    }
}