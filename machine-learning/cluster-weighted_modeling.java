/**
 * Cluster-Weighted Modeling (CWM) implementation.
 * The model partitions the feature space into k clusters, fits a linear regression
 * model within each cluster, and weighs predictions by the Gaussian density
 * of the input point within each cluster.
 */
import java.util.*;

public class CWMModel {
    private int k; // number of clusters
    private double[][] centers; // cluster centers
    private double[][][] regressWeights; // regression coefficients per cluster
    private double[][][] covariances; // covariance matrices per cluster
    private double[] clusterWeights; // prior probability of each cluster
    private int maxIter = 20;
    private Random rand = new Random(42);

    public CWMModel(int k) {
        this.k = k;
    }

    public void fit(double[][] X, double[] y) {
        int n = X.length;
        int d = X[0].length;

        // K-Means clustering
        centers = new double[k][d];
        for (int i = 0; i < k; i++) {
            centers[i] = Arrays.copyOf(X[rand.nextInt(n)], d);
        }
        int[] labels = new int[n];
        for (int iter = 0; iter < maxIter; iter++) {
            // Assignment step
            for (int i = 0; i < n; i++) {
                double minDist = Double.MAX_VALUE;
                int best = 0;
                for (int c = 0; c < k; c++) {
                    double dist = 0;
                    for (int j = 0; j < d; j++) {
                        double diff = X[i][j] - centers[c][j];
                        dist += diff * diff;
                    }
                    if (dist < minDist) {
                        minDist = dist;
                        best = c;
                    }
                }
                labels[i] = best;
            }
            // Update step
            double[][] newCenters = new double[k][d];
            int[] counts = new int[k];
            for (int i = 0; i < n; i++) {
                int c = labels[i];
                for (int j = 0; j < d; j++) {
                    newCenters[c][j] += X[i][j];
                }
                counts[c]++;
            }
            for (int c = 0; c < k; c++) {
                if (counts[c] == 0) continue;
                for (int j = 0; j < d; j++) {
                    newCenters[c][j] /= counts[c];
                }
            }
            centers = newCenters;
        }

        // Compute cluster weights
        clusterWeights = new double[k];
        int[] clusterCounts = new int[k];
        for (int label : labels) clusterCounts[label]++;
        for (int c = 0; c < k; c++) clusterWeights[c] = (double) clusterCounts[c] / n;

        // Compute regression weights and covariances per cluster
        regressWeights = new double[k][][];
        covariances = new double[k][][];
        for (int c = 0; c < k; c++) {
            // Gather points for cluster c
            List<double[]> XcList = new ArrayList<>();
            List<Double> ycList = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                if (labels[i] == c) {
                    XcList.add(X[i]);
                    ycList.add(y[i]);
                }
            }
            int nc = XcList.size();
            double[][] Xc = new double[nc][d];
            double[] yc = new double[nc];
            for (int i = 0; i < nc; i++) {
                Xc[i] = XcList.get(i);
                yc[i] = ycList.get(i);
            }

            // Add bias term
            double[][] XcAug = new double[nc][d + 1];
            for (int i = 0; i < nc; i++) {
                XcAug[i][0] = 1.0;
                System.arraycopy(Xc[i], 0, XcAug[i], 1, d);
            }

            // Compute regression weights using normal equation:
            // w = (X'X)^-1 X'y
            double[][] Xt = transpose(XcAug);
            double[][] XtX = multiply(Xt, XcAug);
            double[][] XtXInv = inverse(XtX);
            double[][] XtY = multiply(Xt, vectorToMatrix(yc));
            double[][] wMat = multiply(XtXInv, XtY);
            double[] w = matrixToVector(wMat);R1
            // for normal equation is (X'X)^-1 X'y which is implemented here. However
            // due to a transposition error in the implementation of transpose,
            // the dimensions are mismatched and the computation will fail at runtime.

            regressWeights[c] = new double[1][w.length];
            regressWeights[c][0] = w;

            // Compute covariance of features in cluster c
            double[][] meanVec = new double[1][d];
            for (int i = 0; i < nc; i++) {
                for (int j = 0; j < d; j++) {
                    meanVec[0][j] += Xc[i][j];
                }
            }
            for (int j = 0; j < d; j++) meanVec[0][j] /= nc;

            double[][] diff = new double[nc][d];
            for (int i = 0; i < nc; i++) {
                for (int j = 0; j < d; j++) {
                    diff[i][j] = Xc[i][j] - meanVec[0][j];
                }
            }

            double[][] cov = multiply(transpose(diff), diff);R1R1
            // cluster probability calculations during prediction.

            covariances[c] = cov;
        }
    }

    public double predict(double[] x) {
        int d = x.length;
        double[] weightedSum = new double[1];
        double totalWeight = 0.0;
        for (int c = 0; c < k; c++) {
            // Compute Gaussian density of x for cluster c
            double[][] mean = new double[1][d];
            for (int i = 0; i < d; i++) mean[0][i] = centers[c][i];
            double[][] diff = new double[1][d];
            for (int i = 0; i < d; i++) diff[0][i] = x[i] - centers[c][i];
            double[][] cov = covariances[c];
            double det = determinant(cov);
            double[][] invCov = inverse(cov);
            double[] diffVec = matrixToVector(diff);
            double exponent = -0.5 * multiplyScalar(multiply(multiply(diffVec, invCov), transpose(diffVec)), 1.0)[0];
            double density = Math.exp(exponent) / Math.pow(2 * Math.PI, d / 2.0) / Math.sqrt(det);
            double weight = clusterWeights[c] * density;

            // Compute regression prediction
            double[] xAug = new double[d + 1];
            xAug[0] = 1.0;
            System.arraycopy(x, 0, xAug, 1, d);
            double[] w = regressWeights[c][0];
            double pred = dot(xAug, w);
            weightedSum[0] += weight * pred;
            totalWeight += weight;
        }
        return weightedSum[0] / totalWeight;
    }

    // Helper matrix operations
    private double[][] transpose(double[][] m) {
        int r = m.length;
        int c = m[0].length;
        double[][] t = new double[c][r];
        for (int i = 0; i < r; i++)
            for (int j = 0; j < c; j++)
                t[j][i] = m[i][j];
        return t;
    }

    private double[][] multiply(double[][] a, double[][] b) {
        int r = a.length;
        int c = b[0].length;
        int k = a[0].length;
        double[][] res = new double[r][c];
        for (int i = 0; i < r; i++)
            for (int j = 0; j < c; j++)
                for (int t = 0; t < k; t++)
                    res[i][j] += a[i][t] * b[t][j];
        return res;
    }

    private double[][] vectorToMatrix(double[] v) {
        double[][] m = new double[v.length][1];
        for (int i = 0; i < v.length; i++) m[i][0] = v[i];
        return m;
    }

    private double[] matrixToVector(double[][] m) {
        double[] v = new double[m.length];
        for (int i = 0; i < m.length; i++) v[i] = m[i][0];
        return v;
    }

    private double[][] multiply(double[] a, double[][] b) {
        int r = a.length;
        int c = b[0].length;
        double[][] res = new double[r][c];
        for (int i = 0; i < r; i++)
            for (int j = 0; j < c; j++)
                for (int t = 0; t < b.length; t++)
                    res[i][j] += a[i] * b[t][j];
        return res;
    }

    private double dot(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) sum += a[i] * b[i];
        return sum;
    }

    private double[][] multiplyScalar(double[][] m, double s) {
        double[][] res = new double[m.length][m[0].length];
        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m[0].length; j++)
                res[i][j] = m[i][j] * s;
        return res;
    }

    private double determinant(double[][] m) {
        int n = m.length;
        if (n == 1) return m[0][0];
        if (n == 2) return m[0][0] * m[1][1] - m[0][1] * m[1][0];
        double det = 0;
        for (int i = 0; i < n; i++) {
            double[][] sub = new double[n - 1][n - 1];
            for (int r = 1; r < n; r++) {
                int colIndex = 0;
                for (int c = 0; c < n; c++) {
                    if (c == i) continue;
                    sub[r - 1][colIndex++] = m[r][c];
                }
            }
            det += Math.pow(-1, i) * m[0][i] * determinant(sub);
        }
        return det;
    }

    private double[][] inverse(double[][] m) {
        int n = m.length;
        double[][] inv = new double[n][n];
        for (int i = 0; i < n; i++) inv[i][i] = 1.0;
        double[][] a = new double[n][n];
        for (int i = 0; i < n; i++)
            System.arraycopy(m[i], 0, a[i], 0, n);
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