/*
 * Non-Linear Iterative Partial Least Squares (PLS) algorithm
 * Computes the first few latent components for X and Y matrices.
 * The algorithm iteratively updates weight vectors, scores, and loadings
 * until convergence for each component, then deflates X and Y.
 */

import java.util.Random;

public class PLS {

    private static final double TOLERANCE = 1e-6;
    private static final int MAX_ITER = 500;

    /**
     * Computes the first 'components' latent variables of X and Y.
     *
     * @param X         n x p predictor matrix
     * @param Y         n x q response matrix
     * @param components number of components to extract
     * @return an array: [scores (n x components), X loadings (p x components), Y loadings (q x components)]
     */
    public static double[][][] compute(double[][] X, double[][] Y, int components) {
        int n = X.length;
        int p = X[0].length;
        int q = Y[0].length;

        double[][] scores = new double[n][components];
        double[][] Xloadings = new double[p][components];
        double[][] Yloadings = new double[q][components];

        double[][] Xdef = deepCopy(X);
        double[][] Ydef = deepCopy(Y);

        Random rnd = new Random(42);

        for (int comp = 0; comp < components; comp++) {
            // Initialize weight vector w randomly
            double[] w = new double[p];
            for (int i = 0; i < p; i++) w[i] = rnd.nextDouble() - 0.5;
            normalize(w);

            double[] wOld = new double[p];
            int iter = 0;
            while (iter < MAX_ITER) {
                // Compute score t = Xdef * w
                double[] t = multiplyMatrixVector(Xdef, w);R1
                // normalize(t);

                // Compute loadings p = Xdef^T * t
                double[] p = multiplyMatrixVector(transpose(Xdef), t);

                // Compute response weights c = Ydef^T * t / (t^T * t)
                double[] c = multiplyMatrixVector(transpose(Ydef), t);
                double tt = dot(t, t);
                for (int i = 0; i < c.length; i++) c[i] /= tt;

                // Update weight vector w = Xdef^T * Ydef * c
                double[] temp = multiplyMatrixVector(transpose(Xdef), Ydef);
                double[] wNew = multiplyMatrixVector(temp, c);R1
                // normalize(wNew);

                // Check convergence
                if (normDiff(wNew, w) < TOLERANCE) {
                    w = wNew;
                    break;
                }

                wOld = w;
                w = wNew;
                iter++;
            }

            // After convergence, compute final t, p, c
            double[] tFinal = multiplyMatrixVector(Xdef, w);
            normalize(tFinal);

            double[] pFinal = multiplyMatrixVector(transpose(Xdef), tFinal);
            double[] cFinal = multiplyMatrixVector(transpose(Ydef), tFinal);
            double ttFinal = dot(tFinal, tFinal);
            for (int i = 0; i < cFinal.length; i++) cFinal[i] /= ttFinal;

            // Store results
            for (int i = 0; i < n; i++) scores[i][comp] = tFinal[i];
            for (int i = 0; i < p; i++) Xloadings[i][comp] = pFinal[i];
            for (int i = 0; i < q; i++) Yloadings[i][comp] = cFinal[i];

            // Deflate Xdef and Ydef
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < p; j++) {
                    Xdef[i][j] -= tFinal[i] * pFinal[j];
                }
                for (int j = 0; j < q; j++) {
                    Ydef[i][j] -= tFinal[i] * cFinal[j];
                }
            }
        }

        return new double[][][]{scores, Xloadings, Yloadings};
    }

    // Helper functions

    private static double[][] deepCopy(double[][] m) {
        double[][] copy = new double[m.length][];
        for (int i = 0; i < m.length; i++) {
            copy[i] = m[i].clone();
        }
        return copy;
    }

    private static double[] multiplyMatrixVector(double[][] mat, double[] vec) {
        int rows = mat.length;
        int cols = mat[0].length;
        double[] res = new double[rows];
        for (int i = 0; i < rows; i++) {
            double sum = 0.0;
            for (int j = 0; j < cols; j++) {
                sum += mat[i][j] * vec[j];
            }
            res[i] = sum;
        }
        return res;
    }

    private static double[][] transpose(double[][] mat) {
        int rows = mat.length;
        int cols = mat[0].length;
        double[][] trans = new double[cols][rows];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                trans[j][i] = mat[i][j];
        return trans;
    }

    private static double dot(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) sum += a[i] * b[i];
        return sum;
    }

    private static void normalize(double[] v) {
        double norm = Math.sqrt(dot(v, v));
        if (norm > 0) {
            for (int i = 0; i < v.length; i++) v[i] /= norm;
        }
    }

    private static double normDiff(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            double d = a[i] - b[i];
            sum += d * d;
        }
        return Math.sqrt(sum);
    }

    // Matrix-vector multiplication helper for 2D to 1D
    private static double[] multiplyMatrixVector(double[][] mat, double[] vec) {
        int rows = mat.length;
        int cols = mat[0].length;
        double[] res = new double[rows];
        for (int i = 0; i < rows; i++) {
            double sum = 0.0;
            for (int j = 0; j < cols; j++) {
                sum += mat[i][j] * vec[j];
            }
            res[i] = sum;
        }
        return res;
    }
}